package org.de013.cachingproxy.service;

import org.de013.cachingproxy.service.CacheService;
import org.de013.cachingproxy.util.Messages;

public class AppServiceImpl implements AppService {

    @Override
    public void help() {
        System.out.println("""
                Caching Proxy
                =============
                Usage: caching-proxy <command> [options]

                Commands:
                  help                        Show this help message
                  language --lang <vi|en>     Change display language

                  --port <port> --origin <url>   Start the caching proxy server
                  --clear-cache                  Delete all cached responses
                  --help                         Show this help message

                Options for --port:
                  --port    Port on which the proxy server will listen
                  --origin  URL of the target server to forward requests to

                Examples:
                  caching-proxy --port 3000 --origin http://dummyjson.com
                  caching-proxy --clear-cache
                  caching-proxy language --lang vi
                  caching-proxy help

                Response headers:
                  X-Cache: HIT   — response served from cache
                  X-Cache: MISS  — response fetched from origin server
                """);
    }

    @Override
    public void setLanguage(String language) {
        Messages.Language lang = language.equalsIgnoreCase("vi")
                ? Messages.Language.VI
                : Messages.Language.EN;
        Messages.setLanguage(lang);
        System.out.println(Messages.get("success.lang.changed", language.toUpperCase()));
    }

    @Override
    public void clearCache() {
        CacheService.deleteCache();
    }
}
