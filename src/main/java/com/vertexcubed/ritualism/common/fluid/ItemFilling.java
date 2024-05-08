package com.vertexcubed.ritualism.common.fluid;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.recipe.FillingRecipe;
import com.vertexcubed.ritualism.common.recipe.FillingRecipeWrapper;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class ItemFilling {

    public static boolean canItemBeFilled(Level level, ItemStack stack) {
        return canItemBeFilled(level, stack, null);
    }
    public static boolean canItemBeFilled(Level level, ItemStack stack, FluidStack fluid) {
        //todo: potions

        if(fluid != null) {
            Ritualism.LOGGER.debug("Checking recipes. Item: " + stack);
            FillingRecipeWrapper wrapper = new FillingRecipeWrapper(stack, fluid);
            Optional<FillingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.FILLING_TYPE.get(), wrapper, level);
            if(recipe.isPresent()) {
                return true;
            }
        }

        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler != null) {
            for(int i = 0; i < handler.getTanks(); i++) {
                if(handler.getFluidInTank(i).getAmount() < handler.getTankCapacity(i)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Attempts to fill a generic item with a fluid. If simulate is true, item input is not modified. Fluid is not modified.
     * @return A pair, the first being the new item stack and the second being any leftover fluids that could not be filled.
     */
    public static Pair<ItemStack, FluidStack> fillItem(Level level, ItemStack stack, FluidStack fluid, boolean simulate) {

        FluidStack leftover = FluidStack.EMPTY;

        FillingRecipeWrapper wrapper = new FillingRecipeWrapper(stack, fluid);
        Optional<FillingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeRegistry.FILLING_TYPE.get(), wrapper, level);
        if(optional.isPresent()) {
            FillingRecipe recipe = optional.get();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            FluidStack drain = recipe.getFluid();
            if(drain.getAmount() < fluid.getAmount()) {
                leftover = fluid.copy();
                leftover.setAmount(fluid.getAmount()- drain.getAmount());
            }
            if(!simulate) {
                stack.shrink(1);
            }
            return Pair.of(result, leftover);
        }

        ItemStack newStack = stack.copy();
        IFluidHandlerItem handler = newStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(handler == null) {
            return Pair.of(ItemStack.EMPTY, leftover);
        }
        int fillAmount = handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        if(fillAmount < fluid.getAmount()) {
            leftover = fluid.copy();
            leftover.setAmount(fluid.getAmount() - fillAmount);
        }
        ItemStack output = handler.getContainer();
        if(!simulate) {
            stack.shrink(1);
        }
        return Pair.of(output, leftover);

    }
}
