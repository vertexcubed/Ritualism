package com.vertexcubed.ritualism.server.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import team.lodestar.lodestone.systems.network.LodestoneClientPacket;
import team.lodestar.lodestone.systems.network.LodestoneServerPacket;

import java.util.function.Supplier;

public class S2CGenericParticlePacket extends LodestoneClientPacket {

    public S2CGenericParticlePacket() {

    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        super.encode(buf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        super.execute(context);
    }

    public static void register(SimpleChannel channel, int index) {

    }
//    public static S2CGenericParticlePacket decode(FriendlyByteBuf buf) {
//
//    }
}
