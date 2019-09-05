package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.ai.AiUtil;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlocklingMineNearbyGoal extends Goal
{
    public static final int ORE_WHITELIST_ID = 0;

    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlocklingEntity blockling;
    private World world;

    private Set<BlockPos> failedBlocks = new LinkedHashSet<>();
    private Set<BlockPos> vein = new LinkedHashSet<>();
    private BlockPos veinStartPos;
    private BlockPos targetPos;

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
        if (blockling.getThousandTimer() % 80 == 0) failedBlocks.clear();
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

            if (distanceSq < blockling.getStats().getMiningRangeSq())
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

            if (blockling.aiManager.getWhitelist(AIManager.MINE_NEARBY_ID, ORE_WHITELIST_ID).isInBlacklist(targetBlock))
            {
                vein.remove(targetPos);
                targetPos = null;
            }
            else if (!blockling.hasMoved())
            {
                failedBlocks.add(targetPos);
                vein.remove(targetPos);
                targetPos = null;
            }
        }
    }

    private void moveToTarget()
    {
        if (targetPos == null || blockling.getNavigator().getPath() == null || !blockling.hasMoved())
        {
            boolean foundTarget = false;

            for (BlockPos blockPos : vein)
            {
                if (AiUtil.canSeeBlock(blockling, blockPos))
                {
                    double distanceSq = blockling.getPosition().distanceSq(blockPos);

                    if (distanceSq < blockling.getStats().getMiningRangeSq())
                    {
                        targetPos = blockPos;
                        foundTarget = true;
                        break;
                    }

                    Path path = AiUtil.getPathTo(blockling, blockPos, blockling.getStats().getMiningRangeSq());

                    if (path != null)
                    {
                        distanceSq = AiUtil.distanceSqFromTarget(path, blockPos);

                        if (distanceSq < blockling.getStats().getMiningRangeSq())
                        {
                            targetPos = blockPos;
                            blockling.getNavigator().setPath(path, 1.0);
                            foundTarget = true;
                            break;
                        }
                    }
                }
            }

            if (!foundTarget)
            {
                failedBlocks.addAll(vein);
                vein.clear();
            }
        }
    }

    private boolean findVeinStart()
    {
        int blocklingX = (int)Math.floor(blockling.posX);
        int blocklingY = (int)Math.floor(blockling.posY);
        int blocklingZ = (int)Math.floor(blockling.posZ);

        int startX = blocklingX - searchRadiusX;
        int startY = blocklingY + searchRadiusY;
        int startZ = blocklingZ - searchRadiusX;

        int endX = blocklingX + searchRadiusX + 1;
        int endY = blocklingY - searchRadiusY - 1;
        int endZ = blocklingZ + searchRadiusX + 1;

        for (int y = startY; y > endY; y--)
        {
            for (int x = startX; x < endX; x++)
            {
                for (int z = startZ; z < endZ; z++)
                {
                    BlockPos testPos = new BlockPos(x, y, z);
                    BlockState testState = world.getBlockState(testPos);
                    Block testBlock = testState.getBlock();

                    if (failedBlocks.contains(testPos))
                    {
                        continue;
                    }

                    if (blockling.aiManager.getWhitelist(AIManager.MINE_NEARBY_ID, ORE_WHITELIST_ID).isInWhitelist(testBlock))
                    {
                        if (AiUtil.canSeeBlock(blockling, testPos))
                        {
                            double distanceSq = blockling.getPosition().distanceSq(testPos);

                            if (distanceSq < blockling.getStats().getMiningRangeSq())
                            {
                                veinStartPos = testPos;
                                return true;
                            }

                            Path testPath = AiUtil.getPathTo(blockling, testPos, blockling.getStats().getMiningRangeSq());

                            if (testPath != null)
                            {
                                distanceSq = AiUtil.distanceSqFromTarget(testPath, testPos);

                                if (distanceSq < blockling.getStats().getMiningRangeSq())
                                {
                                    veinStartPos = testPos;
                                    return true;
                                }
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

                        if (blockling.aiManager.getWhitelist(AIManager.MINE_NEARBY_ID, ORE_WHITELIST_ID).isInWhitelist(surroundingBlock))
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
