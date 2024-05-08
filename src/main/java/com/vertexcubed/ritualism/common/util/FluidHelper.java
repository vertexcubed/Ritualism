package com.vertexcubed.ritualism.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidHelper {

    public static FluidStack fluidStackFromJson(JsonObject jsonObject) {
        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if(fluid == null) {
            throw new JsonSyntaxException("Unknown fluid: " + id);
        }
        int amount;
        if(jsonObject.has("amount")) {
            amount = GsonHelper.getAsInt(jsonObject, "amount");
        }
        else {
            amount = 1;
        }

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
}
