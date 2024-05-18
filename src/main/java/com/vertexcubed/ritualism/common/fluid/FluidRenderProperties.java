package com.vertexcubed.ritualism.common.fluid;

import net.minecraft.resources.ResourceLocation;

public class FluidRenderProperties {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    private final ResourceLocation renderOverlayTexture;
    private final int tintColor;

    private FluidRenderProperties(ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, ResourceLocation renderOverlayTexture, int tintColor) {

        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.renderOverlayTexture = renderOverlayTexture;
        this.tintColor = tintColor;
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    public ResourceLocation getRenderOverlayTexture() {
        return renderOverlayTexture;
    }

    public int getTintColor() {
        return tintColor;
    }


    public static class Builder {
        private ResourceLocation stillTexture = new ResourceLocation("block/water_still");
        private ResourceLocation flowingTexture = new ResourceLocation("block/water_flow");
        private ResourceLocation overlayTexture = new ResourceLocation("block/water_overlay");
        private ResourceLocation renderOverlayTexture = new ResourceLocation("textures/misc/underwater");
        private int tintColor = 0xFFFFFFFF;

        public Builder stillTexture(ResourceLocation texture) {
            stillTexture = texture;
            return this;
        }
        public Builder flowingTexture(ResourceLocation texture) {
            flowingTexture = texture;
            return this;
        }
        public Builder overlayTexture(ResourceLocation texture) {
            overlayTexture = texture;
            return this;
        }
        public Builder renderOverlayTexture(ResourceLocation texture) {
            renderOverlayTexture = texture;
            return this;
        }
        public Builder tintColor(int color) {
            tintColor = color;
            return this;
        }
        public FluidRenderProperties build() {
            return new FluidRenderProperties(stillTexture, flowingTexture, overlayTexture, renderOverlayTexture, tintColor);
        }
    }
}
