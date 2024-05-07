package com.vertexcubed.ritualism.common.block;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.blockentity.MixingCauldronBlockEntity;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class MixingCauldronBlock extends BaseEntityBlock {

    public static final VoxelShape INSIDE = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape OUTSIDE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final VoxelShape ABOVE = Block.box(0.0, 16.0, 0.0, 16.0, 20.0, 16.0);
    public static final VoxelShape SHAPE = Shapes.join(INSIDE, OUTSIDE, BooleanOp.ONLY_SECOND);
    public static final VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);
    public MixingCauldronBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MixingCauldronBlockEntity(pPos, pState);
    }


    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pPos, Player player, InteractionHand hand, BlockHitResult pHit) {
        BlockEntity be = level.getBlockEntity(pPos);
        if (!(be instanceof MixingCauldronBlockEntity)) {
            return InteractionResult.PASS;
        }
        if(player.isShiftKeyDown()) return InteractionResult.PASS;
        if(level.isClientSide) return InteractionResult.CONSUME;

        Ritualism.LOGGER.debug("Use clicked...");

        ItemStack stack = player.getItemInHand(hand);
        IFluidHandlerItem itemFluid = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(itemFluid == null) {
            Ritualism.LOGGER.debug("item fluid is null.");
            return InteractionResult.CONSUME;
        }
        IFluidHandler tank = be.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if(tank == null) {
            Ritualism.LOGGER.debug("tank fluid is null.");
            return InteractionResult.CONSUME;
        }
        if(tank.getFluidInTank(0).isEmpty()) {
            int slot = -1;
            for(int i = 0; i < itemFluid.getTanks(); i++) {
                if(!itemFluid.getFluidInTank(i).isEmpty()) {
                    slot = i;
                    break;
                }
            }
            if(slot == -1) {
                Ritualism.LOGGER.debug("Cannot find filled slot in item.");
                return InteractionResult.CONSUME;
            }

            Ritualism.LOGGER.debug("Tank is empty, draining item...");
            return drainItem(itemFluid, tank, player, hand, stack);

        }
        else if(tank.getFluidInTank(0).getAmount() >= tank.getTankCapacity(0)) {
            Ritualism.LOGGER.debug("Tank is full, draining tank...");
            return drainTank(itemFluid, tank, player, hand, stack);
        }
        else {
            int slot = -1;
            for(int i = 0; i < itemFluid.getTanks(); i++) {
                if(!itemFluid.getFluidInTank(i).isEmpty()) {
                    slot = i;
                    break;
                }
            }
            if(slot == -1) {
                Ritualism.LOGGER.debug("Tank is partially full and item has no matching, draining tank...");
                return drainTank(itemFluid, tank, player, hand, stack);
            }
            Ritualism.LOGGER.debug("Tank is partially full and item has matching, draining item...");
            return drainItem(itemFluid, tank, player, hand, stack);
        }
    }

    private InteractionResult drainItem(IFluidHandlerItem itemFluid, IFluidHandler tank, Player player, InteractionHand hand, ItemStack original) {
        FluidStack simDrained = itemFluid.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        int filled = tank.fill(simDrained, IFluidHandler.FluidAction.EXECUTE);
        FluidStack drained = itemFluid.drain(new FluidStack(simDrained, filled), player.isCreative() ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        if(!drained.isEmpty()) {
            ItemStack containerStack = itemFluid.getContainer();
            if(!containerStack.isEmpty()) {
                if(original.getCount() == 1) {
                    player.setItemInHand(hand, containerStack);
                }
                else if(original.getCount() > 1 && player.getInventory().add(containerStack)) {
                    original.shrink(1);
                }
                else {
                    player.drop(containerStack, false, true);
                    original.shrink(1);
                }
            }
        }
        Ritualism.LOGGER.debug("Successfully drained item and filled tank. Amount drained from item: " + filled);
        return InteractionResult.SUCCESS;
    }
    private InteractionResult drainTank(IFluidHandlerItem itemFluid, IFluidHandler tank, Player player, InteractionHand hand, ItemStack original) {
        int slot = -1;
        for(int i = 0; i < itemFluid.getTanks(); i++) {
            if(itemFluid.getFluidInTank(0).isEmpty()) {
                slot = i;
                break;
            }
        }
        if(slot == -1) {
            Ritualism.LOGGER.debug("Item has no empty tanks.");
            return InteractionResult.CONSUME;
        }

        FluidStack drained = tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        int filled = itemFluid.fill(drained, player.isCreative() ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

        if(filled > 0) {
            ItemStack containerStack = itemFluid.getContainer();
            if(!containerStack.isEmpty()) {
                if(original.getCount() == 1) {
                    player.setItemInHand(hand, containerStack);
                }
                else if(original.getCount() > 1 && player.getInventory().add(containerStack)) {
                    original.shrink(1);
                }
                else {
                    player.drop(containerStack, false, true);
                    original.shrink(1);
                }
            }
        }
        else {
            Ritualism.LOGGER.debug("Cannot fill item.");
            return InteractionResult.CONSUME;
        }

        tank.drain(new FluidStack(drained, filled), IFluidHandler.FluidAction.EXECUTE);
        Ritualism.LOGGER.debug("Successfully drained tank and filled item.");
        return InteractionResult.SUCCESS;

    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }


    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(pState.getBlock() != pNewState.getBlock()) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if(be instanceof MixingCauldronBlockEntity) {
                ((MixingCauldronBlockEntity) be).dropContents();
            } else {
                throw new IllegalStateException("Missing Block Entity!");
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, BlockRegistry.MIXING_CAULDRON_BLOCK_ENTITY.get(), MixingCauldronBlockEntity::tick);
    }
}
