package com.vertexcubed.ritualism.common.block;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.blockentity.MixingCauldronBlockEntity;
import com.vertexcubed.ritualism.common.fluid.ItemEmptying;
import com.vertexcubed.ritualism.common.fluid.ItemFilling;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import com.vertexcubed.ritualism.common.util.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
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
        if(player.isShiftKeyDown()) {
            if(player.getItemInHand(hand).isEmpty()) {
                IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
                if(handler == null) {
                    return InteractionResult.PASS;
                }
                for(int i = handler.getSlots() - 1; i >= 0; i--) {
                    if(!handler.getStackInSlot(i).isEmpty()) {
                        player.getInventory().placeItemBackInInventory(handler.extractItem(i, 64, false));
                        if(!level.isClientSide) {
                            level.playSound(null, pPos, SoundEvents.ITEM_PICKUP,  SoundSource.BLOCKS, 0.2f, (level.random.nextFloat() / 2.0f) + 1.5f);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        }

        Ritualism.LOGGER.debug("Use clicked... client: " + level.isClientSide);

        ItemStack heldItem = player.getItemInHand(hand);
        IFluidHandler tank = be.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if(tank == null) {
            Ritualism.LOGGER.debug("tank is null, side: " + level.isClientSide);
            return InteractionResult.PASS;
        }
        //If item can't be filled or emptied, pass.

        InteractionResult result = attemptFluidInteraction(level, pPos, tank, player, heldItem, hand);
        return result;



    }

    private InteractionResult attemptFluidInteraction(Level level, BlockPos pos, IFluidHandler tank, Player player, ItemStack heldItem, InteractionHand hand) {
        FluidStack tankFluid = tank.getFluidInTank(0);
        if(!ItemFilling.canItemBeFilled(level, heldItem, tankFluid) && !ItemEmptying.canItemBeEmptied(level, heldItem)) {
            return InteractionResult.PASS;
        }

        //tank is not full. Attempt to fill tank.
        if(FluidHelper.canFill(tank)) {
            if((tankFluid.isEmpty() && ItemEmptying.canItemBeEmptied(level, heldItem)) || ItemEmptying.canItemBeEmptied(level, heldItem, tankFluid.getFluid())) {
                //we know we can fill the tank with the specified fluid now.
                Pair<ItemStack, FluidStack> emptySim = ItemEmptying.emptyItem(level, heldItem, tankFluid.getFluid(), true);
                int fillAmount = tank.fill(emptySim.getSecond(), IFluidHandler.FluidAction.SIMULATE);
                if(fillAmount < emptySim.getSecond().getAmount()) {
                    //could not fill all the fluid. Attempting to drain a smaller amount.
                    Pair<ItemStack, FluidStack> emptyLess = ItemEmptying.emptyItem(level, heldItem, emptySim.getSecond().getFluid(), fillAmount, true);
                    if(emptyLess.getSecond().isEmpty()) {
                        //Could not fill smaller amount. Consume.
                        return InteractionResult.CONSUME;
                    }
                }
                //empty the item and fill the tank, for real this time.
                tank.fill(emptySim.getSecond(), IFluidHandler.FluidAction.EXECUTE);
                ItemStack newStack = ItemEmptying.emptyItem(level, heldItem, emptySim.getSecond().getFluid(), fillAmount, player.isCreative()).getFirst();

                if(!player.isCreative()) {
                    if(heldItem.isEmpty()) {
                        player.setItemInHand(hand, newStack);
                    }
                    else {
                        player.getInventory().placeItemBackInInventory(newStack);
                    }
                }
                if(!level.isClientSide) {
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                }
                return InteractionResult.SUCCESS;
            }
            //if cannot empty of specified fluid, attempt to drain as below.
        }
        //tank is full, or cannot empty specified fluid. Attempt to drain.
        if(ItemFilling.canItemBeFilled(level, heldItem, tankFluid) && !tankFluid.isEmpty()) {
            FluidStack drainedAll = tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
            Pair<ItemStack, FluidStack> fill = ItemFilling.fillItem(level, heldItem, drainedAll, player.isCreative());
            tank.drain(fill.getSecond(), IFluidHandler.FluidAction.EXECUTE);
            if(!player.isCreative()) {
                if(heldItem.isEmpty()) {
                    player.setItemInHand(hand, fill.getFirst());
                }
                else {
                    player.getInventory().placeItemBackInInventory(fill.getFirst());
                }
            }
            if(!level.isClientSide) {
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
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
