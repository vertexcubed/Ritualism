package com.vertexcubed.ritualism.common.multiblock.matcher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vertexcubed.ritualism.api.multiblock.StateMatcher;
import com.vertexcubed.ritualism.common.registry.StateMatcherRegistry;
import com.vertexcubed.ritualism.common.util.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class BlockMatcher implements StateMatcher {

    public static final Codec<BlockMatcher> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        CodecUtils.listOrElementCodec(ForgeRegistries.BLOCKS.getCodec()).fieldOf("block").forGetter(BlockMatcher::getBlocks)
    ).apply(instance, BlockMatcher::new));

    private final List<Block> blocks;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;
    protected BlockMatcher(List<Block> blocks) {
        this.blocks = blocks;
        this.predicate = ((blockGetter, blockPos, blockState) -> blocks.contains(blockState.getBlock()));
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Type<?> getType() {
        return StateMatcherRegistry.BLOCK.get();
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return predicate;
    }
}
