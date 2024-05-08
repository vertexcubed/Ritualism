package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.recipe.FillingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Ritualism.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Ritualism.MOD_ID);


    public static final RegistryObject<RecipeSerializer<FillingRecipe>> FILLING_SERIALIZER =
            RECIPE_SERIALIZERS.register("filling", () -> FillingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<FillingRecipe>> FILLING_TYPE =
            RECIPE_TYPES.register("filling", () -> FillingRecipe.Type.INSTANCE);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
