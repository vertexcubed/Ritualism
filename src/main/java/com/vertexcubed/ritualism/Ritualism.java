package com.vertexcubed.ritualism;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.vertexcubed.ritualism.api.multiblock.MultiBlock;
import com.vertexcubed.ritualism.common.registry.*;
import com.vertexcubed.ritualism.server.network.PacketRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

@Mod(Ritualism.MOD_ID)
public class Ritualism
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String MOD_ID = "ritualism";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }


    public Ritualism() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        FluidRegistry.register(modEventBus);
        modEventBus.addListener(BlockRegistry::registerBlockItems);
        TabRegistry.register(modEventBus);
        RecipeRegistry.register(modEventBus);
        StateMatcherRegistry.register(modEventBus);

        modEventBus.register(this);
        modEventBus.register(RitualismClient.class);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        PacketRegistry.register();
        event.enqueueWork(() -> {
            FluidRegistry.registerDispenserBehavior();
        });
    }


    @SubscribeEvent
    public void datapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MultiBlock.REGISTRY, MultiBlock.CODEC, MultiBlock.CODEC);
    }
}
