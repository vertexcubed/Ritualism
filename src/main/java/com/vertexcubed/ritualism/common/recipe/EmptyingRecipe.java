package com.vertexcubed.ritualism.common.recipe;

import com.google.gson.JsonObject;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import com.vertexcubed.ritualism.common.util.FluidHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class EmptyingRecipe implements Recipe<EmptingRecipeWrapper> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack result;
    private final FluidStack fluid;

    public EmptyingRecipe(ResourceLocation id, Ingredient input, ItemStack result, FluidStack fluid) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.fluid = fluid;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(EmptingRecipeWrapper wrapper, Level level) {
        return input.test(wrapper.getInput());
    }


    @Override
    public ItemStack assemble(EmptingRecipeWrapper wrapper, RegistryAccess registryAccess) {
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getResultingFluid() {
        return fluid.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.EMPTYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.EMPTYING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<EmptyingRecipe> {

        @Override
        public EmptyingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            FluidStack fluid = FluidHelper.fluidStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid"));

            return new EmptyingRecipe(pRecipeId, input, result, fluid);
        }

        @Override
        public @Nullable EmptyingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            FluidStack fluid = FluidStack.readFromPacket(buf);
            return new EmptyingRecipe(pRecipeId, input, result, fluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EmptyingRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.result);
            recipe.fluid.writeToPacket(buf);
        }
    }
}
