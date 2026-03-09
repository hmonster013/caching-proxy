package org.de013.cachingproxy.service;

public class ServiceContext {
    private final AppService      appService;

    public ServiceContext(AppService appService) {
        this.appService      = appService;
    }

    public AppService getAppService() {
        return appService;
    }
}
