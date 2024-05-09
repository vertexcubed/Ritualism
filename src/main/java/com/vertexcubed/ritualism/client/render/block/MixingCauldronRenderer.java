package com.vertexcubed.ritualism.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.block.MixingCauldronBlock;
import com.vertexcubed.ritualism.common.blockentity.MixingCauldronBlockEntity;
import com.vertexcubed.ritualism.common.util.Maath;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.awt.*;

public class MixingCauldronRenderer implements BlockEntityRenderer<MixingCauldronBlockEntity> {

    public MixingCauldronRenderer(BlockEntityRendererProvider.Context context) {

    }

    private static final float CORNER_FLUID = (float) MixingCauldronBlock.INSIDE.min(Direction.Axis.X);
    private static final float BOTTOM_FLUID = (float) MixingCauldronBlock.INSIDE.min(Direction.Axis.Y);
    private static final float TOP_FLUID = (float) MixingCauldronBlock.INSIDE.max(Direction.Axis.Y) - (1.0f / 16.0f);


    @Override
    public void render(MixingCauldronBlockEntity be, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        FluidStack fluid = be.getFluid();
        float percent = (float) fluid.getAmount() / be.getCapacity();
        if(!fluid.isEmpty()) {
            renderFluid(be, poseStack, pBuffer, fluid, percent, pPackedLight);
        }

        IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(handler == null) {
            Ritualism.LOGGER.debug("Item cap not synced to client!");
            return;
        }

        float height = BOTTOM_FLUID + (TOP_FLUID - BOTTOM_FLUID) * percent;


        poseStack.pushPose();
        poseStack.translate(0.5, height, 0.5);
        poseStack.scale(0.25f, 0.25f, 0.25f);
//        ItemStack firstItem = handler.getStackInSlot(0);
//        if(!firstItem.isEmpty()) {
//            renderItem(firstItem, pPartialTick, poseStack, pBuffer, be.getLevel(), pPackedLight);
//        }

        Vector2f rotVec = new Vector2f(0f, 1.125f);

        RandomSource random = RandomSource.create(1337);

        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            Vector2f newVec = Maath.rotateDegrees(rotVec, i * 60);
            poseStack.pushPose();
            float yOffset = randomSin(be.getLevel(), pPartialTick, random, 0.05f);
            poseStack.translate(newVec.x, yOffset / 8.0f, newVec.y);

            float rotXOffset = randomSin(be.getLevel(), pPartialTick, random, 0.05f);
            float rotZOffset = randomSin(be.getLevel(), pPartialTick, random, 0.025f);

            poseStack.mulPose(Axis.XP.rotationDegrees(90).mul(Axis.ZP.rotationDegrees(i * 60 + (rotZOffset * 10))).mul(Axis.XP.rotationDegrees(-35 + (rotXOffset * 7))));
            renderItem(stack, poseStack, pBuffer, be.getLevel(), pPackedLight);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static float randomSin(Level level, float partialTick, RandomSource random, float speed) {
        return Mth.sin((level.getGameTime() + partialTick + (5 * random.nextInt(100))) / ((1.0f / speed) + (random.nextFloat() * 5.0f)));
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

    private void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource mbs, Level level, int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, poseStack, mbs, level, 1);
    }
}
