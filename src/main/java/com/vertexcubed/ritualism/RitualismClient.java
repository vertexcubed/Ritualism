package com.vertexcubed.ritualism;

import com.vertexcubed.ritualism.client.render.block.MixingCauldronRenderer;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RitualismClient {


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockRegistry.MIXING_CAULDRON_BLOCK_ENTITY.get(), MixingCauldronRenderer::new);
    }
}
