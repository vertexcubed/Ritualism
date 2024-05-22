package com.vertexcubed.ritualism.common.multiblock.matcher;

import com.mojang.serialization.Codec;
import com.vertexcubed.ritualism.api.multiblock.StateMatcher;
import com.vertexcubed.ritualism.common.registry.StateMatcherRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class AnyMatcher implements StateMatcher {
    public static final Codec<AnyMatcher> CODEC = Codec.unit(AnyMatcher::new);

    @Override
    public Type<?> getType() {
        return StateMatcherRegistry.ANY.get();
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return (blockGetter, blockPos, blockState) -> true;
    }
}
