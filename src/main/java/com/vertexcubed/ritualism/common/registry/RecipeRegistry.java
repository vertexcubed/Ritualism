package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.recipe.EmptyingRecipe;
import com.vertexcubed.ritualism.common.recipe.FillingRecipe;
import com.vertexcubed.ritualism.common.recipe.MixingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Ritualism.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Ritualism.MOD_ID);


    //Filling
    public static final RegistryObject<RecipeSerializer<FillingRecipe>> FILLING_SERIALIZER =
            RECIPE_SERIALIZERS.register("filling", FillingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<FillingRecipe>> FILLING_TYPE =
            RECIPE_TYPES.register("filling", () -> new RecipeType<>() {});

    //Emptying
    public static final RegistryObject<RecipeSerializer<EmptyingRecipe>> EMPTYING_SERIALIZER =
            RECIPE_SERIALIZERS.register("emptying", EmptyingRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<EmptyingRecipe>> EMPTYING_TYPE =
            RECIPE_TYPES.register("emptying", () -> new RecipeType<>() {});


    //MIXING
    public static final RegistryObject<RecipeSerializer<MixingRecipe>> MIXING_SERIALIZER =
            RECIPE_SERIALIZERS.register("mixing", MixingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<MixingRecipe>> MIXING_TYPE =
            RECIPE_TYPES.register("mixing", () -> new RecipeType<>() {});

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
