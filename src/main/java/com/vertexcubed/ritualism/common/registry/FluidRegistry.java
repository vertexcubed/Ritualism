package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.fluid.FluidRenderProperties;
import com.vertexcubed.ritualism.common.fluid.RitualismFluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidRegistry {
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Ritualism.MOD_ID);

    public static final FluidRegistryObject<RitualismFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, LiquidBlock, BucketItem>
            SECOND_TEST = registerWaterLike(new ResourceLocation(Ritualism.MOD_ID, "second_test"), 0x7f0bfc03,true, true);

    private static FluidRegistryObject<RitualismFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, LiquidBlock, BucketItem>
    registerWaterLike(ResourceLocation name, int tintColor, boolean canConvertToSource, boolean canHydrate) {
        FluidType.Properties prop = FluidType.Properties.create()
                .descriptionId("block." + name.getNamespace() + "." + name.getPath())
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .canConvertToSource(canConvertToSource)
                .supportsBoating(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canHydrate(canHydrate);
        FluidRenderProperties renderProp = new FluidRenderProperties.Builder().tintColor(tintColor).build();
        return FLUIDS.register(name.getPath(), prop, renderProp);
    }


    private static FluidRegistryObject<RitualismFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, LiquidBlock, BucketItem> registerLavaLike(ResourceLocation name, int tintColor, boolean canConvertToSource, boolean canHydrate) {
        FluidType.Properties prop = FluidType.Properties.create()
                .descriptionId("block." + name.getNamespace() + "." + name.getPath())
                .canSwim(false)
                .canDrown(false)
                .pathType(BlockPathTypes.LAVA)
                .adjacentPathType(null)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .lightLevel(15)
                .density(3000)
                .viscosity(6000)
                .temperature(1300);
        FluidRenderProperties renderProp = new FluidRenderProperties.Builder()
                .stillTexture(new ResourceLocation("block/lava_still"))
                .flowingTexture(new ResourceLocation("block/lava_flow"))
                .overlayTexture(null)
                .renderOverlayTexture(null)
                .tintColor(tintColor).build();
        return FLUIDS.register(name.getPath(), prop, renderProp);
    }


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }

    private static final DispenseItemBehavior dispenseFluid = new DefaultDispenseItemBehavior() {
        @Override
        public ItemStack execute(BlockSource blockSource, ItemStack stack) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
            BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
            Level level = blockSource.getLevel();
            if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, stack)) {
                dispensiblecontaineritem.checkExtraContent(null, level, stack, blockpos);
                return new ItemStack(Items.BUCKET);
            } else {
                return super.execute(blockSource, stack);
            }
        }

    };

    public static void registerDispenserBehavior() {
        FLUIDS.getEntries().forEach(fluid -> {
            DispenserBlock.registerBehavior(fluid.bucket(), dispenseFluid);
        });
    }
}
