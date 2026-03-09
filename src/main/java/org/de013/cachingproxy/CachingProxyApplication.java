package org.de013.cachingproxy;

import org.de013.cachingproxy.cli.CommandParser;
import org.de013.cachingproxy.cli.command.Command;
import org.de013.cachingproxy.cli.command.StartServerCommand;
import org.de013.cachingproxy.service.AppServiceImpl;
import org.de013.cachingproxy.service.ServiceContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CachingProxyApplication {

    public static void main(String[] args) {
        ServiceContext ctx = new ServiceContext(new AppServiceImpl());
        Command cmd = new CommandParser(args).parse();

        if (cmd == null) return;

        cmd.execute(ctx);

        if (cmd instanceof StartServerCommand) {
            SpringApplication.run(CachingProxyApplication.class, args);
        }
    }
}

