package com.vertexcubed.ritualism.common.item;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.api.multiblock.MultiBlock;
import com.vertexcubed.ritualism.common.fluid.ItemFilling;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class DebugItem extends Item {
    public DebugItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {


//        ItemStack toFill = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
//        Ritualism.LOGGER.debug("Can fill? " + ItemFilling.canItemBeFilled(pLevel, toFill, new FluidStack(Fluids.WATER, 1000)) + ", is client: " + pLevel.isClientSide);
//        Pair<ItemStack, FluidStack> fillResult = ItemFilling.fillItem(pLevel, toFill, new FluidStack(Fluids.WATER, 1000), false);
//        pPlayer.getInventory().add(fillResult.getFirst());
//        Ritualism.LOGGER.debug("Filled fluid: " + fillResult.getSecond().getAmount());

//        ItemStack toEmpty = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
//        Ritualism.LOGGER.debug("Can empty? " + ItemEmptying.canItemBeEmptied(pLevel, toEmpty) + ", is client: " + pLevel.isClientSide);
//        Pair<ItemStack, FluidStack> emptyResult = ItemEmptying.emptyItem(pLevel, toEmpty, false);
//        pPlayer.getInventory().add(emptyResult.getFirst());
//        Ritualism.LOGGER.debug("Emptied fluid: " + emptyResult.getSecond().getAmount() + " " + ForgeRegistries.FLUIDS.getKey(emptyResult.getSecond().getFluid()));







        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        level.registryAccess().registry(MultiBlock.REGISTRY).ifPresent(registry -> {
            registry.entrySet().stream().forEach(multiBlock -> {
                Ritualism.LOGGER.debug(multiBlock.getKey().location() + " is present? " + multiBlock.getValue().test(level, pos) + ", client: " + level.isClientSide);
            });
        });
        return super.useOn(pContext);
    }
}
