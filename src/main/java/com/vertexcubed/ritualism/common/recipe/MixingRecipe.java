package com.vertexcubed.ritualism.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import com.vertexcubed.ritualism.common.util.FluidHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MixingRecipe implements Recipe<MixingRecipeWrapper> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final FluidStack fluid;
    private final FluidStack result;
    public MixingRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, FluidStack fluid, FluidStack result) {
        this.id = id;
        this.ingredients = ingredients;
        this.fluid = fluid;
        this.result = result;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(MixingRecipeWrapper wrapper, Level pLevel) {
        if(!wrapper.getFluid().containsFluid(fluid)) return false;

        List<Ingredient> ingredientsCopy = new ArrayList<>(ingredients.stream().toList());
        for(int i = 0; i < 6; i++) {
            boolean removedElement = false;
            for(int j = 0; j < ingredientsCopy.size(); j++) {
                if(ingredientsCopy.get(j).test(wrapper.getItem(i))) {
                    ingredientsCopy.remove(j);
                    removedElement = true;
                    break;
                }
            }
            if(!removedElement) return false;
        }
        return ingredientsCopy.isEmpty();
    }

    @Override
    public ItemStack assemble(MixingRecipeWrapper wrapper, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public FluidStack getResult() {
        return result;
    }

    /**
     * Do not use, obviously
     */
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.MIXING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.MIXING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MixingRecipe> {

        @Override
        public MixingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(6, Ingredient.EMPTY);
            JsonArray jsonIngredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            if(jsonIngredients.size() > 6) throw new JsonSyntaxException("Cannot have mixing recipe with more than 6 ingredients!");
            for(int i = 0; i < jsonIngredients.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(jsonIngredients.get(i)));
            }
            FluidStack fluid = FluidHelper.fluidStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid"));
            FluidStack result = FluidHelper.fluidStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            return new MixingRecipe(pRecipeId, ingredients, fluid, result);
        }

        @Override
        public @Nullable MixingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(6, Ingredient.EMPTY);
            for(int i = 0; i < 6; i++) {
                ingredients.add(Ingredient.fromNetwork(buf));
            }
            FluidStack fluid = FluidStack.readFromPacket(buf);
            FluidStack result = FluidStack.readFromPacket(buf);
            return new MixingRecipe(pRecipeId, ingredients, fluid, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MixingRecipe pRecipe) {
            pRecipe.ingredients.forEach(ing -> {
                ing.toNetwork(buf);
            });
            pRecipe.fluid.writeToPacket(buf);
            pRecipe.result.writeToPacket(buf);
        }
    }
}
