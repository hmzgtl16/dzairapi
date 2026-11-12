# Changelog

All notable changes to DzairAPI are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

## [0.0.1] - 2026-11-12

### Added

#### Endpoints — Provinces (`/api/v1/provinces`)
- `GET /api/v1/provinces` — paginated list of all provinces (wilayas) with localized names and district/municipality counts.
- `GET /api/v1/provinces/{code}` — single province by numeric code with localized names and statistics.
- `GET /api/v1/provinces/{code}/districts` — paginated list of districts belonging to a province.
- `GET /api/v1/provinces/{code}/municipalities` — paginated list of municipalities belonging to a province.

#### Endpoints — Districts (`/api/v1/districts`)
- `GET /api/v1/districts` — paginated list of all districts (dairas) with localized names and municipality counts.
- `GET /api/v1/districts/{code}` — single district by numeric code with localized names and statistics.
- `GET /api/v1/districts/{code}/municipalities` — paginated list of municipalities belonging to a district.

#### Endpoints — Municipalities (`/api/v1/municipalities`)
- `GET /api/v1/municipalities` — paginated list of all municipalities (communes) with localized names.
- `GET /api/v1/municipalities/{code}` — single municipality by numeric code with localized names.

#### System endpoints
- `GET /health` — Spring Boot Actuator health endpoint backed by a custom `DatabaseHealthIndicator` (`SELECT 1`); returns `{"status":"UP"}` with details and components hidden.
- `GET /openapi` — OpenAPI 3 JSON specification.
- `GET /swagger-ui.html` — Swagger UI with tag ordering, request duration display, and actuator endpoint included.

#### Dataset metadata endpoint
- `GET /api/v1/metadata` — returns `api-version`, `dataset-version`, `last-updated` timestamp, data sources list, and entity counts; response is Caffeine-cached.

#### Error responses — RFC 9457
- All error responses use `application/problem+json` with `type`, `title`, `status`, `detail`, and `instance` fields.
- `404 Not Found` for unknown province, district, or municipality codes.
- `400 Bad Request` for path parameter type mismatches (e.g. non-numeric `code`).
- `400 Bad Request` for general illegal argument errors.
- `429 Too Many Requests` for rate-limit violations.
- `500 Internal Server Error` for unexpected failures.

### Security

- **Rate limiting** — Bucket4j servlet filter applied to all paths: 10 requests per 10-second window per remote IP address, refilled at 1 token per 10 seconds (interval strategy), initial capacity 5. Buckets stored in a Caffeine JCache (`maximumSize=1_000_000`, `expireAfterAccess=3600s`). Rate-limit violations return HTTP 429 with an `application/problem+json` body.
- **Read-only database access** — all JPA transactions in the service layer are annotated `@Transactional(readOnly = true)`; no write operations are exposed through the API.
- **Spring Security filter chains** — all paths other than `/api/**`, `/openapi/**`, `/swagger-ui/**`, `/swagger-ui.html`, and `/health/**` are denied. CSRF, HTTP Basic, form login, and logout are disabled. Sessions are stateless.
- **Secure response headers** — `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy: no-referrer`, `Strict-Transport-Security: max-age=31536000; includeSubDomains` applied by Spring Security.
- **Actuator exposure** — only the `health` endpoint is exposed; details and component info are hidden (`show-details: never`, `show-components: never`).

---

[Unreleased]: https://github.com/hmzgtl16/dzairapi/compare/v0.0.1...HEAD
[0.0.1]: https://github.com/hmzgtl16/dzairapi/releases/tag/v0.0.1