package org.de013.cachingproxy.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private static final Path CONFIG_FILE = Paths.get("language.config");
    private static Language currentLanguage = Language.EN;

    public enum Language {
        VI, EN
    }

    private static final Map<String, Map<Language, String>> messages = new HashMap<>();

    static {
        loadLanguagePreference();
        initMessages();
    }

    private static void initMessages() {
        // Language
        addMessage("error.lang.invalid",
                "Error: Invalid language! Use: language --lang vi or --lang en",
                "Loi: Ngon ngu khong hop le! Dung: language --lang vi hoac --lang en");
        addMessage("success.lang.changed",
                "Language changed to {0}",
                "Da doi ngon ngu sang {0}");
    }

    private static void addMessage(String key, String en, String vi) {
        Map<Language, String> translations = new HashMap<>();
        translations.put(Language.EN, en);
        translations.put(Language.VI, vi);
        messages.put(key, translations);
    }

    public static String get(String key, Object... params) {
        Map<Language, String> translations = messages.get(key);
        if (translations == null) return key;
        String message = translations.get(currentLanguage);
        if (message == null) return key;
        for (int i = 0; i < params.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(params[i]));
        }
        return message;
    }

    public static void setLanguage(Language language) {
        currentLanguage = language;
        saveLanguagePreference();
    }

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    private static void loadLanguagePreference() {
        File file = new File(CONFIG_FILE.toString());
        if (!file.exists()) return;
        try {
            String lang = Files.readString(file.toPath()).trim();
            if ("VI".equalsIgnoreCase(lang)) {
                currentLanguage = Language.VI;
            } else if ("EN".equalsIgnoreCase(lang)) {
                currentLanguage = Language.EN;
            }
        } catch (IOException e) {
            // Use default language
        }
    }

    private static void saveLanguagePreference() {
        try {
            Files.writeString(CONFIG_FILE, currentLanguage.name());
        } catch (IOException e) {
            System.err.println("Cannot save language preference: " + e.getMessage());
        }
    }
}
