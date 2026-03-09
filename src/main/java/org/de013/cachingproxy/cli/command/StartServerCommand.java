package org.de013.cachingproxy.cli.command;

import org.de013.cachingproxy.service.ServiceContext;

public class StartServerCommand implements Command {

    private final String port;
    private final String origin;

    public StartServerCommand(String port, String origin) {
        this.port = port;
        this.origin = origin;
    }

    public String getPort() { return port; }
    public String getOrigin() { return origin; }

    @Override
    public void execute(ServiceContext ctx) {
        System.setProperty("server.port", port);
        System.setProperty("proxy.origin", origin);
    }
}
