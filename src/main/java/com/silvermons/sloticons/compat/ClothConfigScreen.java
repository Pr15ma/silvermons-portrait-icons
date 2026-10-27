package com.silvermons.sloticons.compat;

import com.silvermons.sloticons.SlotIconsConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class ClothConfigScreen {
    private ClothConfigScreen() {
    }

    static Screen build(Screen parent) {
        SlotIconsConfig cfg = SlotIconsConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("silvermons_slot_icons.config.title"))
                .setSavingRunnable(SlotIconsConfig::save);
        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("silvermons_slot_icons.config.general"));

        general.addEntry(eb.startBooleanToggle(Component.translatable("silvermons_slot_icons.config.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("silvermons_slot_icons.config.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.enabled = v)
                .build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("silvermons_slot_icons.config.background"), cfg.showBackground)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("silvermons_slot_icons.config.background.tooltip"))
                .setSaveConsumer(v -> cfg.showBackground = v)
                .build());
        general.addEntry(eb.startFloatField(Component.translatable("silvermons_slot_icons.config.scale"), cfg.scale)
                .setDefaultValue(1.0f)
                .setMin(0.1f)
                .setMax(4.0f)
                .setSaveConsumer(v -> cfg.scale = v)
                .build());
        general.addEntry(eb.startFloatField(Component.translatable("silvermons_slot_icons.config.offset_x"), cfg.offsetX)
                .setDefaultValue(0f)
                .setMin(-16f)
                .setMax(16f)
                .setSaveConsumer(v -> cfg.offsetX = v)
                .build());
        general.addEntry(eb.startFloatField(Component.translatable("silvermons_slot_icons.config.offset_y"), cfg.offsetY)
                .setDefaultValue(0f)
                .setMin(-16f)
                .setMax(16f)
                .setSaveConsumer(v -> cfg.offsetY = v)
                .build());

        ConfigCategory perMon = builder.getOrCreateCategory(Component.translatable("silvermons_slot_icons.config.per_pokemon"));
        perMon.addEntry(eb.startStrList(Component.translatable("silvermons_slot_icons.config.keep_model"), new ArrayList<>(cfg.keepModel))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("silvermons_slot_icons.config.keep_model.tooltip"))
                .setSaveConsumer(v -> cfg.keepModel = new ArrayList<>(v))
                .build());
        perMon.addEntry(eb.startStrList(Component.translatable("silvermons_slot_icons.config.overrides"), encode(cfg))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("silvermons_slot_icons.config.overrides.tooltip"))
                .setCellErrorSupplier(s -> parse(s) == null
                        ? Optional.of(Component.translatable("silvermons_slot_icons.config.overrides.invalid"))
                        : Optional.empty())
                .setSaveConsumer(v -> decode(cfg, v))
                .build());
        return builder.build();
    }

    private static List<String> encode(SlotIconsConfig cfg) {
        List<String> out = new ArrayList<>();
        cfg.overrides.forEach((k, a) -> out.add(k + ":" + a.scale + "," + a.offsetX + "," + a.offsetY));
        return out;
    }

    private static void decode(SlotIconsConfig cfg, List<String> lines) {
        cfg.overrides.clear();
        for (String line : lines) {
            Object[] parsed = parse(line);
            if (parsed != null) {
                cfg.overrides.put((String) parsed[0], (SlotIconsConfig.Adjust) parsed[1]);
            }
        }
    }

    private static Object[] parse(String line) {
        try {
            int colon = line.lastIndexOf(':');
            if (colon <= 0) {
                return null;
            }
            String key = line.substring(0, colon).trim().toLowerCase();
            String[] parts = line.substring(colon + 1).split(",");
            if (key.isEmpty() || parts.length != 3) {
                return null;
            }
            SlotIconsConfig.Adjust a = new SlotIconsConfig.Adjust();
            a.scale = Float.parseFloat(parts[0].trim());
            a.offsetX = Float.parseFloat(parts[1].trim());
            a.offsetY = Float.parseFloat(parts[2].trim());
            return new Object[]{key, a};
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
