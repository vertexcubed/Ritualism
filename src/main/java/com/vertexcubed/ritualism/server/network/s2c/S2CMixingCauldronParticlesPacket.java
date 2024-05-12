package com.vertexcubed.ritualism.server.network.s2c;

import com.vertexcubed.ritualism.Ritualism;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.network.LodestoneClientPacket;
import team.lodestar.lodestone.systems.network.LodestoneServerPacket;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;

import java.awt.*;
import java.util.function.Supplier;

public class S2CMixingCauldronParticlesPacket extends LodestoneClientPacket {


    private final OpCode opCode;
    private final BlockPos pos;
    public S2CMixingCauldronParticlesPacket(OpCode opCode, BlockPos pos) {
        this.opCode = opCode;
        this.pos = pos;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(opCode.ordinal());
        buf.writeBlockPos(pos);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        Level level = Minecraft.getInstance().level;
        if(opCode == OpCode.SUCCESS) {
            success(context.get(), level);
        }
        else if(opCode == OpCode.FAIL) {
            fail(context.get(), level);
        }
    }

    private void success(NetworkEvent.Context ctx, Level level) {

        Ritualism.LOGGER.debug("Spawning success particles...");
        Vec3 center = pos.getCenter();
        for(int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.INSTANT_EFFECT, center.x, center.y + 0.5, center.z,0.1, 0.7, 0.1);
        }
    }
    private void fail(NetworkEvent.Context ctx, Level level) {
        Ritualism.LOGGER.debug("Spawning fail particles...");
        Vec3 center = pos.getCenter();
        for(int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.INSTANT_EFFECT, center.x, center.y + 1.0, center.z,0.1, 0.7, 0.1);
        }
        WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
                .setTransparencyData(GenericParticleData.create(0.75f, 0).build())
                .setColorData(ColorParticleData.create(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f).build())
                .setScaleData(GenericParticleData.create(0.75f, 0.65f).build())
                .setLifetime(100)
                .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                .setRandomMotion(0.025f, 0, 0.025f)
                .setRandomOffset(0.5f)
                .setGravityStrength(0.05f)
                .repeatSurroundBlock(level, pos.above(), 4);
    }



    public static void register(SimpleChannel channel, int index) {
        channel.registerMessage(index, S2CMixingCauldronParticlesPacket.class, S2CMixingCauldronParticlesPacket::encode, S2CMixingCauldronParticlesPacket::decode, S2CMixingCauldronParticlesPacket::handle);
    }
    public static S2CMixingCauldronParticlesPacket decode(FriendlyByteBuf buf) {
        OpCode opCode = OpCode.values()[buf.readVarInt()];
        BlockPos pos = buf.readBlockPos();
        return new S2CMixingCauldronParticlesPacket(opCode, pos);
    }


    public enum OpCode {
        SUCCESS,
        FAIL
    }
}
