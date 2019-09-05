package willr27.blocklings.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import willr27.blocklings.entity.blockling.BlocklingEntity;

public class AiUtil
{
    public static Path getPathTo(MobEntity entity, BlockPos blockPos, float rangeSq)
    {
        for (int x = -1; x <= 1; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    BlockPos surroundingPos = new BlockPos(blockPos).add(x, y, z);
                    Path path = entity.getNavigator().getPathToPos(surroundingPos, 1);

                    if (path != null)
                    {
                        double distanceSq = distanceSqFromTarget(path, blockPos);

                        if (distanceSq < rangeSq) return path;
                    }
                }
            }
        }

        return null;
    }

    public static double distanceSqFromTarget(Path path, BlockPos blockPos)
    {
        return blockPos.distanceSq(getFinalBlockPos(path));
    }

    public static BlockPos getFinalBlockPos(Path path)
    {
        PathPoint finalPoint = path.getFinalPathPoint();
        return new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
    }

    public static boolean canSeeBlock(BlocklingEntity blockling, BlockPos blockPos)
    {
        boolean blocked = true;
        for (int x = -1; x <= 1 && blocked; x++)
        {
            for (int y = -1; y <= 1 && blocked; y++)
            {
                for (int z = -1; z <= 1 && blocked; z++)
                {
                    BlockPos surroundingPos = new BlockPos(x, y, z);
                    BlockState surroundingState = blockling.world.getBlockState(surroundingPos);
                    Block surroundingBlock = surroundingState.getBlock();

                    if (surroundingBlock.isAir(surroundingState))
                    {
                        blocked = false;
                    }
                }
            }
        }
        if (blocked) return false;

        for (double x = 0.03; x < 1; x+= 0.94)
        {
            for (double y = 0.03; y < 1; y+= 0.94)
            {
                for (double z = 0.03; z < 1; z+= 0.94)
                {
                    Vec3d entityVec = new Vec3d(blockling.posX + x, blockling.posY + 0.5 + y, blockling.posZ + z);
                    Vec3d blockVec = new Vec3d(blockPos).add(0.5, 0.5, 0.5);

                    RayTraceContext ctx = new RayTraceContext(entityVec, blockVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, blockling);
                    BlockRayTraceResult result = blockling.world.rayTraceBlocks(ctx);

                    if (result != null)
                    {
                        if (result.getPos().equals(blockPos)) return true;
                    }
                }
            }
        }

        return false;
    }
}
