package willr27.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import willr27.blocklings.config.BlocklingsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockUtil
{
    public static List<Block> ORES = new ArrayList<>();
    static
    {
        ORES.add(Blocks.COAL_ORE);
        ORES.add(Blocks.IRON_ORE);
        ORES.add(Blocks.GOLD_ORE);
        ORES.add(Blocks.LAPIS_ORE);
        ORES.add(Blocks.REDSTONE_ORE);
        ORES.add(Blocks.EMERALD_ORE);
        ORES.add(Blocks.DIAMOND_ORE);
        ORES.add(Blocks.NETHER_QUARTZ_ORE);

//        ORES.add(Blocks.SANDSTONE);
//        ORES.add(Blocks.SAND);
//        ORES.add(Blocks.STONE);
//        ORES.add(Blocks.COBBLESTONE);
//        ORES.add(Blocks.GRANITE);
//        ORES.add(Blocks.GLOWSTONE);
//        ORES.add(Blocks.DIORITE);
//        ORES.add(Blocks.DIRT);
//
//        ORES.add(Blocks.COARSE_DIRT);
//        ORES.add(Blocks.MYCELIUM);
//        ORES.add(Blocks.MAGENTA_CONCRETE);
//        ORES.add(Blocks.MAGENTA_STAINED_GLASS);
//        ORES.add(Blocks.BROWN_CONCRETE);
//        ORES.add(Blocks.BLUE_CONCRETE);
//        ORES.add(Blocks.ACACIA_FENCE);
//        ORES.add(Blocks.SPRUCE_LEAVES);
//
//        ORES.add(Blocks.SANDSTONE_STAIRS);
//        ORES.add(Blocks.YELLOW_WOOL);
//        ORES.add(Blocks.YELLOW_BED);
//        ORES.add(Blocks.ACACIA_LEAVES);
//        ORES.add(Blocks.DARK_OAK_LEAVES);
//        ORES.add(Blocks.JUNGLE_LEAVES);
//        ORES.add(Blocks.OAK_LEAVES);
//        ORES.add(Blocks.BIRCH_LEAVES);
//
//        ORES.add(Blocks.SOUL_SAND);
    }
    public static boolean isOre(Block block)
    {
        return ORES.contains(block);
    }


    public static List<Block> LOGS = new ArrayList<>();
    static
    {
        LOGS.add(Blocks.ACACIA_LOG);
        LOGS.add(Blocks.BIRCH_LOG);
        LOGS.add(Blocks.DARK_OAK_LOG);
        LOGS.add(Blocks.JUNGLE_LOG);
        LOGS.add(Blocks.OAK_LOG);
        LOGS.add(Blocks.SPRUCE_LOG);
    }
    public static boolean isLog(Block block)
    {
        return LOGS.contains(block);
    }


    public static Map<Block, Item> CROPS_SEEDS = new HashMap<>();
    static
    {
        CROPS_SEEDS.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        CROPS_SEEDS.put(Blocks.CARROTS, Items.CARROT);
        CROPS_SEEDS.put(Blocks.POTATOES, Items.POTATO);
        CROPS_SEEDS.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
    }
    public static boolean isCrop(Block block)
    {
        return CROPS_SEEDS.keySet().contains(block);
    }
    public static boolean isGrown(BlockState state)
    {
        return ((CropsBlock)state.getBlock()).isMaxAge(state);
    }

    public static boolean isSeed(Item item)
    {
        return CROPS_SEEDS.values().contains(item);
    }
    public static Item getSeed(CropsBlock block)
    {
        String registryName = block.getRegistryName().toString();
        String itemName = BlocklingsConfig.getCropsSeeds().get(registryName);
        return Registry.ITEM.getOrDefault(new ResourceLocation(itemName));
    }
}
