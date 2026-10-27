package com.silvermons.sloticons;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public final class PortraitIconRenderer {
    private static final float PORTRAIT_DIAMETER = 21f;
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/gui/party/party_slot_portrait_background.png");

    private static boolean failed = false;

    private PortraitIconRenderer() {
    }

    public static boolean render(ItemStack stack, PoseStack pose) {
        SlotIconsConfig cfg = SlotIconsConfig.get();
        if (failed || !cfg.enabled || !(stack.getItem() instanceof PokemonItem item)) {
            return false;
        }
        Pair<Species, Set<String>> sa = item.getSpeciesAndAspects(stack);
        if (sa == null) {
            return false;
        }
        Species species = sa.getFirst();
        Set<String> aspects = sa.getSecond();
        String id = species.getResourceIdentifier().getPath();
        if (cfg.keepsModel(id, aspects)) {
            return false;
        }

        SlotIconsConfig.Adjust adj = cfg.adjustFor(id, aspects);
        float scale = cfg.scale * (adj != null ? adj.scale : 1f);
        float dx = cfg.offsetX + (adj != null ? adj.offsetX : 0f);
        float dy = cfg.offsetY + (adj != null ? adj.offsetY : 0f);

        Minecraft mc = Minecraft.getInstance();
        Matrix4f m = pose.last().pose();
        Vector3f topLeft = m.transformPosition(0f, 1f, 0f, new Vector3f());
        Vector3f bottomRight = m.transformPosition(1f, 0f, 0f, new Vector3f());

        Window window = mc.getWindow();
        double gui = window.getGuiScale();
        int x0 = (int) Math.round(topLeft.x * gui);
        int x1 = (int) Math.round(bottomRight.x * gui);
        int yTop = (int) Math.round(topLeft.y * gui);
        int yBottom = (int) Math.round(bottomRight.y * gui);
        int sx = x0;
        int sy = window.getHeight() - yBottom;
        int sw = x1 - x0;
        int sh = yBottom - yTop;

        boolean hadScissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        int[] old = new int[4];
        if (hadScissor) {
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, old);
            int nx = Math.max(sx, old[0]);
            int ny = Math.max(sy, old[1]);
            int nx2 = Math.min(sx + sw, old[0] + old[2]);
            int ny2 = Math.min(sy + sh, old[1] + old[3]);
            sx = nx;
            sy = ny;
            sw = nx2 - nx;
            sh = ny2 - ny;
        }
        if (sw <= 0 || sh <= 0) {
            return true;
        }

        mc.renderBuffers().bufferSource().endBatch();
        RenderSystem.enableScissor(sx, sy, sw, sh);
        pose.pushPose();
        try {
            pose.translate(0.5, 0.5, 0.5);
            pose.scale(1f / 16f, -1f / 16f, 1f / 16f);
            pose.translate(-8.0, -8.0, 0.0);
            pose.translate(dx, dy, 0.0);
            pose.translate(8.0, 8.0, 0.0);
            float k = 16f / PORTRAIT_DIAMETER * scale;
            pose.scale(k, k, k);
            pose.translate(-PORTRAIT_DIAMETER / 2.0, -PORTRAIT_DIAMETER / 2.0, 0.0);

            if (cfg.showBackground) {
                drawBackground(pose);
            }

            FloatingState state = new FloatingState();
            state.setCurrentAspects(aspects);
            Vector4f tint = item.tint(stack);
            pose.pushPose();
            pose.translate(PORTRAIT_DIAMETER / 2.0 - 1.0, -12.0, 0.0);
            GuiUtilsKt.drawPosablePortrait(
                    species.getResourceIdentifier(), pose, 13f, species.getForm(aspects).getBaseScale(), false,
                    state, 0f, 0f, 0f, 0f, 0f, 0f, false, tint.x, tint.y, tint.z, tint.w);
            pose.popPose();
        } catch (Throwable t) {
            failed = true;
            SlotIconsClient.LOGGER.error("Portrait slot rendering failed; reverting to Cobblemon's 3D item model", t);
        } finally {
            pose.popPose();
            mc.renderBuffers().bufferSource().endBatch();
            if (hadScissor) {
                RenderSystem.enableScissor(old[0], old[1], old[2], old[3]);
            } else {
                RenderSystem.disableScissor();
            }
        }
        return !failed;
    }

    private static void drawBackground(PoseStack pose) {
        Matrix4f mm = pose.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float s = PORTRAIT_DIAMETER;
        bb.addVertex(mm, 0f, s, 0f).setUv(0f, 1f);
        bb.addVertex(mm, s, s, 0f).setUv(1f, 1f);
        bb.addVertex(mm, s, 0f, 0f).setUv(1f, 0f);
        bb.addVertex(mm, 0f, 0f, 0f).setUv(0f, 0f);
        BufferUploader.drawWithShader(bb.buildOrThrow());
    }
}
