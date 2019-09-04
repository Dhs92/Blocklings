package willr27.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil
{
    public static final List<Block> ORES = new ArrayList<>();
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
    }
    public static boolean isOre(Block block)
    {
        return ORES.contains(block);
    }
}
