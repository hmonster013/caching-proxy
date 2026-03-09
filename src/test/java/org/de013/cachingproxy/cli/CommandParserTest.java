package org.de013.cachingproxy.cli;

import org.de013.cachingproxy.cli.command.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommandParserTest {

    @Test
    void parse_help_returnsHelpCommand() {
        Command cmd = new CommandParser(new String[]{"help"}).parse();
        assertThat(cmd).isInstanceOf(HelpCommand.class);
    }

    @Test
    void parse_helpFlag_returnsHelpCommand() {
        Command cmd = new CommandParser(new String[]{"--help"}).parse();
        assertThat(cmd).isInstanceOf(HelpCommand.class);
    }

    @Test
    void parse_helpCaseInsensitive_returnsHelpCommand() {
        Command cmd = new CommandParser(new String[]{"HELP"}).parse();
        assertThat(cmd).isInstanceOf(HelpCommand.class);
    }

    @Test
    void parse_languageVI_returnsLanguageCommand() {
        Command cmd = new CommandParser(new String[]{"language", "--lang", "vi"}).parse();
        assertThat(cmd).isInstanceOf(LanguageCommand.class);
    }

    @Test
    void parse_languageEN_returnsLanguageCommand() {
        Command cmd = new CommandParser(new String[]{"language", "--lang", "en"}).parse();
        assertThat(cmd).isInstanceOf(LanguageCommand.class);
    }

    @Test
    void parse_languageInvalid_returnsNull() {
        Command cmd = new CommandParser(new String[]{"language", "--lang", "xx"}).parse();
        assertThat(cmd).isNull();
    }

    @Test
    void parse_languageMissingLang_returnsNull() {
        Command cmd = new CommandParser(new String[]{"language"}).parse();
        assertThat(cmd).isNull();
    }

    @Test
    void parse_clearCache_returnsClearCacheCommand() {
        Command cmd = new CommandParser(new String[]{"--clear-cache"}).parse();
        assertThat(cmd).isInstanceOf(ClearCacheCommand.class);
    }

    @Test
    void parse_portAndOrigin_returnsStartServerCommand() {
        Command cmd = new CommandParser(new String[]{"--port", "3000", "--origin", "http://dummyjson.com"}).parse();
        assertThat(cmd).isInstanceOf(StartServerCommand.class);
        StartServerCommand start = (StartServerCommand) cmd;
        assertThat(start.getPort()).isEqualTo("3000");
        assertThat(start.getOrigin()).isEqualTo("http://dummyjson.com");
    }

    @Test
    void parse_missingOrigin_returnsNull() {
        Command cmd = new CommandParser(new String[]{"--port", "3000"}).parse();
        assertThat(cmd).isNull();
    }

    @Test
    void parse_missingPort_returnsNull() {
        Command cmd = new CommandParser(new String[]{"--origin", "http://dummyjson.com"}).parse();
        assertThat(cmd).isNull();
    }

    @Test
    void parse_invalidPort_returnsNull() {
        Command cmd = new CommandParser(new String[]{"--port", "99999", "--origin", "http://dummyjson.com"}).parse();
        assertThat(cmd).isNull();
    }

    @Test
    void parse_unknownCommand_returnsNull() {
        Command cmd = new CommandParser(new String[]{"foobar"}).parse();
        assertThat(cmd).isNull();
    }

    @Test
    void parse_emptyArgs_returnsNull() {
        Command cmd = new CommandParser(new String[]{}).parse();
        assertThat(cmd).isNull();
    }
}
