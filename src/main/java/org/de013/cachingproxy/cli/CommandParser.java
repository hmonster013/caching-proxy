package org.de013.cachingproxy.cli;

import org.de013.cachingproxy.cli.command.*;
import org.de013.cachingproxy.util.CommandConstants;
import org.de013.cachingproxy.util.Messages;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private String[] args;

    public CommandParser() {}

    public CommandParser(String[] args) {
        this.args = args;
    }

    public Command parse() {
        if (args == null || args.length == 0) {
            System.err.println(Messages.get("error.no_command"));
            return null;
        }

        String first = args[0].toLowerCase();

        // Flag-based commands (start with --)
        if (first.startsWith("--")) {
            Map<String, String> flags = parseFlags(args, 0);

            if (flags.containsKey("--help")) {
                return new HelpCommand();
            }
            if (flags.containsKey("--clear-cache")) {
                return new ClearCacheCommand();
            }
            if (flags.containsKey("--port") || flags.containsKey("--origin")) {
                return parseStartServer(flags);
            }
            System.err.println(Messages.get("error.unknown_command", first));
            return null;
        }

        // Subcommand-based commands
        switch (first) {
            case CommandConstants.LANGUAGE:
                return parseLanguage(parseFlags(args, 1));
            case CommandConstants.HELP:
                return new HelpCommand();
            default:
                System.err.println(Messages.get("error.unknown_command", first));
                return null;
        }
    }

    // ── --port / --origin ─────────────────────────────────────────────────────

    private Command parseStartServer(Map<String, String> flags) {
        String port   = flags.get("--port");
        String origin = flags.get("--origin");

        if (port == null || origin == null) {
            System.err.println("Usage: caching-proxy --port <port> --origin <url>");
            return null;
        }
        try {
            int p = Integer.parseInt(port);
            if (p < 1 || p > 65535) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.err.println("Error: --port must be a valid port number (1-65535).");
            return null;
        }
        return new StartServerCommand(port, origin);
    }

    // ── language [flags] ──────────────────────────────────────────────────────

    private Command parseLanguage(Map<String, String> flags) {
        String lang = flags.get("--lang");
        if (lang == null || (!lang.equalsIgnoreCase("vi") && !lang.equalsIgnoreCase("en"))) {
            System.err.println(Messages.get("error.lang.invalid"));
            return null;
        }
        return new LanguageCommand(lang);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Map<String, String> parseFlags(String[] args, int offset) {
        Map<String, String> flags = new HashMap<>();
        for (int i = offset; i < args.length - 1; i++) {
            if (args[i].startsWith("--")) {
                flags.put(args[i].toLowerCase(), args[i + 1]);
                i++;
            }
        }
        if (args.length > offset && args[args.length - 1].startsWith("--")) {
            flags.put(args[args.length - 1].toLowerCase(), "");
        }
        return flags;
    }
}

