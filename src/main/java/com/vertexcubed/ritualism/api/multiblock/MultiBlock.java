package com.vertexcubed.ritualism.api.multiblock;


import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.multiblock.matcher.AirMatcher;
import com.vertexcubed.ritualism.common.multiblock.matcher.AnyMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

/**
 * Basic multiblock system for validating and creating json multiblocks.
 * Based on Modonomicon/Patchouli but tweaked to fit my needs.
 * Todo: yoink dense multiblocks from modonomicon?
 * Todo 2: "metadata" fields. This would be machine specific and would probably need to be registered.
 */
public class MultiBlock {

    public static final ResourceKey<Registry<MultiBlock>> REGISTRY = ResourceKey.createRegistryKey(Ritualism.modLoc("multiblocks"));

    public static final Codec<MultiBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().listOf().fieldOf("pattern").forGetter(multiblock -> multiblock._pattern),
            Codec.unboundedMap(Codec.STRING, StateMatcher.CODEC).fieldOf("mappings").forGetter(multiblock -> multiblock._mappings)
    ).apply(instance, MultiBlock::new));


    //for internal use and serialization
    private final List<List<String>> _pattern;
    private final Map<String, StateMatcher> _mappings;


    private StateMatcher[][][] matchers;
    private Vec3i center;
    private final Vec3i size;


    public MultiBlock(List<List<String>> pattern, Map<String, StateMatcher> mappings) {
        this._pattern = pattern;
        _mappings = new ImmutableMap.Builder<String, StateMatcher>()
                //hardcoded keys. Can be overriden (hence buildKeepingLast instead of buildOrThrow)
                .put(" ", new AirMatcher())
                .put("_", new AnyMatcher())
                .putAll(mappings)
                .buildKeepingLast();
        this.size = getPatternSize(pattern);
        buildMultiBlock();
    }

    /**
     * Determines the size of the multiblock and errors if not a box.
     */
    private static Vec3i getPatternSize(List<List<String>> pattern) {
        int x = -1;
        int z = -1;
        for(List<String> layer : pattern) {
            if(x == -1) {
                x = layer.size();
            }
            if(layer.size() != x) throw new IllegalArgumentException("Inconsistent list size, expected: " + x + ", got:" + layer.size());
            for(String str : layer) {
                if(z == -1) {
                    z = str.length();
                }
                if(str.length() != z) throw new IllegalArgumentException("Inconsistent string size, expected: " + z + ", got: " + str.length());
            }
        }
        return new Vec3i(x, pattern.size(), z);
    }


    private void buildMultiBlock() {

        center = null;
        matchers = new StateMatcher[size.getX()][size.getY()][size.getZ()];
        for(int x  = 0; x < size.getX(); x++) {
            for(int y = 0; y < size.getY(); y++) {
                for(int z = 0; z < size.getZ(); z++) {
                    String c = String.valueOf(_pattern.get(y).get(x).charAt(z));
                    if(!_mappings.containsKey(c)) {
                        throw new IllegalArgumentException("Key " + c + "not in mappings!");
                    }
                    if(c.equals("0")) {
                        if(center != null) throw new IllegalArgumentException("Cannot have more than one core!");
                        this.center = new Vec3i(x, y, z);
                    }

                    this.matchers[x][y][z] = _mappings.get(c);
                }
            }
        }
    }

    //x y z starting from 0,0,0 in multiblock space
    public boolean test(Level level, BlockPos center, int x, int y, int z) {

        if(x < 0 || y < 0 || z < 0 || x > size.getX() || y > size.getY() || z > size.getZ()) return false;

        BlockPos checkPos = center.subtract(this.center).offset(x, y, z);
        BlockState blockState = level.getBlockState(checkPos);

        return matchers[x][y][z].getStatePredicate().test(level, checkPos, blockState);
    }

    public boolean test(Level level, BlockPos center) {
        for(int x = 0; x < size.getX(); x++) {
            for(int y = 0; y < size.getY(); y++) {
                for(int z = 0; z < size.getZ(); z++) {
                    if(!test(level, center, x, y, z)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
