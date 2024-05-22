package com.vertexcubed.ritualism.common.multiblock.matcher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vertexcubed.ritualism.api.multiblock.StateMatcher;
import com.vertexcubed.ritualism.common.registry.StateMatcherRegistry;
import com.vertexcubed.ritualism.common.util.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.registries.ForgeRegistries;

public class TagMatcher implements StateMatcher {

    public static final Codec<TagMatcher> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(ForgeRegistries.BLOCKS.getRegistryKey()).fieldOf("tag").forGetter(TagMatcher::getTag)
    ).apply(instance, TagMatcher::new));

    private final TagKey<Block> tag;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    public TagMatcher(TagKey<Block> tag) {
        this.tag = tag;
        this.predicate = ((blockGetter, blockPos, blockState) -> blockState.is(tag));

    }

    public TagKey<Block> getTag() {
        return tag;
    }

    @Override
    public Type<?> getType() {
        return StateMatcherRegistry.TAG.get();
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return predicate;
    }
}
