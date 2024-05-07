package com.vertexcubed.ritualism.common.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHelper {
    public static SimpleContainer createContainerFromHandler(IItemHandler handler) {
        SimpleContainer out = new SimpleContainer(handler.getSlots());
        for(int i = 0; i < handler.getSlots(); i++) {
            out.setItem(i, handler.getStackInSlot(i).copy());
        }
        return out;
    }

    public static int getFirstValidSlot(IItemHandler handler, ItemStack stack) {
        for(int i = 0; i < handler.getSlots(); i++) {
            //if the stack is unchanged, insertion did not work.
            if(!handler.insertItem(i, stack, true).equals(stack, false)) return i;
        }
        return -1;
    }

    public static boolean isFull(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getCount() < itemHandler.getSlotLimit(slot)) {
                return false;
            }
        }
        return true;
    }
}
