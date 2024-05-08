package com.vertexcubed.ritualism.common.recipe;

import com.vertexcubed.ritualism.Ritualism;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FillingRecipeWrapper extends RecipeWrapper {

    private final FluidStack fluid;
    public FillingRecipeWrapper(ItemStack stack, FluidStack fluid) {
        super(new ItemStackHandler(1));
        this.fluid = fluid;
        setItem(0, stack);
    }

    public ItemStack getInput() {

        return this.getItem(0);
    }
    public FluidStack getFluid() {
        return fluid;
    }
}
