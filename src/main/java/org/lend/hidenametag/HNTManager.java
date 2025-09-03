package org.lend.hidenametag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.text.TextColor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HNTManager {
    private static final File CONFIG_FILE = new File("config/hidenametag.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HNTConfig config = new HNTConfig();

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, HNTConfig.class);
                System.out.println("[HideNameTag] config loaded.");
            } catch (IOException e) {
                System.err.println("[HideNameTag] load error: " + e.getMessage());
            }
        } else {
            saveConfig();
        }
    }

    public static void saveConfig() {
        CONFIG_FILE.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("[HideNameTag] save error: " + e.getMessage());
        }
    }

    public static void setNameColor(String hex) {
        config.nameColor = normalizeHex(hex);
        saveConfig();
    }

    public static void setBracketColor(String hex) {
        config.bracketColor = normalizeHex(hex);
        saveConfig();
    }

    public static TextColor getNameColor() {
        return parseColor(config.nameColor, 0xFFFFFF);
    }

    public static TextColor getBracketColor() {
        return parseColor(config.bracketColor, 0x084882);
    }

    private static String normalizeHex(String hex) {
        if (!hex.startsWith("#")) hex = "#" + hex;
        if (!hex.matches("#[0-9A-Fa-f]{6}")) {
            throw new IllegalArgumentException("Incorrect HEX code: " + hex);
        }
        return hex.toUpperCase();
    }

    private static TextColor parseColor(String hex, int fallback) {
        try {
            return TextColor.parse(hex).result().orElse(TextColor.fromRgb(fallback));
        } catch (Exception e) {
            return TextColor.fromRgb(fallback);
        }
    }
}
