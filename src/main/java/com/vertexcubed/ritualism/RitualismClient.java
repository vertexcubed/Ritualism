package com.vertexcubed.ritualism;

import com.vertexcubed.ritualism.client.render.block.MixingCauldronRenderer;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import com.vertexcubed.ritualism.common.registry.FluidRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RitualismClient {


    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FluidRegistry.FLUIDS.getEntries().forEach(fluid -> {
                ItemBlockRenderTypes.setRenderLayer(fluid.still(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(fluid.flowing(), RenderType.translucent());
            });
        });
    }


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockRegistry.MIXING_CAULDRON_BLOCK_ENTITY.get(), MixingCauldronRenderer::new);
    }
}
