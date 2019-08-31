package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AIHelper
{
    /* Distance from center to center */
    public static double distanceSqTo(BlockPos fromPos, BlockPos toPos)
    {
        return fromPos.distanceSqToCenter(toPos.getX() + 0.5, toPos.getY() + 0.5, toPos.getZ() + 0.5);
    }

    /* Distance from center to center */
    public static double distanceSqTo(EntityBlockling blockling, BlockPos toPos)
    {
        double blocklingX = blockling.posX + blockling.width / 2;
        double blocklingY = blockling.posY + blockling.height / 2;
        double blocklingZ = blockling.posZ + blockling.width / 2;
        return toPos.distanceSqToCenter(blocklingX, blocklingY, blocklingZ);
    }

    public static double distanceSqFromPathEnd(Path path, BlockPos toPos)
    {
        return distanceSqTo(new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z), toPos);
    }

    public static boolean canSeeBlock(EntityBlockling blockling, BlockPos blockPos)
    {
        Vec3d blockVec = new Vec3d(blockPos);
        double height = 0.6F * blockling.getBlocklingStats().getScale();
        for (int it = 0; it < 2; it++)
        {
            double xStart = blockling.posX;
            double yStart;

            if (it == 0)
            {
                yStart = blockling.posY + height * 0.2D;
            }
            else
            {
                yStart = blockling.posY + height * 0.8D;
            }
            double zStart = blockling.posZ;
            Vec3d blocklingVec = new Vec3d(xStart, yStart, zStart);

            for (double i = 0.03D; i <= 0.97D; i += 0.94D)
            {
                for (double j = 0.03D; j <= 0.97D; j += 0.94D)
                {
                    for (double k = 0.03D; k <= 0.97D; k += 0.94D)
                    {
                        Vec3d testVec = new Vec3d(Math.floor(blockVec.x) + i, Math.floor(blockVec.y) + j, Math.floor(blockVec.z) + k);

                        RayTraceResult result = blockling.world.rayTraceBlocks(blocklingVec, testVec, true, true, true);
                        if (result != null)
                        {
                            BlockPos pos = result.getBlockPos();
                            if (pos.equals(new BlockPos(blockVec)))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Path getPathTo(EntityBlockling blockling, BlockPos blockPos, double rangeSq)
    {
        for (int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    BlockPos surroundingPos = new BlockPos(blockPos.getX() + i, blockPos.getY() + j, blockPos.getZ() + k);

                    Path testPath = blockling.getNavigator().getPathToPos(surroundingPos);
                    if (testPath != null)
                    {
                        PathPoint finalPoint = testPath.getFinalPathPoint();
                        BlockPos finalPos = new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);

                        // If we can't get in range of the block skip to next one
                        if (distanceSqTo(finalPos, blockPos) >= rangeSq)
                        {
                            continue;
                        }

                        if (!finalPos.equals(blockPos))
                        {
                            return testPath;
                        }
                    }
                }
            }
        }

        return blockling.getNavigator().getPathToPos(blockPos);
    }
}
