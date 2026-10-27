package com.silvermons.sloticons;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SlotIconsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("silvermons_slot_icons");

    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        SlotIconsConfig.load();
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.silvermons_slot_icons.toggle",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "key.categories.silvermons_slot_icons"));
        ClientTickEvents.END_CLIENT_TICK.register(SlotIconsClient::onTick);
    }

    private static void onTick(Minecraft mc) {
        while (toggleKey.consumeClick()) {
            SlotIconsConfig cfg = SlotIconsConfig.get();
            cfg.enabled = !cfg.enabled;
            SlotIconsConfig.save();
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.translatable(cfg.enabled
                        ? "silvermons_slot_icons.message.portrait"
                        : "silvermons_slot_icons.message.default"), true);
            }
        }
    }
}
