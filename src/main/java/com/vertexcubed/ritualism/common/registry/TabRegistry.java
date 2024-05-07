package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ritualism.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register(Ritualism.MOD_ID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Ritualism.MOD_ID))
                    .icon(() -> new ItemStack(Items.BARRIER))
                    .displayItems((parameters, output) -> {
                        ItemRegistry.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        //blocks
                    }).build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }

}
