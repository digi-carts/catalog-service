# Changelog

## [1.1.0] - 2026-08-29

### Features
- add role guard on product write and MIME-typed image upload endpoint
- add JavaDoc, health aliases, and component tests

### Bug Fixes
- return 400 Invalid file type when multipart file is absent or null
- update tests for buildTree signature and findTags mock changes
- avoid lower(bytea) by using empty-string sentinel instead of null search
- use COALESCE to avoid lower(bytea) error on null search param
- resolve catalog product query lower(bytea) and category compile errors
- remove liquibase default-schema to allow fresh DB bootstrap
- update controller @RequestMapping paths to match gateway routes
- use @Query for parent.id traversal in CategoryRepository
- remove Pageable from Optional query method; fix JSONB default syntax
- correct schema name and JSONB default value syntax
- run create-schema always so it recreates if missing
- accept any checksum for idempotent create-schema changeset
- limit HikariCP pool to 2 connections (db-f1-micro max 25 total)
- disable Hibernate validation (Liquibase owns schema, uuid vs String mismatch)
- set liquibase-schema=public so schema is created before tracking tables
- add Cloud SQL postgres-socket-factory for Cloud Run connectivity

### Performance
- fix N+1 queries and full table scan in catalog-service

### Documentation
- add complete project documentation

### CI/Build
- retrigger prod deploy
- retrigger after db-g1-small upgrade
- trigger first dev build
- use separate GCP project IDs for dev (digi-carts-dev) and prod (digi-carts)
- add release step to prod deploy workflow