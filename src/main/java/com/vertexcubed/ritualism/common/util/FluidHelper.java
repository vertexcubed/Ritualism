package com.vertexcubed.ritualism.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidHelper {

    public static FluidStack fluidStackFromJson(JsonObject jsonObject) {
        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if(fluid == null) {
            throw new JsonSyntaxException("Unknown fluid: " + id);
        }
        int amount = jsonObject.has("amount") ? GsonHelper.getAsInt(jsonObject, "amount") : 1;

        FluidStack output = new FluidStack(fluid, amount);
        if(!jsonObject.has("nbt")) {
            return output;
        }

        try {
            output.setTag(TagParser.parseTag(GsonHelper.getAsString(jsonObject, "nbt")));
        }
        catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static JsonObject fluidStackToJson(FluidStack stack) {
        JsonObject output = new JsonObject();
        output.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
        output.addProperty("amount", stack.getAmount());
        if(!stack.hasTag()) {
            return output;
        }
        output.addProperty("nbt", stack.getTag().toString());
        return output;
    }

    public static boolean canFill(IFluidHandler handler) {
        for(int i = 0; i < handler.getTanks(); i++) {
            if(handler.getFluidInTank(i).getAmount() < handler.getTankCapacity(i)) {
                return true;
            }
        }
        return false;
    }
    public static boolean canFill(IFluidHandler handler, FluidStack stack) {
        return handler.fill(stack, IFluidHandler.FluidAction.SIMULATE) > 0;

    }
}
