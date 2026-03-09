package org.de013.cachingproxy.controller;

import org.de013.cachingproxy.model.CachedResponse;
import org.de013.cachingproxy.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProxyControllerTest {

    @Mock
    private CacheService cacheService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProxyController controller = new ProxyController("http://dummyjson.com", cacheService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void request_returnsCachedResponse_withHitHeader() throws Exception {
        CachedResponse cached = new CachedResponse(
                200,
                Map.of("Content-Type", List.of("application/json")),
                "{\"id\":1,\"title\":\"iPhone 9\"}".getBytes()
        );
        when(cacheService.get("/products/1")).thenReturn(Optional.of(cached));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Cache", "HIT"))
                .andExpect(content().string("{\"id\":1,\"title\":\"iPhone 9\"}"));

        verify(cacheService, never()).put(any(), any());
    }

    @Test
    void request_preservesQueryStringInCacheKey() throws Exception {
        CachedResponse cached = new CachedResponse(
                200,
                Map.of("Content-Type", List.of("application/json")),
                "[]".getBytes()
        );
        when(cacheService.get("/products?limit=5")).thenReturn(Optional.of(cached));

        mockMvc.perform(get("/products").queryParam("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Cache", "HIT"));

        verify(cacheService).get("/products?limit=5");
    }

    @Test
    void request_differentPaths_lookupDifferentCacheKeys() throws Exception {
        when(cacheService.get("/products/1")).thenReturn(Optional.of(
                new CachedResponse(200, Map.of(), "p1".getBytes())));
        when(cacheService.get("/products/2")).thenReturn(Optional.of(
                new CachedResponse(200, Map.of(), "p2".getBytes())));

        mockMvc.perform(get("/products/1"))
                .andExpect(content().string("p1"))
                .andExpect(header().string("X-Cache", "HIT"));

        mockMvc.perform(get("/products/2"))
                .andExpect(content().string("p2"))
                .andExpect(header().string("X-Cache", "HIT"));
    }

    @Test
    void request_returns404_whenCachedResponseIs404() throws Exception {
        CachedResponse notFound = new CachedResponse(
                404,
                Map.of("Content-Type", List.of("application/json")),
                "{\"message\":\"Product not found\"}".getBytes()
        );
        when(cacheService.get("/products/9999")).thenReturn(Optional.of(notFound));

        mockMvc.perform(get("/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Cache", "HIT"));
    }
}
