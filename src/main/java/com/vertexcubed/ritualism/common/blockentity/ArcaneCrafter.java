package com.vertexcubed.ritualism.common.blockentity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ArcaneCrafter {

    void activate(Level level, Player player, InteractionHand hand);

    boolean canActivate(Level level, Player player, InteractionHand hand);
}
