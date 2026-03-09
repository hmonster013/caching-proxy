package org.de013.cachingproxy.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.de013.cachingproxy.model.CachedResponse;
import org.de013.cachingproxy.service.CacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Slf4j
@RestController
public class ProxyController {

    private static final Set<String> SKIP_REQUEST_HEADERS = Set.of(
            "host", "connection", "transfer-encoding", "keep-alive",
            "proxy-authenticate", "proxy-authorization", "te", "trailers", "upgrade",
            "content-length", "expect"
    );

    private static final Set<String> SKIP_RESPONSE_HEADERS = Set.of(
            "transfer-encoding", "connection", "keep-alive"
    );

    private final String origin;
    private final CacheService cacheService;
    private final HttpClient httpClient;

    public ProxyController(@Value("${proxy.origin}") String origin, CacheService cacheService) {
        this.origin = origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
        this.cacheService = cacheService;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException, InterruptedException {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String cacheKey = path + (query != null ? "?" + query : "");

        Optional<CachedResponse> cached = cacheService.get(cacheKey);
        if (cached.isPresent()) {
            log.info("CACHE HIT  : {}", cacheKey);
            return buildResponse(cached.get(), "HIT");
        }

        log.info("CACHE MISS : {} → {}{}", cacheKey, origin, cacheKey);
        CachedResponse response = forwardRequest(request, cacheKey);
        cacheService.put(cacheKey, response);
        return buildResponse(response, "MISS");
    }

    private CachedResponse forwardRequest(HttpServletRequest request, String cacheKey)
            throws IOException, InterruptedException {

        String targetUrl = origin + cacheKey;

        byte[] bodyBytes = request.getInputStream().readAllBytes();
        HttpRequest.BodyPublisher bodyPublisher = bodyBytes.length > 0
                ? HttpRequest.BodyPublishers.ofByteArray(bodyBytes)
                : HttpRequest.BodyPublishers.noBody();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .method(request.getMethod(), bodyPublisher);

        Collections.list(request.getHeaderNames()).forEach(name -> {
            if (!SKIP_REQUEST_HEADERS.contains(name.toLowerCase())) {
                builder.header(name, request.getHeader(name));
            }
        });

        HttpResponse<byte[]> response = httpClient.send(builder.build(),
                HttpResponse.BodyHandlers.ofByteArray());

        Map<String, List<String>> headers = new LinkedHashMap<>();
        response.headers().map().forEach((name, values) -> {
            if (!SKIP_RESPONSE_HEADERS.contains(name.toLowerCase())) {
                headers.put(name, values);
            }
        });

        return new CachedResponse(response.statusCode(), headers, response.body());
    }

    private ResponseEntity<byte[]> buildResponse(CachedResponse cached, String cacheStatus) {
        HttpHeaders headers = new HttpHeaders();
        cached.getHeaders().forEach((name, values) -> {
            if (!name.startsWith(":")) { // skip HTTP/2 pseudo-headers
                headers.put(name, values);
            }
        });
        headers.set("X-Cache", cacheStatus);
        return new ResponseEntity<>(cached.getBody(), headers, HttpStatus.valueOf(cached.getStatusCode()));
    }
}
