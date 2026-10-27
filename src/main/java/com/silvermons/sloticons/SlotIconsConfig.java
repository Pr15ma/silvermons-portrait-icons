package com.silvermons.sloticons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class SlotIconsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public boolean showBackground = false;
    public float scale = 1.0f;
    public float offsetX = 0f;
    public float offsetY = 0f;
    public Map<String, Adjust> overrides = new LinkedHashMap<>();
    public java.util.List<String> keepModel = new java.util.ArrayList<>();

    public static final class Adjust {
        public float scale = 1.0f;
        public float offsetX = 0f;
        public float offsetY = 0f;
    }

    private static SlotIconsConfig instance = new SlotIconsConfig();

    public static SlotIconsConfig get() {
        return instance;
    }

    public static void load() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("silvermons_slot_icons.json");
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file)) {
                SlotIconsConfig read = GSON.fromJson(r, SlotIconsConfig.class);
                if (read != null) {
                    instance = read;
                }
            } catch (Exception e) {
                SlotIconsClient.LOGGER.warn("Could not read {} - using defaults ({})", file.getFileName(), e.toString());
            }
        }
        save();
    }

    public static void save() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("silvermons_slot_icons.json");
        try (Writer w = Files.newBufferedWriter(file)) {
            GSON.toJson(instance, w);
        } catch (IOException e) {
            SlotIconsClient.LOGGER.warn("Could not write {}: {}", file.getFileName(), e.toString());
        }
    }

    private static String[] keys(String species, Set<String> aspects) {
        String[] k = new String[aspects.size() + 1];
        int i = 0;
        for (String a : aspects) {
            k[i++] = species + "|" + a;
        }
        k[i] = species;
        return k;
    }

    public boolean keepsModel(String species, Set<String> aspects) {
        for (String k : keys(species, aspects)) {
            if (keepModel.contains(k)) {
                return true;
            }
        }
        return false;
    }

    public Adjust adjustFor(String species, Set<String> aspects) {
        for (String k : keys(species, aspects)) {
            Adjust a = overrides.get(k);
            if (a != null) {
                return a;
            }
        }
        return null;
    }
}
