package com.vertexcubed.ritualism.common.fluid;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.recipe.FillingRecipe;
import com.vertexcubed.ritualism.common.recipe.FillingRecipeWrapper;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import com.vertexcubed.ritualism.common.util.FluidHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

public class ItemFilling {

    public static boolean canItemBeFilled(Level level, ItemStack stack) {
        return canItemBeFilled(level, stack, FluidStack.EMPTY);
    }
    public static boolean canItemBeFilled(Level level, ItemStack stack, FluidStack fluid) {
        //todo: potions

        if(!fluid.isEmpty()) {
            FillingRecipeWrapper wrapper = new FillingRecipeWrapper(stack, fluid);
            Optional<FillingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.FILLING_TYPE.get(), wrapper, level);
            if(recipe.isPresent()) {
                return true;
            }
        }
        ItemStack split = stack.copy();
        split.setCount(1);

        IFluidHandlerItem handler = split.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler != null) {
            if(fluid.isEmpty()) {
                return FluidHelper.canFill(handler);
            }
            return FluidHelper.canFill(handler, fluid);
        }
        return false;

    }

    /**
     * Attempts to fill a generic item with a fluid. If simulate is true, item input is not modified. Fluid is not modified.
     * @return A pair, the first being the new item stack and the second being the amount of fluid that was filled.
     */
    public static Pair<ItemStack, FluidStack> fillItem(Level level, ItemStack stack, FluidStack fluid, boolean simulate) {

        FluidStack filled = fluid.copy();

        FillingRecipeWrapper wrapper = new FillingRecipeWrapper(stack, fluid);
        Optional<FillingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeRegistry.FILLING_TYPE.get(), wrapper, level);
        if(optional.isPresent()) {
            FillingRecipe recipe = optional.get();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            FluidStack drain = recipe.getFluid();
            if(drain.getAmount() < fluid.getAmount()) {
                filled.setAmount(drain.getAmount());
            }
            if(!simulate) {
                stack.shrink(1);
            }
            return Pair.of(result, filled);
        }

        ItemStack newStack = stack.copy();
        newStack.setCount(1);
        IFluidHandlerItem handler = newStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler == null) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }
        int fillAmount = handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        if(fillAmount < fluid.getAmount()) {
            filled.setAmount(fillAmount);
        }
        ItemStack output = handler.getContainer();
        if(!simulate) {
            stack.shrink(1);
        }
        return Pair.of(output, filled);

    }
}
