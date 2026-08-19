package com.digicart.catalog.service;

import com.digicart.catalog.dto.ProductCreateRequest;
import com.digicart.catalog.dto.ProductUpdateRequest;
import com.digicart.catalog.dto.StockDeductRequest;
import com.digicart.catalog.entity.Category;
import com.digicart.catalog.entity.Product;
import com.digicart.catalog.repository.CategoryRepository;
import com.digicart.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Application service implementing product use cases for <em>catalog-service</em>.
 */
@Service
public class ProductService {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RestClient restClient;

    @Value("${platform.service.url}")
    private String platformServiceUrl;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this(productRepository, categoryRepository, RestClient.create());
    }

    ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, RestClient restClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.restClient = restClient;
    }

    public Map<String, Object> findAll(String storeId, String search, String tag,
                                       String categoryParam, String sort, int page, int limit) {
        Set<UUID> categoryIds = resolveCategoryIds(storeId, categoryParam);

        Sort sorting = resolveSort(sort);
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sorting);

        List<Product> products = productRepository.findFiltered(storeId, search, categoryIds, pageRequest);
        long total = productRepository.countFiltered(storeId, search, categoryIds);

        // In-memory tag filter (tags stored as JSON array)
        if (tag != null && !tag.isBlank()) {
            String finalTag = tag;
            products = products.stream()
                .filter(p -> p.getTags().contains(finalTag))
                .collect(Collectors.toList());
        }

        return Map.of("products", products, "total", total, "page", page, "limit", limit);
    }

    private Set<UUID> resolveCategoryIds(String storeId, String categoryParam) {
        if (categoryParam == null || categoryParam.isBlank()) return null;

        List<Category> allCats = categoryRepository.findByStoreIdOrderByNameAsc(storeId);
        boolean isUuid = categoryParam.matches("[0-9a-f-]{36}");

        UUID rootId = null;
        if (isUuid) {
            rootId = allCats.stream()
                .filter(c -> c.getId().toString().equals(categoryParam))
                .map(Category::getId).findFirst().orElse(null);
        } else {
            rootId = allCats.stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryParam))
                .map(Category::getId).findFirst().orElse(null);
        }

        if (rootId == null) return Collections.emptySet();

        Set<UUID> ids = new HashSet<>();
        collectSubtree(rootId, allCats, ids);
        return ids;
    }

    private void collectSubtree(UUID id, List<Category> all, Set<UUID> result) {
        result.add(id);
        for (Category c : all) {
            if (id.equals(c.getParentId())) collectSubtree(c.getId(), all, result);
        }
    }

    private Sort resolveSort(String sort) {
        return switch (sort == null ? "" : sort) {
            case "price_asc"  -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "name_asc"   -> Sort.by("name").ascending();
            default           -> Sort.by("createdAt").descending();
        };
    }

    public Map<String, Object> stockSummary(String storeId) {
        long total = productRepository.countByStoreId(storeId);
        long outOfStock = productRepository.countByStoreIdAndStock(storeId, 0);
        long lowStock = productRepository.countLowStock(storeId, LOW_STOCK_THRESHOLD);
        List<Product> topLow = productRepository.findTopLowStock(
            storeId, LOW_STOCK_THRESHOLD, PageRequest.of(0, 5));

        List<Map<String, Object>> topLowDto = topLow.stream()
            .map(p -> Map.<String, Object>of("id", p.getId(), "name", p.getName(), "stock", p.getStock()))
            .toList();

        return Map.of("total", total, "outOfStock", outOfStock, "lowStock", lowStock, "topLow", topLowDto);
    }

    public List<String> findTags(String storeId) {
        return productRepository.findAll().stream()
            .filter(p -> p.getStoreId().equals(storeId))
            .flatMap(p -> p.getTags().stream())
            .distinct().sorted().collect(Collectors.toList());
    }

    public Optional<Product> findById(String storeId, String idOrSlug) {
        boolean isUuid = idOrSlug.matches("[0-9a-f-]{36}");
        if (isUuid) {
            Optional<Product> p = productRepository.findByIdAndStoreId(UUID.fromString(idOrSlug), storeId);
            if (p.isPresent()) return p;
        }
        String namePattern = idOrSlug.replace("-", " ");
        Optional<Product> byName = productRepository.findByStoreIdAndNameIgnoreCase(storeId, namePattern);
        if (byName.isPresent()) return byName;
        return productRepository.findFirstByStoreIdAndNameContainingIgnoreCase(
            storeId, namePattern, PageRequest.of(0, 1));
    }

    @Transactional
    public Product create(String storeId, String userEmail, ProductCreateRequest req) {
        if (userEmail != null) enforceProductLimit(storeId, userEmail);

        Product product = new Product();
        product.setStoreId(storeId);
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setStock(req.stock());
        product.setImages(req.images() != null ? req.images() : new ArrayList<>());
        product.setTags(req.tags() != null ? req.tags() : new ArrayList<>());
        product.setSpecs(req.specs() != null ? req.specs() : new ArrayList<>());
        if (req.categoryId() != null && !req.categoryId().isBlank()) {
            categoryRepository.findById(UUID.fromString(req.categoryId())).ifPresent(product::setCategory);
        }
        return productRepository.save(product);
    }

    private void enforceProductLimit(String storeId, String userEmail) {
        try {
            var response = restClient.get()
                .uri(platformServiceUrl + "/subscription-status")
                .header("x-user-email", userEmail)
                .retrieve()
                .toEntity(Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sub = (Map<String, Object>) response.getBody().get("subscription");
                int maxProducts = 50;
                if (sub != null) {
                    Object raw = sub.get("maxProducts");
                    if (raw instanceof Number number) {
                        maxProducts = number.intValue();
                    }
                }
                long count = productRepository.countByStoreId(storeId);
                if (count >= maxProducts) throw new IllegalStateException(
                    "Product limit reached. Your plan allows up to " + maxProducts + " products.");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception ex) {
            log.debug("platform-service unreachable; allowing product creation", ex);
        }
    }

    @Transactional
    public Product update(UUID id, String storeId, ProductUpdateRequest req) {
        Product product = productRepository.findByIdAndStoreId(id, storeId)
            .orElseThrow(() -> new NoSuchElementException("Product not found"));

        if (req.name() != null) product.setName(req.name());
        if (req.description() != null) product.setDescription(req.description());
        if (req.price() != null) product.setPrice(req.price());
        if (req.stock() != null) product.setStock(req.stock());
        if (req.images() != null) product.setImages(req.images());
        if (req.tags() != null) product.setTags(req.tags());
        if (req.specs() != null) product.setSpecs(req.specs());
        if (req.categoryId() != null) {
            if (req.categoryId().isBlank()) {
                product.setCategory(null);
            } else {
                categoryRepository.findById(UUID.fromString(req.categoryId())).ifPresent(product::setCategory);
            }
        }
        return productRepository.save(product);
    }

    @Transactional
    public void delete(UUID id, String storeId, String role) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product not found"));
        if ("superadmin".equals(role)) {
            productRepository.delete(product);
            return;
        }
        if (storeId == null) throw new IllegalArgumentException("x-store-id header required");
        if (!product.getStoreId().equals(storeId)) throw new SecurityException("Forbidden");
        productRepository.delete(product);
    }

    @Transactional
    public void deductStock(StockDeductRequest req) {
        for (var item : req.items()) {
            productRepository.deductStock(UUID.fromString(item.productId()), item.qty());
        }
    }

    @Transactional
    public Product addImageUrl(UUID productId, String storeId, String url) {
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
            .orElseThrow(() -> new NoSuchElementException("Product not found"));
        List<String> images = new ArrayList<>(product.getImages());
        images.add(url);
        product.setImages(images);
        return productRepository.save(product);
    }
}
