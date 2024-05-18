package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.common.fluid.RitualismFluidType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class FluidRegistryObject<TYPE extends RitualismFluidType, STILL extends ForgeFlowingFluid.Source, FLOWING extends ForgeFlowingFluid.Flowing, BLOCK extends LiquidBlock, BUCKET extends BucketItem> {
    private RegistryObject<TYPE> type;
    private RegistryObject<STILL> still;
    private RegistryObject<FLOWING> flowing;
    private RegistryObject<BLOCK> block;
    private RegistryObject<BUCKET> bucket;

    //package private to prevent others from breaking things
    FluidRegistryObject() {
        this.type = null;
        this.still = null;
        this.flowing = null;
        this.block = null;
        this.bucket = null;
    }

    FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> updateType(RegistryObject<TYPE> type) {
        this.type = Objects.requireNonNull(type);
        return this;
    }
    FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> updateStill(RegistryObject<STILL> still) {
        this.still = Objects.requireNonNull(still);
        return this;
    }
    FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> updateFlowing(RegistryObject<FLOWING> flowing) {
        this.flowing = Objects.requireNonNull(flowing);
        return this;
    }
    FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> updateBlock(RegistryObject<BLOCK> block) {
        this.block = Objects.requireNonNull(block);
        return this;
    }
    FluidRegistryObject<TYPE, STILL, FLOWING, BLOCK, BUCKET> updateBucket(RegistryObject<BUCKET> bucket) {
        this.bucket = Objects.requireNonNull(bucket);
        return this;
    }

    public STILL getFluid() {
        return still.get();
    }

    public TYPE type() {
        return type.get();
    }

    public STILL still() {
        return still.get();
    }

    public FLOWING flowing() {
        return flowing.get();
    }

    public BLOCK block() {
        return block.get();
    }

    public BUCKET bucket() {
        return bucket.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FluidRegistryObject) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.still, that.still) &&
                Objects.equals(this.flowing, that.flowing) &&
                Objects.equals(this.block, that.block) &&
                Objects.equals(this.bucket, that.bucket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, still, flowing, block, bucket);
    }

    @Override
    public String toString() {
        return "FluidRegistryObject[" +
                "type=" + type + ", " +
                "still=" + still + ", " +
                "flowing=" + flowing + ", " +
                "block=" + block + ", " +
                "bucket=" + bucket + ']';
    }

}
