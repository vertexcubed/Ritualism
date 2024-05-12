package com.vertexcubed.ritualism.server.network;

import com.vertexcubed.ritualism.server.network.s2c.S2CMixingCauldronParticlesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;

import static com.vertexcubed.ritualism.Ritualism.modLoc;

public class PacketRegistry {

    public static SimpleChannel RITUALISM_CHANNEL;
    public static final String PROTOCOL_VERSION = "1";

    public static int index = 0;

    public static void register() {
        RITUALISM_CHANNEL = NetworkRegistry.newSimpleChannel(modLoc("main"),
                () -> PacketRegistry.PROTOCOL_VERSION, PacketRegistry.PROTOCOL_VERSION::equals, PacketRegistry.PROTOCOL_VERSION::equals);

        register(S2CMixingCauldronParticlesPacket::register);

    }

    private static void register(BiConsumer<SimpleChannel, Integer> con) {
        con.accept(RITUALISM_CHANNEL, index++);
    }

    public static <MSG> void sendToNearbyClients(Level level, BlockPos pos, MSG packet) {
        RITUALISM_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), packet);
    }
}
