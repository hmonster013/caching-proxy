package org.de013.cachingproxy.service;

import org.de013.cachingproxy.util.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class AppServiceImplTest {

    private AppServiceImpl service;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        service = new AppServiceImpl();
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // Reset language to EN before each test
        Messages.setLanguage(Messages.Language.EN);
    }

    // ── help ──────────────────────────────────────────────────────────────────

    @Test
    void help_printsUsageInfo() {
        service.help();
        String output = out.toString();
        assertThat(output).contains("Caching Proxy");
        assertThat(output).contains("--port");
        assertThat(output).contains("--origin");
        assertThat(output).contains("--clear-cache");
        assertThat(output).contains("X-Cache");
        assertThat(output).contains("help");
        assertThat(output).contains("language --lang <vi|en>");
    }

    // ── setLanguage ───────────────────────────────────────────────────────────

    @Test
    void setLanguage_toVI_changesLanguage() {
        service.setLanguage("vi");
        assertThat(Messages.getCurrentLanguage()).isEqualTo(Messages.Language.VI);
        assertThat(out.toString()).contains("VI");
    }

    @Test
    void setLanguage_toEN_changesLanguage() {
        service.setLanguage("vi"); // switch to VI first
        out.reset();
        service.setLanguage("en");
        assertThat(Messages.getCurrentLanguage()).isEqualTo(Messages.Language.EN);
        assertThat(out.toString()).contains("EN");
    }

    @Test
    void setLanguage_caseInsensitive() {
        service.setLanguage("VI");
        assertThat(Messages.getCurrentLanguage()).isEqualTo(Messages.Language.VI);
        service.setLanguage("En");
        assertThat(Messages.getCurrentLanguage()).isEqualTo(Messages.Language.EN);
    }
}
