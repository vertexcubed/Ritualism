package com.vertexcubed.ritualism.common.fluid;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.recipe.EmptingRecipeWrapper;
import com.vertexcubed.ritualism.common.recipe.EmptyingRecipe;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;
/**
 * Todo: This shit is a mess. Like it's genuinely fucking awful. Clean it up, please.
 */
public class ItemEmptying {

    public static boolean canItemBeEmptied(Level level, ItemStack stack) {
        //todo: potions

        if(level.getRecipeManager().getRecipeFor(RecipeRegistry.EMPTYING_TYPE.get(), new EmptingRecipeWrapper(stack), level).isPresent()) {
            return true;
        }

        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler != null) {
            for(int i = 0; i < handler.getTanks(); i++) {
                if(handler.getFluidInTank(i).getAmount() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Fluid sensitive version.
     */
    public static boolean canItemBeEmptied(Level level, ItemStack stack, Fluid fluid) {
        //todo: potions


        if(fluid.isSame(Fluids.EMPTY)) {
            return false;
        }

        Optional<EmptyingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeRegistry.EMPTYING_TYPE.get(), new EmptingRecipeWrapper(stack), level);
        if(optional.isPresent()) {
            FluidStack resultingFluid = optional.get().getResultingFluid();
            return resultingFluid.getFluid().isSame(fluid);
        }

        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler != null) {
            for(int i = 0; i < handler.getTanks(); i++) {
                if(handler.getFluidInTank(i).getFluid().isSame(fluid)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static Pair<ItemStack, FluidStack> emptyItem(Level level, ItemStack stack, boolean simulate) {
        return emptyItem(level, stack, Fluids.EMPTY, Integer.MAX_VALUE, simulate);
    }

    public static Pair<ItemStack, FluidStack> emptyItem(Level level, ItemStack stack, Fluid fluid, boolean simulate) {
        return emptyItem(level, stack, fluid, Integer.MAX_VALUE, simulate);
    }

    /**
     * Attempts to empty a generic item. If simulate is true, item input is not modified..
     * @return A pair, the first being the new item stack and the second being the amount of fluid that was emptied.
     */
    public static Pair<ItemStack, FluidStack> emptyItem(Level level, ItemStack stack, Fluid fluid, int amount, boolean simulate) {

        Optional<EmptyingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeRegistry.EMPTYING_TYPE.get(), new EmptingRecipeWrapper(stack), level);
        if(optional.isPresent()) {
            EmptyingRecipe recipe = optional.get();
            ItemStack output = recipe.getResultItem(level.registryAccess());
            FluidStack fluidOut = recipe.getResultingFluid();
            if((!fluid.isSame(Fluids.EMPTY) && !fluidOut.getFluid().isSame(fluid)) || amount < fluidOut.getAmount()) {
                return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
            }
            if(!simulate) {
                stack.shrink(1);
            }
            return Pair.of(output, fluidOut);
        }

        ItemStack newStack = stack.copy();
        newStack.setCount(1);
        IFluidHandlerItem handler = newStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler == null) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }
        FluidStack drained;
        if(fluid.isSame(Fluids.EMPTY)) {
            drained = handler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        }
        else {
            drained = handler.drain(new FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);
        }
        ItemStack output = handler.getContainer();
        if(!simulate) {
            stack.shrink(1);
        }
        return Pair.of(output, drained);
    }
}
