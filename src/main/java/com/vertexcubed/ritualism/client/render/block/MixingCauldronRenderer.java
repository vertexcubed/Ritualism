package com.vertexcubed.ritualism.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.block.MixingCauldronBlock;
import com.vertexcubed.ritualism.common.blockentity.MixingCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.joml.Matrix4f;

import java.awt.*;

public class MixingCauldronRenderer implements BlockEntityRenderer<MixingCauldronBlockEntity> {

    public MixingCauldronRenderer(BlockEntityRendererProvider.Context context) {

    }

    private static final float CORNER_FLUID = (float) MixingCauldronBlock.INSIDE.min(Direction.Axis.X);
    private static final float BOTTOM_FLUID = (float) MixingCauldronBlock.INSIDE.min(Direction.Axis.Y);
    private static final float TOP_FLUID = (float) MixingCauldronBlock.INSIDE.max(Direction.Axis.Y) - (1.0f / 16.0f);


    @Override
    public void render(MixingCauldronBlockEntity be, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        FluidStack fluidOld = be.getFluidOld();
        FluidStack fluid = be.getFluid();
        if(!fluid.isEmpty()) {
            float percent = (float) fluid.getAmount() / be.getCapacity();
            renderFluid(be, pPoseStack, pBuffer, fluid, percent, pPackedLight);
        }

        IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(handler == null) {
            Ritualism.LOGGER.debug("Item cap not synced to client!");
            return;
        }
        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack item = handler.getStackInSlot(i);
            if(item.isEmpty()) continue;

        }
    }

    /**
     * Mostly borrowed from Hexerei joe please dont sue me
     */
    private void renderFluid(MixingCauldronBlockEntity be, PoseStack poseStack, MultiBufferSource mbs, FluidStack fluid, float percent, int packedLight) {
        VertexConsumer con = mbs.getBuffer(RenderType.translucentNoCrumbling());
        IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidExtensions.getStillTexture());
        //colors are argb
        int color = fluidExtensions.getTintColor();
        float alpha = (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;


        Color waterColor = new Color(BiomeColors.getAverageWaterColor(be.getLevel(), be.getBlockPos()));
        //if fluid is water, use biome water color
        if(be.getFluid().getFluid().isSame(Fluids.WATER)) {
            red = waterColor.getRed()/255f;
            green = waterColor.getGreen()/255f;
            blue = waterColor.getBlue()/255f;
        }

        renderFluidQuad(poseStack.last().pose(), con, sprite, red, green, blue, alpha, percent, packedLight);
    }

    private void renderFluidQuad(Matrix4f matrix, VertexConsumer con, TextureAtlasSprite sprite, float r, float g, float b, float alpha, float percent, int packedLight){

        float height = BOTTOM_FLUID + (TOP_FLUID - BOTTOM_FLUID) * percent;
        float minU = sprite.getU(CORNER_FLUID * 16);
        float maxU = sprite.getU((1 - CORNER_FLUID) * 16);
        float minV = sprite.getV(CORNER_FLUID * 16);
        float maxV = sprite.getV((1 - CORNER_FLUID) * 16);
        con.vertex(matrix, CORNER_FLUID, height, CORNER_FLUID).color(r, g, b, alpha).uv(minU, minV).uv2(packedLight).normal(0, 1, 0).endVertex();
        con.vertex(matrix, CORNER_FLUID, height, 1 - CORNER_FLUID).color(r, g, b, alpha).uv(minU, maxV).uv2(packedLight).normal(0, 1, 0).endVertex();
        con.vertex(matrix, 1 - CORNER_FLUID, height, 1 - CORNER_FLUID).color(r, g, b, alpha).uv(maxU, maxV).uv2(packedLight).normal(0, 1, 0).endVertex();
        con.vertex(matrix, 1 - CORNER_FLUID, height, CORNER_FLUID).color(r, g, b, alpha).uv(maxU, minV).uv2(packedLight).normal(0, 1, 0).endVertex();
    }

    private void renderItem(ItemStack stack, Level level, float partialTick, PoseStack poseStack, MultiBufferSource mbs,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, poseStack, mbs, level, 1);
    }
}
