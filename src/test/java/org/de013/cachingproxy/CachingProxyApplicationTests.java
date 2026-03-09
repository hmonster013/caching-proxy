package org.de013.cachingproxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "server.port=0",
        "proxy.origin=http://dummyjson.com"
})
class CachingProxyApplicationTests {

    @Test
    void contextLoads() {
    }

}
