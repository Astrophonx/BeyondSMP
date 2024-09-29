package io.astrophonx.beyondsmp.blocks;

import io.astrophonx.beyondsmp.BeyondSMP;
import io.astrophonx.beyondsmp.blocks.custom.SubwooferBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    // DeferredRegister for blocks
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BeyondSMP.MODID);

    // DeferredRegister for items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BeyondSMP.MODID);

    // Register the SubwooferBlock with the necessary properties
    public static final RegistryObject<Block> SUBWOOFER_BLOCK = BLOCKS.register("subwoofer",
            () -> new SubwooferBlock(BlockBehaviour.Properties.of().strength(5.0f)));

    // Register the BlockItem for the SubwooferBlock
    private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Method to register both block and block item
    private static void registerBlock(String name, RegistryObject<Block> block) {
        registerBlockItem(name, block);
    }

    // Register all mod blocks
    public static void registerModBlocks() {
        BeyondSMP.LOGGER.info("Registering Mod Blocks for " + BeyondSMP.MODID);

        // Register blocks and block items here
        registerBlock("subwoofer", SUBWOOFER_BLOCK);
    }
}