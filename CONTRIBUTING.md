# Contributing

Thanks for taking the time to improve DzairAPI. All contributors are expected to follow our [Code of Conduct](./CODE_OF_CONDUCT.md).

## Ways to Contribute

Good contributions include:

- Bug fixes for API behavior, validation, error handling, or rate limiting.
- Corrections to administrative-division data with verifiable public sources.
- Tests that cover existing behavior or prevent regressions.
- Documentation improvements for setup, examples, response shapes, or migration notes.
- Performance improvements that preserve the public API contract.

Please avoid unrelated refactors in the same pull request as a behavior change.

### Before you start

- Search [existing issues](https://github.com/hmzgtl16/dz-province-api/issues) to avoid duplicates.
- For larger changes, open an issue first to discuss the approach. Use the appropriate issue template: [bug report](https://github.com/hmzgtl16/dz-province-api/issues/new?template=bug_report.yml), [data correction](https://github.com/hmzgtl16/dz-province-api/issues/new?template=data_correction.yml), or [feature request](https://github.com/hmzgtl16/dz-province-api/issues/new?template=feature_request.yml).
- For questions that are not bugs, data corrections, or feature requests, see [SUPPORT.md](./SUPPORT.md).

## Security

Do not report security vulnerabilities in public issues. Follow the process in [SECURITY.md](./SECURITY.md).

## Development Setup

### Prerequisites

- **JDK 25** (managed by Gradle toolchain; downloaded automatically if missing).
- **Docker** (for PostgreSQL container via `docker-compose.yaml`).

### Clone and run

```bash
git clone https://github.com/hmzgtl16/dz-province-api.git
cd dz-province-api
```

Start the database container:

```bash
docker compose up -d
```

Run the application (default profile: `dev`, port 8081):

```bash
./gradlew bootRun
```

The API is available at `http://localhost:8081/api/v1/`.

### Profiles

- `dev` (default): development settings with debug logging for rate limiting.
- `prod`: production settings.

Override the profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Flyway migrations run automatically on startup.

## Project Structure

See the [Project Structure](./README.md#project-structure) section in README.md.

## Branches and Commits

The git history does not follow a strict convention. The following is a suggested approach:

- **Branch naming**: use prefixes such as `feat/`, `fix/`, `docs/`, `chore/` followed by a short description (e.g., `feat/add-district-filter`).
- **Commit messages**: follow [Conventional Commits](https://www.conventionalcommits.org/) (e.g., `feat: add district filter`, `fix: correct wilaya code`).

## Checks

Run these commands before opening a pull request:

```bash
# Format code and resources
./gradlew spotlessApply

# Run tests and build (enforces formatting, coverage, and compilation)
./gradlew build
```

The build enforces:

- **Formatting**: Spotless (KtLint for Kotlin, Prettier for YAML and SQL).
- **Tests**: all unit and integration tests must pass.
- **Coverage**: Kover enforces a minimum of **80% instruction** and **70% branch** coverage for main sources.

Tests don't need any external service for rate limiting. The rate limiter uses an in-memory Caffeine cache and is not active in the test profile.

## Tests

Tests live in `src/test/kotlin`. The project uses an H2 in-memory database for unit tests and integration tests.

When adding new behavior, include a focused test that covers the change. New endpoints need at least a happy-path test and a negative case (e.g., invalid input or not-found response).

## Data Changes

The administrative-division data comes from texts published in the Official Journal of the Algerian Republic (JORA). Data corrections require:

1. A link to the specific Official Journal text as the source.
2. A [data correction issue](https://github.com/hmzgtl16/dz-province-api/issues/new?template=data_correction.yml) describing the change.
3. A Flyway migration in `src/main/resources/db/migration/` applying the change.

Never edit an already-applied migration. Always create a new versioned migration file (e.g., `V2__correct_wilaya_name.sql`).

## API Changes

DzairAPI v1 is the current stable API. Pull requests should preserve existing response shapes, error codes, pagination behavior, filter semantics, and rate-limit headers unless the change is explicitly intended to be breaking. Breaking changes require an issue and maintainer agreement first.

When adding or changing public behavior:

- Update the OpenAPI annotations on controllers and the customizers in `OpenApiConfig` (`com.example.dzairapi.config`).
- Add or update tests.
- Update `README.md` if the public API surface changes.

## Logging and Privacy

Do not add logging for request bodies, response bodies, cookies, authorization headers, API keys, full query parameter values, or other unnecessary personal data. Application logs should stay limited to operational metadata.

See [PRIVACY.md](./PRIVACY.md) and [SECURITY.md](./SECURITY.md) for more details.

## Pull Requests

Pull requests should be focused and small. The [PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md) is filled in automatically and covers:

- Summary and reason (link an issue with `Closes #`).
- Type of change and API compatibility.
- Checklist: `spotlessApply` and `build` pass locally, tests added or updated, OpenAPI annotations updated, README updated if needed, no logging of sensitive data.
- Data sources (for data changes).
- Test results.

CI runs the build and tests on each pull request. All checks must pass.

Reviews are handled by a solo maintainer, so review turnaround may take time.

## License of Contributions

By contributing, you agree that your contributions to the source code will be licensed under the project's [Apache-2.0 License](./LICENSE).