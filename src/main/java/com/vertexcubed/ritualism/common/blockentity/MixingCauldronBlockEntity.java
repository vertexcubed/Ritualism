package com.vertexcubed.ritualism.common.blockentity;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.block.MixingCauldronBlock;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import com.vertexcubed.ritualism.common.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;


public class MixingCauldronBlockEntity extends BlockEntity {

    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return getStackInSlot(slot).isEmpty() || super.isItemValid(slot, stack);
        }
    };

    private FluidTank fluidHandler = new FluidTank(2000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHanlder = LazyOptional.empty();

    public MixingCauldronBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockRegistry.MIXING_CAULDRON_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    /**
     * Gets a copy of the fluid in the tank. To modify, use capabilities.
     */
    public FluidStack getFluid() {
        return fluidHandler.getFluidInTank(0).copy();
    }

    public int getCapacity() {
        return fluidHandler.getCapacity();
    }

    protected VoxelShape getSuckShape() {
        return MixingCauldronBlock.SUCK;
    }

    private boolean suckInItems(Level level) {
        if(ItemHelper.isFull(itemHandler)) return false;
        List<ItemEntity> items = getItemsAt(level, this);
        for(ItemEntity item : items) {
            if(addItemsFromWorld(level, item)) {
                return true;
            }
        }
        return false;
    }

    private boolean addItemsFromWorld(Level level, ItemEntity item) {
        ItemStack original = item.getItem().copy();
        int slot = ItemHelper.getFirstValidSlot(itemHandler, original);
        Ritualism.LOGGER.debug("Valid slot: " + slot);
        if(slot == -1) {
            return false;
        }
        ItemStack leftover = itemHandler.insertItem(slot, original, false);
        if(leftover.isEmpty()) {
            item.discard();
        }
        else {
            item.setItem(leftover);
        }
        return true;
    }

    private static List<ItemEntity> getItemsAt(Level pLevel, MixingCauldronBlockEntity be) {
        return be.getSuckShape()
                .toAabbs()
                .stream()
                .flatMap((aabb) -> pLevel.getEntitiesOfClass(ItemEntity.class, aabb.move(be.worldPosition.getX(), be.worldPosition.getY(), be.worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream())
                .collect(Collectors.toList());
    }

    public void dropContents() {
        Containers.dropContents(this.level, this.worldPosition, ItemHelper.createContainerFromHandler(itemHandler));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHanlder.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHanlder = LazyOptional.of(() -> fluidHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHanlder.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("fluid", fluidHandler.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        fluidHandler.readFromNBT(tag.getCompound("fluid"));
    }

    @Override
    public void setChanged() {
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        super.setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, MixingCauldronBlockEntity blockEntity) {
        if(!Block.isShapeFullBlock(level.getBlockState(blockPos.above()).getShape(level, blockPos.above()))) {
            blockEntity.suckInItems(level);
        }
    }
}
