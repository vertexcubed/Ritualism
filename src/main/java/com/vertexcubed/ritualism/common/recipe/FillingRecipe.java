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

public class FillingRecipe implements Recipe<FillingRecipeWrapper> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final FluidStack fluid;
    private final ItemStack result;

    public FillingRecipe(ResourceLocation id, Ingredient input, FluidStack fluid, ItemStack result) {
        this.id = id;
        this.input = input;
        this.fluid = fluid;
        this.result = result;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    @Override
    public boolean matches(FillingRecipeWrapper wrapper, Level level) {
        return input.test(wrapper.getInput()) && fluid.isFluidEqual(wrapper.getFluid());
    }


    @Override
    public ItemStack assemble(FillingRecipeWrapper wrapper, RegistryAccess registryAccess) {
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

    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FILLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FILLING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FillingRecipe> {

        @Override
        public FillingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            FluidStack fluid = FluidHelper.fluidStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            return new FillingRecipe(pRecipeId, input, fluid, result);
        }

        @Override
        public @Nullable FillingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            FluidStack fluid = FluidStack.readFromPacket(buf);
            ItemStack result = buf.readItem();
            return new FillingRecipe(pRecipeId, input, fluid, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FillingRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.fluid.writeToPacket(buf);
            buf.writeItem(recipe.result);
        }
    }
}
