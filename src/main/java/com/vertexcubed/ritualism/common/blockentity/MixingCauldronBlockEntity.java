package com.vertexcubed.ritualism.common.blockentity;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.block.MixingCauldronBlock;
import com.vertexcubed.ritualism.common.recipe.MixingRecipe;
import com.vertexcubed.ritualism.common.recipe.MixingRecipeWrapper;
import com.vertexcubed.ritualism.common.registry.BlockRegistry;
import com.vertexcubed.ritualism.common.registry.RecipeRegistry;
import com.vertexcubed.ritualism.common.registry.TagRegistry;
import com.vertexcubed.ritualism.common.util.ItemHelper;
import com.vertexcubed.ritualism.server.network.PacketRegistry;
import com.vertexcubed.ritualism.server.network.s2c.S2CMixingCauldronParticlesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import java.util.Optional;
import java.util.stream.Collectors;


public class MixingCauldronBlockEntity extends BlockEntity implements ArcaneCrafter {


    public static final int MAX_CRAFT_TIME = 100;
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
            if(craftingTime > 0) return false;
            return getStackInSlot(slot).isEmpty() || super.isItemValid(slot, stack);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(craftingTime > 0) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
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

    private boolean isHeated;
    private int craftingTime = -1;
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
                if(level instanceof ServerLevel serverLevel) {
                    Vec3 pos = worldPosition.getCenter();
                    serverLevel.sendParticles(ParticleTypes.SPLASH, pos.x, pos.y + 0.5, pos.z, serverLevel.random.nextInt(4) + 4, 0.2, 0, 0.2, 1.0);
                }
                return true;
            }
        }
        return false;
    }

    private boolean addItemsFromWorld(Level level, ItemEntity item) {
        ItemStack original = item.getItem().copy();
        int slot = ItemHelper.getFirstValidSlot(itemHandler, original);
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
        if(!level.isClientSide) {
            level.playSound(null, this.worldPosition, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.2f, level.random.nextFloat() * 1.5f + 0.5f);
        }
        return true;
    }

    @Override
    public void activate(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide) {
            craftingTime = 100;
            setChanged();
        }
    }

    @Override
    public boolean canActivate(Level level, Player player, InteractionHand hand) {
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            if(!itemHandler.getStackInSlot(i).isEmpty()) {
                return isHeated && craftingTime == -1;
            }
        }
        return false;
    }

    public boolean isCrafting() {
        return craftingTime > -1;
    }
    private void craft(Level level, MixingRecipe recipe) {
        Ritualism.LOGGER.debug("Crafted! Client: " + level.isClientSide);
        FluidStack result = recipe.getResultingFluid();
        FluidStack old = fluidHandler.getFluidInTank(0);
        fluidHandler.setFluid(new FluidStack(result.getFluid(), old.getAmount() - recipe.getFluidConsumed()));
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }}
    private void failCraft(Level level) {
        Ritualism.LOGGER.debug("Failed! Client: " + level.isClientSide);
        fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            Vec3 center = worldPosition.getCenter();
            flingItem(level, center.x, center.y + 1.0, center.z, stack);
        }
    }

    private void flingItem(Level level, double x, double y, double z, ItemStack stack) {
        double d0 = EntityType.ITEM.getWidth();
        double d1 = 1.0D - d0;
        double d2 = d0 / 2.0D;
        double d3 = Math.floor(x) + level.random.nextDouble() * d1 + d2;
        double d4 = Math.floor(y) + level.random.nextDouble() * d1;
        double d5 = Math.floor(z) + level.random.nextDouble() * d1 + d2;

        while(!stack.isEmpty()) {
            ItemEntity itementity = new ItemEntity(level, d3, d4, d5, stack.split(level.random.nextInt(21) + 10));
            float f = 0.05F;
            itementity.setDeltaMovement(level.random.triangle(0.0D, 0.11485000171139836D), 0.5, level.random.triangle(0.0D, 0.11485000171139836D));
            level.addFreshEntity(itementity);
        }
    }

    private static List<ItemEntity> getItemsAt(Level pLevel, MixingCauldronBlockEntity be) {
        return be.getSuckShape()
                .toAabbs()
                .stream()
                .flatMap((aabb) -> pLevel.getEntitiesOfClass(ItemEntity.class, aabb.move(be.worldPosition.getX(), be.worldPosition.getY(), be.worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream())
                .collect(Collectors.toList());
    }

    private void spawnBoilingParticles(Level level) {
        if(isHeated && !fluidHandler.isEmpty() && level.getGameTime() % 50 == 0) {
//            double d0 = (level.random.nextDouble() * 2.0D - 1.0D);
//            double d1 = (level.random.nextDouble() * 2.0D - 1.0D);
            Vec3 pos = worldPosition.getCenter();
            if(level instanceof ServerLevel serverLevel) {

//                Ritualism.LOGGER.debug("Spawning particles..");

            }
        }
    }

    public void dropContents() {
        Containers.dropContents(this.level, this.worldPosition, ItemHelper.createContainerFromHandler(itemHandler));
    }

    private void updateIsHeated() {

        BlockState below = level.getBlockState(worldPosition.below());
        boolean heatedOld = isHeated;
        isHeated = below.is(TagRegistry.HEAT_SOURCES);
        //only set changed if heat status changes, to prevent unnecessary syncing
        if(heatedOld != isHeated) {
            setChanged();
        }
    }

    public boolean isHeated() {
        return isHeated;
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
        tag.putBoolean("isHeated", isHeated);
        tag.putInt("crafting_time", craftingTime);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        fluidHandler.readFromNBT(tag.getCompound("fluid"));
        isHeated = tag.getBoolean("isHeated");
        craftingTime = tag.getInt("crafting_time");
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

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, MixingCauldronBlockEntity be) {
        if(!be.isHeated) {
            be.craftingTime = -1;
        }

        if(be.craftingTime > -1) {
            be.craftingTime--;
        }


        if(be.craftingTime == 0) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(6, ItemStack.EMPTY);
            for(int i = 0; i < be.itemHandler.getSlots(); i++) {
                stacks.set(i, be.itemHandler.getStackInSlot(i));
            }
            MixingRecipeWrapper wrapper = new MixingRecipeWrapper(stacks, be.fluidHandler.getFluidInTank(0));
            Optional<MixingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeRegistry.MIXING_TYPE.get(), wrapper, level);
            if(optional.isPresent()) {
                be.craft(level, optional.get());

                level.playSound(null, be.worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.25f);
                PacketRegistry.sendToNearbyClients(level, be.worldPosition, new S2CMixingCauldronParticlesPacket(S2CMixingCauldronParticlesPacket.OpCode.SUCCESS, be.worldPosition));
            }
            else {
                be.failCraft(level);

                level.playSound(null, be.worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.8f, 1.25f);
                PacketRegistry.sendToNearbyClients(level, be.worldPosition, new S2CMixingCauldronParticlesPacket(S2CMixingCauldronParticlesPacket.OpCode.FAIL, be.worldPosition));
            }
        }

        if(!Block.isShapeFullBlock(level.getBlockState(blockPos.above()).getShape(level, blockPos.above())) && !be.fluidHandler.isEmpty()) {
            be.suckInItems(level);
        }


        be.updateIsHeated();

        be.spawnBoilingParticles(level);

    }
    public static void tickClient(Level level, BlockPos blockPos, BlockState blockState, MixingCauldronBlockEntity be) {
        if(!be.isHeated) {
            be.craftingTime = -1;
        }
        if(be.craftingTime > -1) {
            be.craftingTime--;
        }
    }
}
