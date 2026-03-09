package org.de013.cachingproxy.cli.command;

import org.de013.cachingproxy.service.ServiceContext;

public class LanguageCommand implements Command {
    private String language;

    public LanguageCommand(String language) {
        this.language = language;
    }

    @Override
    public void execute(ServiceContext ctx) {
        ctx.getAppService().setLanguage(language);
    }
}
