# Paging and Sorting Implementation

## Overview

This document records the implementation of buyer-facing product browsing with pagination, sorting, search, category filtering, store filtering, product details, category listing, and store details.

The browsing APIs are read-only and publicly accessible. Redis caching is not included in this implementation.

## Implemented Changes

### Product Browsing API

Added a public controller at `/api/v1/products`.

Endpoints:

- `GET /api/v1/products`
- `GET /api/v1/products/{productId}`

The product list supports:

- `search`: searches product titles and descriptions.
- `categoryId`: filters by category ID.
- `storeId`: filters by store ID.
- `page`: zero-based page number.
- `size`: page size between 1 and 100.
- `sort`: supported values are `title`, `price`, `createdAt`, and `updatedAt`.
- `direction`: `asc` or `desc`.

Example:

```text
GET /api/v1/products?search=laptop&categoryId=2&storeId=3&sort=price&direction=asc&page=0&size=20
```

Invalid sort fields use `createdAt`. Invalid direction values use `desc`.

### Product Repository Query

Added a repository query that combines search, category, and store filters in the database.

The query:

- Matches the search against product title and description.
- Treats blank search values as no search filter.
- Applies category and store filters when their IDs are provided.
- Uses Spring Data `Pageable` for pagination and sorting.

### Product Browse DTO

Added `ProductBrowseResponse`, which contains:

- Product ID, title, description, and price.
- Total available stock quantity.
- Category ID and name.
- Store ID and name.
- Product creation and update timestamps.

The browsing response avoids exposing the seller details that are not needed for buyer product discovery.

### Efficient Stock Loading

Added a repository method to retrieve stock records for multiple products in one query.

The browse page loads stock quantities in one batch operation and maps them to their products. This prevents a separate stock query for every item in the result page.

Product detail requests retrieve and total stock records for only the requested product.

### Category Browsing

Added public category browsing at:

- `GET /api/v1/categories`

Also added:

- A category repository.
- A category browse service.
- A category controller.

Categories are returned through the existing category DTO.

### Store Browsing

Expanded the public store endpoints at `/api/v1/store`:

- `GET /api/v1/store`
- `GET /api/v1/store/{storeId}`

Store browse responses contain:

- Store ID.
- Store name and description.
- Product count.
- Store creation timestamp.

The product count is calculated from the database through a repository query.

### Security Configuration

Updated the Spring Security configuration so anonymous buyers can access:

- `GET /api/v1/products/**`
- `GET /api/v1/categories/**`
- `GET /api/v1/store`
- `GET /api/v1/store/**`

Existing seller registration and product posting operations remain protected by the application authentication rules.

## Changed and Added Files

### Added

- `src/main/java/com/marketplace/plv/product/ProductBrowseController.java`
- `src/main/java/com/marketplace/plv/product/ProductBrowseService.java`
- `src/main/java/com/marketplace/plv/product/dto/ProductBrowseResponse.java`
- `src/main/java/com/marketplace/plv/product/dto/CategorySummary.java`
- `src/main/java/com/marketplace/plv/product/dto/StoreSummary.java`
- `src/main/java/com/marketplace/plv/category/CategoryController.java`
- `src/main/java/com/marketplace/plv/category/CategoryRepository.java`
- `src/main/java/com/marketplace/plv/category/CategoryService.java`
- `src/main/java/com/marketplace/plv/store/dto/StoreBrowseResponse.java`

### Updated

- `src/main/java/com/marketplace/plv/product/ProductRepository.java`
- `src/main/java/com/marketplace/plv/product_stocks/ProductStocksRepository.java`
- `src/main/java/com/marketplace/plv/store/StoreController.java`
- `src/main/java/com/marketplace/plv/store/StoreRepository.java`
- `src/main/java/com/marketplace/plv/store/StoreService.java`
- `src/main/java/com/marketplace/plv/shared/config/SecurityConfig.java`

## API Behavior

### Product List

The product list is returned as a Spring Data page. Typical response fields include:

- `content`
- `pageable`
- `totalElements`
- `totalPages`
- `size`
- `number`
- `first`
- `last`
- `empty`

Each item in `content` contains the product browse DTO fields.

### Product Details

Unknown product IDs return HTTP `404 Not Found` through the existing global exception handling mechanism.

### Pagination Validation

The page number must be zero or greater. Page size must be between 1 and 100.

## Validation

The implementation compiled successfully with:

```text
104 source files compiled
BUILD SUCCESS
```

The existing Spring context test cannot start without a valid JDBC database URL configured in `DB_URL`. The observed failure is:

```text
DataSource: 'url' must start with "jdbc"
```

The paging and sorting implementation itself has not introduced a compile failure.
