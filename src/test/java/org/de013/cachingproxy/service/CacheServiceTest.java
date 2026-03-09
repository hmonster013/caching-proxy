package org.de013.cachingproxy.service;

import org.de013.cachingproxy.model.CachedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        // Ensure no leftover cache file from previous runs
        new File(CacheService.CACHE_FILE).delete();
        cacheService = new CacheService();
    }

    @Test
    void get_returnsEmpty_whenCacheIsEmpty() {
        Optional<CachedResponse> result = cacheService.get("/products/1");
        assertThat(result).isEmpty();
    }

    @Test
    void put_thenGet_returnsCachedResponse() {
        CachedResponse response = new CachedResponse(
                200,
                Map.of("Content-Type", List.of("application/json")),
                "{\"id\":1}".getBytes()
        );

        cacheService.put("/products/1", response);

        Optional<CachedResponse> result = cacheService.get("/products/1");
        assertThat(result).isPresent();
        assertThat(result.get().getStatusCode()).isEqualTo(200);
        assertThat(result.get().getBody()).isEqualTo("{\"id\":1}".getBytes());
    }

    @Test
    void put_persistsToDisk_andReloadOnNewInstance() {
        CachedResponse response = new CachedResponse(
                200,
                Map.of("Content-Type", List.of("application/json")),
                "hello".getBytes()
        );

        cacheService.put("/test", response);

        // Simulate server restart — new CacheService instance reads from file
        CacheService reloaded = new CacheService();
        Optional<CachedResponse> result = reloaded.get("/test");

        assertThat(result).isPresent();
        assertThat(result.get().getStatusCode()).isEqualTo(200);
        assertThat(new String(result.get().getBody())).isEqualTo("hello");

        new File(CacheService.CACHE_FILE).delete();
    }

    @Test
    void clear_removesAllEntriesAndDeletesFile() {
        cacheService.put("/a", new CachedResponse(200, Map.of(), "a".getBytes()));
        cacheService.put("/b", new CachedResponse(200, Map.of(), "b".getBytes()));

        cacheService.clear();

        assertThat(cacheService.get("/a")).isEmpty();
        assertThat(cacheService.get("/b")).isEmpty();
        assertThat(new File(CacheService.CACHE_FILE)).doesNotExist();
    }

    @Test
    void deleteCache_static_deletesFileIfExists() throws IOException {
        new File(CacheService.CACHE_FILE).createNewFile();
        assertThat(new File(CacheService.CACHE_FILE)).exists();

        CacheService.deleteCache();

        assertThat(new File(CacheService.CACHE_FILE)).doesNotExist();
    }

    @Test
    void deleteCache_static_doesNotThrow_whenFileAbsent() {
        new File(CacheService.CACHE_FILE).delete();
        // Should not throw
        CacheService.deleteCache();
    }
}
