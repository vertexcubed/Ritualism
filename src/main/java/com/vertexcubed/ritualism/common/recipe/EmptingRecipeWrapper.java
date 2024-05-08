package com.vertexcubed.ritualism.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class EmptingRecipeWrapper extends RecipeWrapper {
    public EmptingRecipeWrapper(ItemStack stack) {
        super(new ItemStackHandler(1));
        setItem(0, stack);
    }

    public ItemStack getInput() {
        return getItem(0);
    }
}
