package com.vertexcubed.ritualism.common.fluid;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RitualismFluidType extends FluidType {

    private final FluidRenderProperties renderProperties;
    public RitualismFluidType(Properties properties, FluidRenderProperties renderProperties) {
        super(properties);
        this.renderProperties = renderProperties;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return renderProperties.getStillTexture();
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return renderProperties.getFlowingTexture();
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return renderProperties.getOverlayTexture();
            }

            @Override
            public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return renderProperties.getRenderOverlayTexture();
            }

            @Override
            public int getTintColor() {
                return renderProperties.getTintColor();
            }
        });
    }
}
