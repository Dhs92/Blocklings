package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import willr27.blocklings.block.BlockUtil;
import willr27.blocklings.entity.BlocklingEntity;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.ai.AiUtil;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlocklingMineNearbyGoal extends Goal
{
    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlocklingEntity blockling;
    private World world;

    private Set<BlockPos> vein = new LinkedHashSet<>();
    private BlockPos veinStartPos;
    private BlockPos targetPos;

    private double range = 3;
    private double rangeSq = 9;

    public BlocklingMineNearbyGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
        this.world = blockling.world;
    }

    @Override
    public void startExecuting()
    {

    }

    @Override
    public void resetTask()
    {
        vein.clear();
        veinStartPos = null;
        targetPos = null;
    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.MINE_NEARBY_ID)) return false;

        if (!findVeinStart()) return false;
        if (!findVein()) return false;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (vein.isEmpty()) return false;

        return true;
    }

    @Override
    public void tick()
    {
        updateToValidTarget();
        moveToTarget();
        tryMineTarget();
    }

    private void tryMineTarget()
    {
        if (targetPos != null)
        {
            double distanceSq = blockling.getPosition().distanceSq(targetPos);

            if (distanceSq < rangeSq)
            {
                world.destroyBlock(targetPos, false);
                vein.remove(targetPos);
                targetPos = null;
            }
        }
    }

    private void updateToValidTarget()
    {
        if (targetPos != null)
        {
            BlockState targetState = world.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if (!BlockUtil.isOre(targetBlock))
            {
                vein.remove(targetPos);
                targetPos = null;
            }
        }
    }

    private void moveToTarget()
    {
        if (targetPos == null || blockling.getNavigator().getPath() == null || !blockling.velocityChanged)
        {
            for (BlockPos blockPos : vein)
            {
                Path path = AiUtil.getPathTo(blockling, blockPos);

                if (path != null)
                {
                    double distanceSq = AiUtil.distanceSqFromTarget(path, blockPos);

                    if (distanceSq < rangeSq)
                    {
                        targetPos = blockPos;
                        blockling.getNavigator().setPath(path, 1.0);
                        return;
                    }
                }
            }

            vein.clear();
        }
    }

    private boolean findVeinStart()
    {
        int blocklingX = (int)blockling.posX;
        int blocklingY = (int)blockling.posY;
        int blocklingZ = (int)blockling.posZ;

        int startX = blocklingX - searchRadiusX;
        int startY = blocklingY + searchRadiusY;
        int startZ = blocklingZ - searchRadiusX;

        int endX = blocklingX + searchRadiusX;
        int endY = blocklingY - searchRadiusY;
        int endZ = blocklingZ + searchRadiusX;

        BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();
        for (int y = startY; y > endY; y--)
        {
            for (int x = startX; x < endX; x++)
            {
                for (int z = startZ; z < endZ; z++)
                {
                    testPos.setPos(x, y, z);
                    BlockState testState = world.getBlockState(testPos);
                    Block testBlock = testState.getBlock();

                    if (BlockUtil.isOre(testBlock))
                    {
                        Path testPath = AiUtil.getPathTo(blockling, testPos);

                        if (testPath != null)
                        {
                            double distanceSq = AiUtil.distanceSqFromTarget(testPath, testPos);

                            if (distanceSq < rangeSq)
                            {
                                veinStartPos = testPos;
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean findVein()
    {
        Set<BlockPos> positionsToTest = new HashSet<>();
        positionsToTest.add(veinStartPos);

        while (!positionsToTest.isEmpty())
        {
            BlockPos testPos = (BlockPos)positionsToTest.toArray()[0];

            int startX = testPos.getX() - 1;
            int startY = testPos.getY() - 1;
            int startZ = testPos.getZ() - 1;

            int endX = startX + 2;
            int endY = startY + 2;
            int endZ = startZ + 2;

            for (int x = startX; x <= endX; x++)
            {
                for (int y = startY; y <= endY; y++)
                {
                    for (int z = startZ; z <= endZ; z++)
                    {
                        BlockPos surroundingPos = new BlockPos(x, y, z);
                        BlockState surroundingState = world.getBlockState(surroundingPos);
                        Block surroundingBlock = surroundingState.getBlock();

                        if (surroundingPos == testPos)
                        {
                            continue;
                        }

                        if (vein.contains(surroundingPos))
                        {
                            continue;
                        }

                        if (BlockUtil.isOre(surroundingBlock))
                        {
                            positionsToTest.add(surroundingPos);
                        }
                    }
                }
            }

            vein.add(testPos);
            positionsToTest.remove(testPos);
        }

        return !vein.isEmpty();
    }
}
