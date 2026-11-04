# Marketplace Feature, Redis Caching, and Rate Limiting Plan

## Purpose

This document is a planning roadmap for expanding the school-based marketplace API. It intentionally proposes features and implementation approaches only; no application code, dependencies, configuration, or database schema is changed by this plan.

## Current Foundation

The repository already has foundations for:

- Java 21 and Spring Boot
- PostgreSQL/JPA persistence
- Spring Security and JWT/OAuth2 resource-server support
- Accounts, users, buyers, sellers, stores, products, categories, stocks, carts, cart items, orders, order items, and payments
- Basic authentication and account/store operations

## 1. Feature Roadmap

### Phase 1 — Complete the core marketplace

| Priority | Feature | Description | Main modules |
|---|---|---|---|
| P0 | Product CRUD and seller catalog management | Create, edit, publish, archive, duplicate, and delete seller products | `product`, `store`, `category`, `product_stocks` |
| P0 | Buyer-facing product browsing | Paginated lists, sorting, category filters, search, product details, and store details | `product`, `category`, `store` |
| P0 | Cart persistence | Add/remove items, update quantities, calculate totals, merge guest/cart state, and detect unavailable items | `cart`, `cartitem`, `product_stocks` |
| P0 | Checkout and order creation | Validate stock, reserve inventory, snapshot prices, calculate totals, and create an immutable order | `cart`, `order`, `orderitem`, `product_stocks` |
| P0 | Order lifecycle | Pending, paid, confirmed, packed, shipped, completed, cancelled, and refunded states | `order`, `orderitem` |
| P0 | Stock management | Set stock, track stock movements, prevent overselling, and expose low-stock warnings | `product_stocks` |
| P0 | Payment integration | Support payment methods and status callbacks/idempotency; integrate a real provider only where required | `payment` |
| P0 | Authorization | Ensure buyers, sellers, and administrators can access only their permitted resources | `shared/auth`, every domain controller |
| P0 | API validation and error consistency | Standard validation messages, field errors, status codes, and request correlation IDs | `shared` |

### Phase 2 — Marketplace trust and operations

| Priority | Feature | Description | Main modules |
|---|---|---|---|
| P1 | Reviews and ratings | Buyers review purchased products/stores; seller responses and moderation controls | New `review` module |
| P1 | Wishlist/favorites | Save products and stores for later | New `wishlist` module |
| P1 | Product images | Upload validation, image variants, primary-image selection, and safe object storage | New `media` module |
| P1 | Seller dashboard | Sales summaries, revenue, best-selling products, stock alerts, and pending orders | Seller reporting/read-model services |
| P1 | Buyer dashboard | Recent orders, order status, spending summaries, and reorder action | Buyer reporting/read-model services |
| P1 | Order cancellation and returns | Cancellation windows, reasons, refund tracking, and seller approval | `order`, `payment` |
| P1 | Store profile and status | Store description, contact details, opening status, banners, and operational state | `store` |
| P1 | Seller verification | Verification requests, document review, approval/rejection, and status history | Seller/account workflow |
| P1 | Product variants | Size, color, bundle, and school/category-specific variants | New product option entities |
| P1 | Shipping and pickup | Delivery zones, pickup locations, shipping fees, and fulfillment methods | New `fulfillment` module |
| P1 | Notifications | Email and in-app notifications for login, orders, payment, shipment, and stock events | New `notification` module |

### Phase 3 — Discovery, growth, and engagement

| Priority | Feature | Description |
|---|---|---|
| P2 | Full-text product search | Search by name, description, category, store, and SKU; support filters and pagination |
| P2 | Recommendations | Recommend frequently purchased, similar, trending, or recently viewed products |
| P2 | Flash sales and promotions | Time-limited discounts, coupon codes, usage limits, minimum spend, and seller/campaign rules |
| P2 | Loyalty or points | Award points for purchases, refunds/reversal handling, and configurable redemption |
| P2 | Product bundles | Sell groups of products as one purchasable bundle |
| P2 | Recently viewed products | Track per-user product views with Redis and persist only when needed |
| P2 | Seller subscriptions | Optional plans and entitlements for additional store/catalog capabilities |
| P2 | Referrals | Referral codes, attribution, anti-abuse checks, and rewards |
| P2 | Shop announcements | Store-level banners and marketplace announcements |
| P2 | Buyer-school integration | Campus/school affiliation, school-specific catalogs, and optional student verification |

### Phase 4 — Administration and platform reliability

| Priority | Feature | Description |
|---|---|---|
| P1 | Admin console APIs | User, seller, store, category, product, order, payment, and complaint administration |
| P1 | Audit log | Immutable audit trail for authentication, sensitive profile changes, moderation, refunds, and configuration changes |
| P1 | Moderation | Report products, stores, reviews, or users; track reviewers, decisions, and appeal status |
| P1 | Feature flags | Enable or disable features per environment, seller, store, or school |
| P1 | Metrics and health checks | Request, DB, Redis, cache, checkout, payment, and queue metrics |
| P1 | Structured logging and tracing | Correlation IDs, trace IDs, log levels, and sanitized sensitive data |
| P1 | OpenAPI documentation | Document authentication, schemas, validation, errors, pagination, and examples |
| P1 | Background jobs | Scheduled stock cleanup, abandoned-cart reminders, report generation, payment reconciliation, and notification retries |
| P1 | Security hardening | Security headers, secrets management, input validation, account lockout, CSRF strategy, and dependency scanning |

### Additional feature candidates

- Product availability calendar for preorders or scheduled releases
- Purchase reservations with automatic expiry
- Seller-managed delivery slots
- QR-code order pickup verification
- In-app messaging between buyer and seller, with abuse reporting
- Marketplace dispute center
- Saved addresses and checkout preferences
- Buy-now checkout separate from cart checkout
- Guest checkout policy and guest-to-account order claiming
- Inventory forecast and restock suggestions
- Seller import/export of products and stock through validated CSV files
- Partial fulfillment and split-store orders
- Tax and fee configuration by school/region
- API/webhook support for third-party integrations
- Accessibility metadata and multi-language product content

## 2. Redis Requirements

Redis should be introduced as a shared, low-latency data store for:

1. Application caching
2. Distributed rate limiting
3. Temporary sessions or one-time security codes, if required
4. Cart or checkout data that benefits from short-lived storage
5. Idempotency keys for payment and checkout requests
6. Distributed locks for narrowly scoped operations
7. Background-job coordination where a queue implementation is added

Redis must not become the primary source of truth for products, orders, payments, or money balances. PostgreSQL remains authoritative for durable business records and transactional consistency.

## 3. Proposed Redis Architecture

### Components

Add these logical components when implementation begins:

- `spring-boot-starter-data-redis`
- Redis connection and timeout configuration
- Lettuce connection factory and connection pool configuration
- JSON Redis serialization for application cache values
- Typed cache/key-management utilities
- A Redis health check
- Cache event metrics where Spring and the chosen monitoring stack support them

Spring Data Redis manages Redis access; Spring Cache provides annotations and centralized cache configuration. Bucket4j Redis or a custom Redis Lua script can implement distributed rate limiting. Prefer an established library over application-specific atomic Lua logic if maintainers need to run Redis on more than one application instance.

### Docker/local setup

Add a Redis service alongside PostgreSQL in `compose.yaml` and use environment-backed settings for Redis host, port, username/password, database index, TLS, and timeouts. Keep Redis and PostgreSQL on a private application network in non-local environments.

Example future application configuration shape:

```yaml
spring:
  cache:
    type: redis
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2s
```

Actual secrets and passwords should come from environment variables or a secret manager, not source control.

## 4. Cache Key and TTL Policy

Use namespaced, versioned keys, for example:

- `marketplace:v1:product:{productId}`
- `marketplace:v1:product:list:{hash(query)}:page:{page}:size:{size}`
- `marketplace:v1:store:{storeId}`
- `marketplace:v1:category:tree`
- `marketplace:v1:buyer:{userId}:cart`
- `marketplace:v1:idempotency:checkout:{key}`

Do not use unescaped free-form filter values in keys. Serialize a canonical, normalized query representation and hash it to a fixed-length string where practical.

Recommended initial TTL ranges:

| Data | Suggested TTL | Notes |
|---|---:|---|
| Public category tree | 30–60 minutes | Invalidate on category changes |
| Store/product detail | 5–15 minutes | Invalidate on update/archive/delete |
| Public product search/list | 30 seconds–2 minutes | Short TTL tolerates eventual consistency |
| User cart | 1–24 hours | Prefer authoritative PostgreSQL if carts must be durable |
| Stock availability | 5–30 seconds | Never make Redis authoritative for final checkout stock |
| JWT logout/revocation marker | Remaining token lifetime | Redis key expiry should match token expiry |
| OTP/password-reset token | Business-defined expiry | Store only a hashed value; never plaintext |
| Checkout idempotency key | Provider-defined retention period | Enforce at an application/business boundary |

## 5. Suggested Caching Plan by Endpoint

### Cache reads

- Individual category details and category trees
- Individual published product details
- Store public profiles
- Public product listings/search results
- Seller/store aggregate summaries
- Dashboard aggregates with sufficiently short TTL

### Usually avoid caching in Redis without further design

- Checkout and payment state
- Stock mutations
- Account secrets
- Order mutation responses
- Personalized financial records with sensitive data
- Admin pages that require immediate consistency

### Cache-aside flow

For read-heavy data:

1. Build the normalized cache key.
2. Check Redis through Spring Cache.
3. On a miss, load from PostgreSQL.
4. Store a serializable DTO in Redis with a TTL.
5. Return the DTO.
6. Evict or update matching cache entries after successful writes.

If Redis is unavailable:

- Non-critical read caches should fall back to PostgreSQL.
- Application startup should not fail simply because Redis is temporarily unavailable, depending on deployment policy.
- Record cache errors and metrics rather than silently changing business data behavior.

## 6. Cache Invalidation and Consistency

- Evict product detail/list keys when a product is created, updated, published, archived, priced, or deleted.
- Evict store keys on store profile changes and sell-order changes.
- Evict category keys on category changes or product-category reassignment.
- Evict cart or stock projections after each mutation that affects them.
- Prefer explicit cache eviction and targeted keys over deleting an entire Redis database.
- For high-write entities, group keys by an entity version or cache generation so a write can invalidate a logical group.
- Add `Cache-Control` response headers for browser/CDN caches where appropriate, but do not treat browser cache freshness as server authorization.

## 7. Cache Reliability Measures

Plan to address:

- Cache stampede: staggered TTLs, request coalescing, lock on a cache key, or refresh-ahead.
- Null caching: avoid caching nulls or use short-lived negative entries only for bounded public lookups.
- Hot keys: split popular results, avoid embedding a single unbounded key in the response, and monitor Redis.
- Serialization/schema changes: include a cache version in keys and allow failed deserialization to trigger a cache miss.
- Large payloads: cache DTO projections, not lazy-loading entities or complete aggregate graphs.
- Sensitive DTO fields: cache only fields allowed for the caller's public context.
- Redis eviction: size Redis memory and configure the chosen eviction policy separately from application TTL behavior.

## 8. Rate Limiting Approach

A distributed Redis-backed limiter is recommended because multiple application instances must share the same quota.

### Limit dimensions

Apply separate budgets based on the relevant security risk:

- Per authenticated user ID for normal authenticated APIs
- Per IP address for login, registration, password reset, and public endpoints
- Per API key or client ID for integrations if API keys are introduced
- Per user plus endpoint group for sensitive operations
- Higher limits for read/search traffic than expensive write or authentication traffic

Choose the identity in this order for protected endpoints: authenticated account ID, then trusted client/IP identifier. Never expose the real IP through an arbitrary `X-Forwarded-For` header unless it has been validated by the reverse proxy/load balancer.

### Suggested policies

| Endpoint group | Example policy |
|---|---|
| Login | 5 attempts per 15 minutes per account/IP, with temporary lockout after repeated abuse |
| Registration | 3 per hour per IP |
| Password reset/request | 3 per hour per account/IP |
| Product search/list | 120–300 requests/minute per IP/user |
| Cart mutations | 60 per minute per user |
| Checkout/payment | 10–20 per minute per user/IP |
| General authenticated API | 120–600 per minute depending on endpoint cost |
| Administrative APIs | Lower or role-specific quotas |

These are starting points only and must be load-tested and adjusted against real traffic.

### Algorithm recommendation

Use a **sliding-window or token-bucket algorithm**:

- Sliding window limits recent traffic smoothly.
- Token bucket allows small bursts while preserving an average rate.
- Fixed windows are simpler but can allow a boundary burst.

Quota state must be updated atomically so parallel requests cannot bypass the limit. Include the endpoint policy/version in Redis keys, for example:

`marketplace:ratelimit:v1:login:ip:{ip}` and `marketplace:ratelimit:v1:checkout:user:{accountId}`.

### Response behavior

When a request exceeds its quota:

- Return `429 Too Many Requests`.
- Return standard rate-limit headers such as `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`, and `Retry-After` when supported by the chosen library.
- Do not disclose whether a username/account exists in login-rate-limit responses.
- Use generic error payloads for authentication failures while applying separate security counters internally.

Add the rate limit policy to Spring Security filters or MVC interceptors, depending on where authentication and trusted identity are available. Authentication endpoints may execute before normal authenticated authorization.

## 9. Redis and Rate Limiter Failure Policy

Because availability is important:

- Provide a metric for Redis cache misses, Redis errors, rate-limit decisions, and latency.
- Allow critical Redis operations to fail closed or open according to risk; security-sensitive endpoints should default more conservatively.
- Avoid crashing the entire application if Redis is unavailable.
- Recommend fail-open with monitoring for non-security read-cache operations after evidence that PostgreSQL can absorb the load.
- Recommend fail-open or a local-instance fallback only for non-critical rate limits; login and payment abuse protection require a documented fail-closed policy.
- Never use local-only fallback counters as the sole distributed protection in production.

## 10. Security and Data Considerations

- Enable Redis authentication and TLS in production.
- Apply network ACL/firewall restrictions so Redis is not publicly reachable.
- Do not cache raw passwords, JWTs, payment credentials, full card numbers, or sensitive documents.
- Hash or prefix-looking identifiers in cache keys when privacy requires it.
- Redact cache values from logs.
- Define tenant/school isolation before caching any account-specific or school-specific data.
- Validate cached DTOs and cache versions during deserialization upgrades.
- Use random, single-use idempotency keys for checkout and payment retries.

## 11. Implementation Sequence

### Epic 1 — Redis foundation

1. Add Redis to local Docker Compose.
2. Add Redis dependencies and externalized configuration.
3. Add health checks and metrics.
4. Implement JSON serialization and key conventions.
5. Test Redis outage and timeout behavior.

### Epic 2 — Read caching

1. Categorize public read-heavy endpoints.
2. Add cache-aside support for categories.
3. Add product/store details and product lists.
4. Add invalidation when DTOs change.
5. Load-test cache hit ratio, database load, latency, and memory use.

### Epic 3 — Distributed rate limiting

1. Add trusted-IP resolution and account identity extraction.
2. Implement login and password-reset limits first.
3. Implement public search and general API limits.
4. Add response headers and 429 errors.
5. Run multi-instance integration tests and Redis failure tests.

### Epic 4 — High-value Redis workflows

1. Token revocation/logout markers.
2. Checkout idempotency keys.
3. Short-lived password-reset/verification codes using hashed values.
4. Recently viewed and wishlist session acceleration if still needed.
5. Distributed locks for bounded inventory/payment reconciliation operations.

Redis use should be introduced incrementally. Cache correctness and security should be tested before enabling Redis for write-adjacent workflows.

## 12. Testing Plan

- Unit test key generation, TTL configuration, and cache DTO serialization.
- Unit test rate-limit policy decisions and threshold boundaries.
- Integration test Redis cache hit, miss, eviction, and invalidation behavior.
- Integration test Redis across two application instances using Testcontainers.
- Verify that concurrent requests cannot bypass a distributed rate limit.
- Verify login rate limits for account and IP dimensions separately.
- Verify Redis outages do not corrupt PostgreSQL transaction data.
- Verify expired keys and TTL-relative reset headers.
- Verify no sensitive values are written to Redis or logs.
- Compare PostgreSQL and cache results after update, archive, and deletion operations.

## 13. Suggested Delivery Order

1. Complete authentication and authorization.
2. Complete seller/catalog, stock, product search, cart, and checkout.
3. Add PostgreSQL-backed orders/payments and reliable lifecycle transitions.
4. Add Redis and cache public category/product/store reads.
5. Add Redis rate limiting to login, registration, and password reset.
6. Add reviews, wishlist, notifications, seller dashboard, and returns.
7. Add Redis advanced workflows such as idempotency, revocation, and temporary codes.
8. Add admin, moderation, audit, metrics, promotions, recommendations, and reporting.

This order keeps Redis from masking existing business-logic, authorization, or data-integrity gaps.
