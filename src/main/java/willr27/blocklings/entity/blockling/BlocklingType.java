package willr27.blocklings.entity.blockling;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.util.BlocklingsResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlocklingType
{
    public static final BlocklingType GRASS = new BlocklingType("grass");
    public static final BlocklingType OAK_LOG = new BlocklingType("oak_log");

    static
    {
        GRASS.predicates.add(blockling -> blockBelowIs(blockling, Blocks.GRASS_BLOCK));

        OAK_LOG.predicates.add(blockling -> blockBelowIs(blockling, Blocks.STONE));
    }

    public final ResourceLocation entityTexture;
    public List<Predicate<BlocklingEntity>> predicates = new ArrayList<>();

    public BlocklingType(String texture)
    {
        this.entityTexture = new BlocklingsResourceLocation("textures/entities/blockling/blockling_" + texture + ".png");
    }


    private static boolean blockBelowIs(BlocklingEntity blockling, Block block)
    {
        return blockBelowIs(blockling, new Block[] {block});
    }

    private static boolean blockBelowIs(BlocklingEntity blockling, Block[] blocks)
    {
        Block testBlock = blockling.world.getBlockState(blockling.getPosition().down()).getBlock();
        for (Block block : blocks)
        {
            if (testBlock == block)
            {
                return true;
            }
        }
        return false;
    }

    private static boolean blockNearbyIs(BlocklingEntity blockling, Block[] blocks, int radius)
    {
        return true;
    }
}
