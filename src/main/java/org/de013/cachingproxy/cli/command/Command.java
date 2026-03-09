package org.de013.cachingproxy.cli.command;


import org.de013.cachingproxy.service.ServiceContext;

public interface Command {
    void execute(ServiceContext ctx);
}
