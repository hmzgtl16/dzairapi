# Security Policy

## Supported Versions

| Version | Supported |
|---|---|
| 0.0.x (current) | Yes |
| Older release lines | No |

The API is versioned in the URL path (`/api/v1/...`). Security fixes are applied to the latest release in the 0.0.x line.

## Reporting a Vulnerability

**Do not open public GitHub issues for security vulnerabilities.** Use one of the channels below.

### Primary channel

Use [GitHub private vulnerability reporting](https://github.com/hmzgtl16/dzairapi/security/advisories/new) to submit a report privately.

> If the "Report a vulnerability" button is not visible in the repository Security tab, the maintainer needs to enable the feature in repository Settings > Code security > Security advisories.

### Secondary channel

Email the maintainer at `hamza.gattal.16@gmail.com`.

### What to include

- A clear description of the vulnerability.
- Reproduction steps or a proof of concept.
- The affected endpoint or component (for example, `/api/v1/provinces`).
- The impact (data exposure, authentication bypass, service disruption, etc.).
- Any suggested fix or mitigation.

Use the same channels for any issue involving secrets or sensitive operational data.

## What to Expect

- An acknowledgment within **7 days** of reporting.
- A status update within **14 days**, including whether the report is being investigated and any request for additional information.
- A target fix date that depends on severity. Critical issues are prioritized.

Once confirmed, the issue will be fixed privately. The fix is released together with a GitHub security advisory or release notes. The reporter is credited unless they prefer to remain anonymous.

## Scope

### In scope

- Vulnerabilities in this repository's code, configuration, and build: API routes, validation, error handling, rate limiting, logging, security configuration, Flyway migrations.
- Dependency vulnerabilities that affect the running service.
- Exposure of secrets or sensitive operational data.
- Issues in the deployed service, if a production instance exists.

### Out of scope

- Denial-of-service or load testing against the production API.
- Social engineering and physical attacks.
- Reports based only on missing headers or best-practice suggestions without a practical exploit.
- Automated scanner output without a demonstrated impact.
- Public dataset inaccuracies without a security or privacy impact. Use the [data correction issue template](https://github.com/hmzgtl16/dzairapi/issues/new?template=data_correction.yml) instead.
- Vulnerabilities in third-party services or in dependencies with no impact on this project.

## Safe Harbor

Good-faith research that follows this policy will not be pursued legally or result in a ban. This means no data destruction, no privacy violations, no service disruption, and no public disclosure before a fix is available. If you are unsure whether an action is within scope, please reach out first.

## Operational Privacy

Application logs do not include request bodies, response bodies, cookies, authorization headers, API keys, or full query parameter values. Logs are limited to operational metadata such as request path, status code, and response time. If you discover a path that leaks sensitive data into logs, report it as a security issue. See [PRIVACY.md](./PRIVACY.md) for full details.