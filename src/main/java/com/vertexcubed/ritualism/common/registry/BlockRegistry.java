package com.vertexcubed.ritualism.common.registry;

import com.vertexcubed.ritualism.Ritualism;
import com.vertexcubed.ritualism.common.block.MixingCauldronBlock;
import com.vertexcubed.ritualism.common.blockentity.MixingCauldronBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ritualism.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ritualism.MOD_ID);

    public static final RegistryObject<Block>
            MIXING_CAULDRON = BLOCKS.register("mixing_cauldron", () -> new MixingCauldronBlock(
                    BlockBehaviour.Properties.of()
                            .pushReaction(PushReaction.BLOCK)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
                            .strength(3.5f)
                            .noOcclusion()
                    ))
            ;


    public static final RegistryObject<BlockEntityType<MixingCauldronBlockEntity>> MIXING_CAULDRON_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("mixing_cauldron_block_entity", () -> BlockEntityType.Builder.of(MixingCauldronBlockEntity::new, MIXING_CAULDRON.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public static void registerBlockItems(final RegisterEvent event) {
        BLOCKS.getEntries().forEach(block -> {
            event.register(ForgeRegistries.Keys.ITEMS,
                    helper -> helper.register(new ResourceLocation(Objects.requireNonNull(block.getId().toString())), new BlockItem(block.get(), new Item.Properties())));
        });
    }
}
