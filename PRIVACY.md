# Privacy Policy

Last updated: 2026-11-09

DzairAPI is an independent open API project operated by Hamza Gattal. It provides public Algerian administrative divisions data.

DzairAPI is not affiliated with, endorsed by, or operated by any official government institution.

## Data You Send to the API

The API is public and does not require an account, API key, or authorization header. Requests include only query parameters (for example, `page`, `size`, `lang`) and standard HTTP headers.

Do not send secrets, private personal data, or confidential business data in API query parameters.

## Application Logs

Application logs are minimized and used for operations and debugging. The application does not contain custom request-logging filters or loggers. Any logged data comes from framework defaults and the Bucket4j library (which logs rate-limit debug messages only in the `dev` profile).

Application logs do not intentionally include:

- Request bodies.
- Response bodies.
- Cookies.
- Authorization headers.
- API keys.
- Full query parameter values.

## Rate Limiting Data

Client IP addresses (obtained via `getRemoteAddr()`) are used as rate-limit keys and stored in a Caffeine in-memory cache (`dzairapi-rate-limit-buckets`). Cache entries expire after 3600 seconds (1 hour) of inactivity. This data is used solely for rate limiting and is not used for any other purpose.

## Server Access Logs

If the service is deployed behind a reverse proxy or load balancer, that infrastructure may produce access logs containing IP address, user agent, request path, timestamp, status code, response size, and response time. The retention period for such logs is `TODO: verify retention or remove this section`.

## Hosting and Processors

The hosting provider and location are `TODO: name hosting provider and location`. Infrastructure providers may process limited technical data, such as IP addresses and server logs, as needed to provide hosting, networking, and security services.

## Cookies and Analytics

The API itself does not use browser cookies.

DzairAPI does not use advertising pixels or third-party marketing trackers.

## Sharing

Operational logs are not sold. They may be shared only when necessary with infrastructure providers, security responders, legal authorities where required, or maintainers who need access to operate and protect the service.

## Your Requests

For privacy questions, access requests, deletion requests, or objections, email `hamza.gattal.16@gmail.com`.

Some technical logs may not be tied to a directly identifiable account because DzairAPI does not require accounts. Requests will be handled based on the information reasonably available.

## Changes

This policy may change as the project, infrastructure, or logging practices change. Material changes should be reflected in this file and linked documentation.