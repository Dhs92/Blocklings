package willr27.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.List;

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
}
