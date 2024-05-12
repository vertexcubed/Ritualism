package com.vertexcubed.ritualism.common.item;

import com.vertexcubed.ritualism.common.blockentity.ArcaneCrafter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

//todo: replace this with tag based logic
public class ArcaneStaffItem extends Item {
    public ArcaneStaffItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getPlayer().isShiftKeyDown()) {
            BlockPos pos = pContext.getClickedPos();
            Level level = pContext.getLevel();
            BlockEntity be = level.getBlockEntity(pos);
            if(be instanceof ArcaneCrafter crafter && crafter.canActivate(level, pContext.getPlayer(), pContext.getHand())) {
                crafter.activate(level, pContext.getPlayer(), pContext.getHand());
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(pContext);
    }
}
