package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TagRegistry {

    public static final TagKey<Block>
        HEAT_SOURCES = blockTag("heat_sources");


    private static TagKey<Block> blockTag(String name) {
        return BlockTags.create(new ResourceLocation(Ritualism.MOD_ID, name));
    }
}
