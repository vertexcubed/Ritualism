package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.common.fluid.FluidRenderProperties;
import com.vertexcubed.ritualism.common.fluid.RitualismFluidType;
import com.vertexcubed.ritualism.mixin.MapColorAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;
import net.minecraftforge.fluids.ForgeFlowingFluid.Source;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FluidDeferredRegister {
    private final DeferredRegister<Fluid> fluidDR;
    private final DeferredRegister<FluidType> typeDR;
    private final DeferredRegister<Block> blockDR;
    private final DeferredRegister<Item> itemDR;

    private final List<FluidRegistryObject<? extends RitualismFluidType, ?, ?, ?, ?>> entries = new ArrayList<>();

    public FluidDeferredRegister(String modid) {
        this.fluidDR = DeferredRegister.create(ForgeRegistries.FLUIDS, modid);
        this.typeDR = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, modid);
        this.blockDR = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
        this.itemDR = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
    }

    public void register(IEventBus eventBus) {
        fluidDR.register(eventBus);
        typeDR.register(eventBus);
        blockDR.register(eventBus);
        itemDR.register(eventBus);
    }

    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @return A new fluid registry object.
     */
    public FluidRegistryObject<RitualismFluidType, Source, Flowing, LiquidBlock, BucketItem> register(String name, FluidType.Properties typeProperties, FluidRenderProperties renderProperties) {
        return register(name, typeProperties, renderProperties, RitualismFluidType::new);
    }

    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @param typeFactory A factory representing the FluidType, for FluidTypes with a unique class.
     * @return A new fluid registry object.
     */
    public <TYPE extends RitualismFluidType> FluidRegistryObject<TYPE, Source, Flowing, LiquidBlock, BucketItem> register(
            String name, FluidType.Properties typeProperties, FluidRenderProperties renderProperties, FluidTypeFactory<TYPE> typeFactory) {
        return register(name, typeProperties, renderProperties, typeFactory, (fluid, prop) -> new BucketItem(fluid, prop.craftRemainder(Items.BUCKET).stacksTo(1)));
    }

    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @param typeFactory A factory representing the FluidType, for FluidTypes with a unique class.
     * @param bucketFactory A factory representing the BucketItem, for custom buckets not using vanilla BucketItem
     * @return A new fluid registry object.
     */
    public <TYPE extends RitualismFluidType, BUCKET extends BucketItem> FluidRegistryObject<TYPE, Source, Flowing, LiquidBlock, BUCKET> register(
            String name, FluidType.Properties typeProperties, FluidRenderProperties renderProperties, FluidTypeFactory<TYPE> typeFactory, BucketFactory<BUCKET> bucketFactory) {
        return register(name, PropertiesFactory.empty(), typeProperties, renderProperties, typeFactory, Source::new, Flowing::new, (fluid, prop) -> new LiquidBlock(fluid, prop.noCollission().explosionResistance(100f).replaceable().liquid().pushReaction(PushReaction.DESTROY).noLootTable()), bucketFactory);
    }


    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param propertiesFactory A factory representing the properties of the FlowingFluid. Use this to define slope finding distance and level change per block.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @return A new fluid registry object.
     */
    public FluidRegistryObject<RitualismFluidType, Source, Flowing, LiquidBlock, BucketItem> register(String name, PropertiesFactory propertiesFactory, FluidType.Properties typeProperties, FluidRenderProperties renderProperties) {
        return register(name, propertiesFactory, typeProperties, renderProperties, RitualismFluidType::new);
    }

    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param propertiesFactory A factory representing the properties of the FlowingFluid. Use this to define slope finding distance and level change per block.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @param typeFactory A factory representing the FluidType, for FluidTypes with a unique class.
     * @return A new fluid registry object.
     */
    public <TYPE extends RitualismFluidType> FluidRegistryObject<TYPE, Source, Flowing, LiquidBlock, BucketItem> register(
            String name, PropertiesFactory propertiesFactory, FluidType.Properties typeProperties, FluidRenderProperties renderProperties, FluidTypeFactory<TYPE> typeFactory) {
        return register(name, propertiesFactory, typeProperties, renderProperties, typeFactory, (fluid, prop) -> new BucketItem(fluid, prop.craftRemainder(Items.BUCKET).stacksTo(1)));
    }

    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param propertiesFactory A factory representing the properties of the FlowingFluid. Use this to define slope finding distance and level change per block.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @param typeFactory A factory representing the FluidType, for FluidTypes with a unique class.
     * @param bucketFactory A factory representing the BucketItem, for custom buckets not using vanilla BucketItem
     * @return A new fluid registry object.
     */
    public <TYPE extends RitualismFluidType, BUCKET extends BucketItem> FluidRegistryObject<TYPE, Source, Flowing, LiquidBlock, BUCKET> register(
            String name, PropertiesFactory propertiesFactory, FluidType.Properties typeProperties, FluidRenderProperties renderProperties, FluidTypeFactory<TYPE> typeFactory, BucketFactory<BUCKET> bucketFactory) {
        return register(name, propertiesFactory, typeProperties, renderProperties, typeFactory, Source::new, Flowing::new, (fluid, prop) -> new LiquidBlock(fluid, prop.noCollission().explosionResistance(100f).replaceable().liquid().pushReaction(PushReaction.DESTROY).noLootTable()), bucketFactory);
    }

    /**
     * Creates a new Fluid registry object. This serves to simplify fluid registration so you only need to register one thing instead of a million.
     * @param name The name of the fluid.
     * @param propertiesFactory A factory representing the properties of the FlowingFluid. Use this to define slope finding distance and level change per block.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @param typeFactory A factory representing the FluidType, for FluidTypes with a unique class.
     * @param blockFactory A factory representing the LiquidBlock, for custom liquid blocks.
     * @param bucketFactory A factory representing the BucketItem, for custom buckets not using vanilla BucketItem
     * @return A new fluid registry object.
     */
    public <TYPE extends RitualismFluidType, BLOCK extends LiquidBlock, BUCKET extends BucketItem> FluidRegistryObject<TYPE, Source, Flowing, BLOCK, BUCKET> register(
            String name, PropertiesFactory propertiesFactory, FluidType.Properties typeProperties, FluidRenderProperties renderProperties, FluidTypeFactory<TYPE> typeFactory, FluidBlockFactory<BLOCK> blockFactory, BucketFactory<BUCKET> bucketFactory) {
        return register(name, propertiesFactory, typeProperties, renderProperties, typeFactory, Source::new, Flowing::new, blockFactory, bucketFactory);
    }

    /**
     * Creates a new Fluid registry object. It is highly recommended to use one of the overloaded versions of this method instead, unless you absolutely need to use this one.
     * @param name The name of the fluid.
     * @param propertiesFactory A factory representing the properties of the FlowingFluid. Use this to define slope finding distance and level change per block.
     * @param typeProperties The properties of the FluidType. Use this to define physics-related fluid settings.
     * @param renderProperties The render properties of the FluidType. Use this to define color and textures.
     * @param typeFactory A factory representing the FluidType, for FluidTypes with a unique class.
     * @param stillFactory A factory representing the still/source fluid, for fluids not using ForgeFlowingFluid.Source
     * @param flowingFactory A factory representing the flowing fluid, for fluids not using ForgeFlowingFluid.Flowing
     * @param blockFactory A factory representing the LiquidBlock, for custom liquid blocks.
     * @param bucketFactory A factory representing the BucketItem, for custom buckets not using vanilla BucketItem
     * @return A new fluid registry object.
     */
    public <TYPE extends RitualismFluidType, STILL extends Source, FLOWING extends Flowing, BLOCK extends LiquidBlock, BUCKET extends BucketItem>
    FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> register(
            String name, PropertiesFactory propertiesFactory, FluidType.Properties typeProperties, FluidRenderProperties renderProperties, FluidTypeFactory<TYPE> typeFactory,
            FluidFactory<STILL> stillFactory, FluidFactory<FLOWING> flowingFactory, FluidBlockFactory<BLOCK> blockFactory, BucketFactory<BUCKET> bucketFactory) {

        FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> output = new FluidRegistryObject<>();

        ForgeFlowingFluid.Properties properties = propertiesFactory.create(new ForgeFlowingFluid.Properties(output::type, output::still, output::flowing).block(output::block).bucket(output::bucket));

        RegistryObject<TYPE> typeRO = typeDR.register(name, () -> typeFactory.create(typeProperties, renderProperties));
        RegistryObject<STILL> stillRO = fluidDR.register(name, () -> stillFactory.create(properties));
        RegistryObject<FLOWING> flowingRO = fluidDR.register(name + "_flowing", () -> flowingFactory.create(properties));
        RegistryObject<BLOCK> blockRO = blockDR.register(name, () -> blockFactory.create(output::getFluid,
        BlockBehaviour.Properties.of().mapColor(getClosestColor(renderProperties.getTintColor()))));
        RegistryObject<BUCKET> bucketRO = itemDR.register(name + "_bucket", () -> bucketFactory.create(output::getFluid, new Item.Properties()));

        entries.add(output);
        //types and such updated after because silly properties needs a reference to the output early. Yay!
        return output.updateType(typeRO).updateStill(stillRO).updateFlowing(flowingRO).updateBlock(blockRO).updateBucket(bucketRO);
    }

    public List<FluidRegistryObject<? extends RitualismFluidType, ?, ?, ?, ?>> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Copied from <a href=https://github.com/mekanism/Mekanism/blob/1.20.6/src/main/java/mekanism/common/registration/impl/FluidDeferredRegister.java> Mekanism</a>, which is under MIT license.
     */
    private static MapColor getClosestColor(int tint) {
        if (tint == 0xFFFFFFFF) {
            return MapColor.NONE;
        }
        int red = FastColor.ARGB32.red(tint);
        int green = FastColor.ARGB32.green(tint);
        int blue = FastColor.ARGB32.blue(tint);
        MapColor color = MapColor.NONE;
        double minDistance = Double.MAX_VALUE;
        for (MapColor toTest : MapColorAccessor.getMapColors()) {
            if (toTest != null && toTest != MapColor.NONE) {
                int testRed = FastColor.ARGB32.red(toTest.col);
                int testGreen = FastColor.ARGB32.green(toTest.col);
                int testBlue = FastColor.ARGB32.blue(toTest.col);
                double distanceSquare = perceptualColorDistanceSquared(red, green, blue, testRed, testGreen, testBlue);
                if (distanceSquare < minDistance) {
                    minDistance = distanceSquare;
                    color = toTest;
                }
            }
        }
        return color;
    }

    /**
     * Copied from <a href=https://github.com/mekanism/Mekanism/blob/1.20.6/src/main/java/mekanism/common/registration/impl/FluidDeferredRegister.java> Mekanism</a>, which is under MIT license.
     */
    private static double perceptualColorDistanceSquared(int red1, int green1, int blue1, int red2, int green2, int blue2) {
        int redMean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = green1 - green2;
        int b = blue1 - blue2;
        return (((512 + redMean) * r * r) >> 8) + 4 * g * g + (((767 - redMean) * b * b) >> 8);
    }



    //FACTORIES

    @FunctionalInterface
    public interface PropertiesFactory {
        static PropertiesFactory empty() {
            return properties -> properties;
        }

        ForgeFlowingFluid.Properties create(ForgeFlowingFluid.Properties properties);
    }

    @FunctionalInterface
    public interface FluidTypeFactory<TYPE extends RitualismFluidType> {

        TYPE create(FluidType.Properties typeProperties, FluidRenderProperties renderProperties);
    }

    //Can be used for both sources and fluids
    @FunctionalInterface
    public interface FluidFactory<FLUID extends ForgeFlowingFluid> {

        FLUID create(ForgeFlowingFluid.Properties properties);
    }

    @FunctionalInterface
    public interface FluidBlockFactory<BLOCK extends LiquidBlock> {

        BLOCK create(Supplier<? extends FlowingFluid> fluid, BlockBehaviour.Properties properties);
    }

    @FunctionalInterface
    public interface BucketFactory<BUCKET extends BucketItem> {

        BUCKET create(Supplier<? extends FlowingFluid> fluid, Item.Properties properties);
    }
}
