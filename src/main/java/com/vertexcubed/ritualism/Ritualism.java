package com.vertexcubed.ritualism;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import com.vertexcubed.ritualism.common.registry.ItemRegistry;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import com.vertexcubed.ritualism.common.registry.TabRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Ritualism.MOD_ID)
public class Ritualism
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String MOD_ID = "ritualism";
    public static final Logger LOGGER = LogUtils.getLogger();
    public Ritualism() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        modEventBus.addListener(BlockRegistry::registerBlockItems);
        TabRegistry.register(modEventBus);
        RecipeRegistry.register(modEventBus);

        modEventBus.register(this);
        modEventBus.register(RitualismClient.class);
    }


}
