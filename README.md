# Caching Proxy

> Solution for [roadmap.sh — Caching Server](https://roadmap.sh/projects/caching-server)

A CLI-driven caching proxy server built with Spring Boot.It forwards HTTP requests to an origin server and caches the responses. Subsequent identical requests are served directly from the cache without hitting the origin.

## How It Works

```
Client → Caching Proxy → Origin Server
                ↓
          proxy-cache.json
```

- **Cache MISS**: request is forwarded to the origin, response is cached and returned with `X-Cache: MISS`
- **Cache HIT**: response is served from cache with `X-Cache: HIT`
- Cache is persisted to `proxy-cache.json` and survives server restarts

## Requirements

- Java 17+
- Maven

## Build

```bash
./mvnw clean package -DskipTests
```

The JAR will be at `target/caching-proxy-0.0.1-SNAPSHOT.jar`.

## Usage

### Start the proxy server

```bash
java -jar target/caching-proxy-0.0.1-SNAPSHOT.jar --port <port> --origin <url>
```

| Flag | Description | Required |
|------|-------------|----------|
| `--port` | Port the proxy server listens on | ✅ |
| `--origin` | URL of the target server to forward requests to | ✅ |

**Example:**

```bash
java -jar target/caching-proxy-0.0.1-SNAPSHOT.jar --port 3000 --origin http://dummyjson.com
```

The proxy will start on `http://localhost:3000`. Any request sent to it will be forwarded to `http://dummyjson.com`.

```bash
# Fetches from origin and caches — X-Cache: MISS
curl -I http://localhost:3000/products/1

# Served from cache — X-Cache: HIT
curl -I http://localhost:3000/products/1
```

### Clear the cache

Stop the server first, then run:

```bash
java -jar target/caching-proxy-0.0.1-SNAPSHOT.jar --clear-cache
```

This deletes the `proxy-cache.json` file. The next server start will begin with an empty cache.

## Response Headers

| Header | Value | Meaning |
|--------|-------|---------|
| `X-Cache` | `HIT` | Response served from cache |
| `X-Cache` | `MISS` | Response fetched from origin server and cached |

## Cache Persistence

Cached responses are stored in `proxy-cache.json` in the working directory. The file is loaded on startup, so previously cached responses survive server restarts.

To inspect the cache:

```bash
# PowerShell
Get-Content proxy-cache.json | ConvertFrom-Json

# Linux/macOS
cat proxy-cache.json | jq 'keys'
```

## Running Tests

```bash
./mvnw test
```

Tests are split into:
- `CacheServiceTest` — unit tests for cache storage, persistence, and clearing
- `ProxyControllerTest` — unit tests for routing, cache key handling, and response headers (mocked `CacheService`)
- `CachingProxyApplicationTests` — Spring context load test

## Project Structure

```
src/main/java/org/de013/cachingproxy/
├── CachingProxyApplication.java      # Entry point, CLI argument parsing
├── controller/
│   └── ProxyController.java          # Catch-all request handler, forwards & caches
├── service/
│   ├── CacheService.java             # In-memory cache with file persistence
│   ├── AppService.java
│   └── AppServiceImpl.java
├── model/
│   └── CachedResponse.java           # Stores status code, headers, body
└── cli/
    └── ...                           # CLI command infrastructure
```
