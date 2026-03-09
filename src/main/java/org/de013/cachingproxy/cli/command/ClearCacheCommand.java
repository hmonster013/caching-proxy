package org.de013.cachingproxy.cli.command;

import org.de013.cachingproxy.service.ServiceContext;

public class ClearCacheCommand implements Command {

    @Override
    public void execute(ServiceContext ctx) {
        ctx.getAppService().clearCache();
    }
}
