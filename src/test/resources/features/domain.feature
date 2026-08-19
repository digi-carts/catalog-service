Feature: Catalog component
  Scenario: products require store header
    When I GET "/products"
    Then the response status is 400

  Scenario: products with store header
    When I GET "/products" with store header "store-1"
    Then the response status is 200
