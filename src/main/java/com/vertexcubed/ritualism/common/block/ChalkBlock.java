package com.vertexcubed.ritualism.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChalkBlock extends Block {

    public static final VoxelShape HITBOX = Block.box(0, 0, 0, 16, 1, 16);
    public ChalkBlock(Properties pProperties) {
        super(pProperties.noCollission().noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return HITBOX;
    }
}
