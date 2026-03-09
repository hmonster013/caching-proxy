package org.de013.cachingproxy.cli.command;

import org.de013.cachingproxy.service.ServiceContext;

public class HelpCommand implements Command {

    @Override
    public void execute(ServiceContext ctx) {
        ctx.getAppService().help();
    }
}

