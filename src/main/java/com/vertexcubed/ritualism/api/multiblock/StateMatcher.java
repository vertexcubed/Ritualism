package com.vertexcubed.ritualism.api.multiblock;

import com.mojang.serialization.Codec;
import com.vertexcubed.ritualism.common.registry.StateMatcherRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public interface StateMatcher {


    Codec<StateMatcher> CODEC = ExtraCodecs.lazyInitializedCodec(() -> StateMatcherRegistry.STATE_MATCHER_REGISTRY.get().getCodec()).dispatch(StateMatcher::getType, Type::codec);

    Type<?> getType();

    TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate();




    record Type<S extends StateMatcher>(Codec<S> codec) {}
}
