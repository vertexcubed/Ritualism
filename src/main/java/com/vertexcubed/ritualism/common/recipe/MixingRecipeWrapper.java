package com.vertexcubed.ritualism.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class MixingRecipeWrapper extends RecipeWrapper {

    private FluidStack fluid;
    public MixingRecipeWrapper(NonNullList<ItemStack> stacks, FluidStack fluid) {
        super(new ItemStackHandler(stacks));
        if(stacks.size() > 6) throw new IllegalArgumentException("Cannot have mixing recipe with more than 6 ingredients!");
        this.fluid = fluid;
    }

    public FluidStack getFluid() {
        return fluid;
    }
}
