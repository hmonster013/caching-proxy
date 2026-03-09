package org.de013.cachingproxy.service;

import lombok.extern.slf4j.Slf4j;
import org.de013.cachingproxy.model.CachedResponse;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CacheService {

    static final String CACHE_FILE = "proxy-cache.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, CachedResponse> cache;

    public CacheService() {
        this.cache = loadFromFile();
    }

    public Optional<CachedResponse> get(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void put(String key, CachedResponse response) {
        cache.put(key, response);
        saveToFile();
    }

    public void clear() {
        cache.clear();
        deleteCache();
        log.info("Cache cleared.");
    }

    /** Called before Spring starts (static context) */
    public static void deleteCache() {
        File file = new File(CACHE_FILE);
        if (file.exists() && file.delete()) {
            System.out.println("Cache file deleted: " + file.getAbsolutePath());
        } else {
            System.out.println("No cache file found.");
        }
    }

    private Map<String, CachedResponse> loadFromFile() {
        File file = new File(CACHE_FILE);
        if (!file.exists()) return new ConcurrentHashMap<>();
        try {
            Map<String, CachedResponse> loaded = mapper.readValue(
                    file, new TypeReference<Map<String, CachedResponse>>() {});
            log.info("Loaded {} cached entries from {}", loaded.size(), CACHE_FILE);
            return new ConcurrentHashMap<>(loaded);
        } catch (JacksonException e) {
            log.warn("Could not load cache file, starting fresh: {}", e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }

    private void saveToFile() {
        try {
            mapper.writeValue(new File(CACHE_FILE), cache);
        } catch (JacksonException e) {
            log.warn("Could not save cache to file: {}", e.getMessage());
        }
    }
}

