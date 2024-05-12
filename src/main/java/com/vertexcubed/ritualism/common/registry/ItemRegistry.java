package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.item.ArcaneStaffItem;
import com.vertexcubed.ritualism.common.item.DebugItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ritualism.MOD_ID);
    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug_item", () -> new DebugItem(new Item.Properties()));

    public static final RegistryObject<Item> ARCANE_FOCUS = basicItem("arcane_focus");
    public static final RegistryObject<Item> ARCANE_STAFF = ITEMS.register("arcane_staff", () -> new ArcaneStaffItem(new Item.Properties()));


    public static RegistryObject<Item> basicItem(String name) {
        return ITEMS.register("arcane_focus", () -> new Item(new Item.Properties()));
    }
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
