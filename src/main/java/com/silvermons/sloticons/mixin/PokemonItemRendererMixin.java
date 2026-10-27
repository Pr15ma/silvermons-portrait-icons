package com.silvermons.sloticons.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.silvermons.sloticons.PortraitIconRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.cobblemon.mod.common.client.render.item.PokemonItemRenderer", remap = false)
public abstract class PokemonItemRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void silvermons$slotPortrait(ItemStack stack, ItemDisplayContext mode, PoseStack matrices,
                                         MultiBufferSource vertexConsumers, int light, int overlay,
                                         CallbackInfo ci) {
        if (mode == ItemDisplayContext.GUI && PortraitIconRenderer.render(stack, matrices)) {
            ci.cancel();
        }
    }
}
