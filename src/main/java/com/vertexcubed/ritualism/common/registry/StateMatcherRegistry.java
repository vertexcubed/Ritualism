package com.vertexcubed.ritualism.common.registry;

import com.mojang.serialization.Codec;
import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.api.multiblock.StateMatcher;
import com.vertexcubed.ritualism.common.multiblock.matcher.AirMatcher;
import com.vertexcubed.ritualism.common.multiblock.matcher.AnyMatcher;
import com.vertexcubed.ritualism.common.multiblock.matcher.BlockMatcher;
import com.vertexcubed.ritualism.common.multiblock.matcher.TagMatcher;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class StateMatcherRegistry {
    public static final DeferredRegister<StateMatcher.Type<?>> STATE_MATCHERS = DeferredRegister.create(Ritualism.modLoc("state_matchers"), Ritualism.MOD_ID);
    public static final Supplier<IForgeRegistry<StateMatcher.Type<?>>> STATE_MATCHER_REGISTRY = STATE_MATCHERS.makeRegistry(RegistryBuilder::new);


    public static final RegistryObject<StateMatcher.Type<AnyMatcher>> ANY = register("any", AnyMatcher.CODEC);
    public static final RegistryObject<StateMatcher.Type<AirMatcher>> AIR = register("air", AirMatcher.CODEC);
    public static final RegistryObject<StateMatcher.Type<BlockMatcher>> BLOCK = register("block", BlockMatcher.CODEC);
    public static final RegistryObject<StateMatcher.Type<TagMatcher>> TAG = register("tag", TagMatcher.CODEC);


    private static <S extends StateMatcher> RegistryObject<StateMatcher.Type<S>> register(String name, Codec<S> codec) {
        return STATE_MATCHERS.register(name, () -> new StateMatcher.Type<>(codec));
    }


    public static void register(IEventBus eventBus) {
        STATE_MATCHERS.register(eventBus);
    }
}
