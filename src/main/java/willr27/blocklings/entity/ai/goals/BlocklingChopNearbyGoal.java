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

public class BlocklingChopNearbyGoal extends Goal
{
    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlocklingEntity blockling;
    private World world;

    private Set<BlockPos> failedBlocks = new LinkedHashSet<>();
    private Set<BlockPos> tree = new LinkedHashSet<>();
    private BlockPos treeStartPos;
    private BlockPos targetPos;

    public BlocklingChopNearbyGoal(BlocklingEntity blockling)
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
        tree.clear();
        treeStartPos = null;
        targetPos = null;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getThousandTimer() % 80 == 0) failedBlocks.clear();
        if (blockling.getThousandTimer() % 20 != 0) return false;
        if (!blockling.aiManager.isActive(AIManager.CHOP_NEARBY_ID)) return false;

        if (!findTreeStart()) return false;
        if (!findTree()) return false;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (tree.isEmpty()) return false;

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
                BlockPos logPos = (BlockPos) tree.toArray()[tree.size() - 1];
                world.destroyBlock(logPos, false);
                tree.remove(logPos);
            }
        }
    }

    private void updateToValidTarget()
    {
        if (targetPos != null)
        {
            BlockState targetState = world.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if (blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_WHITELIST_ID).isInBlacklist(targetBlock))
            {
                tree.remove(targetPos);
                targetPos = null;
            }
//            else if (!blockling.hasMoved())
//            {
//                failedBlocks.add(targetPos);
//                tree.remove(targetPos);
//                targetPos = null;
//            }
        }
    }

    private void moveToTarget()
    {
        if (targetPos == null || blockling.getNavigator().getPath() == null /*|| !blockling.hasMoved()*/)
        {
            boolean foundTarget = false;

            for (BlockPos blockPos : tree)
            {
                if (AiUtil.canSeeBlock(blockling, blockPos))
                {
                    double distanceSq = blockling.getPosition().distanceSq(blockPos);

                    if (distanceSq < blockling.getStats().getWoodcuttingRangeSq())
                    {
                        targetPos = blockPos;
                        foundTarget = true;
                        break;
                    }

                    Path path = AiUtil.getPathTo(blockling, blockPos, blockling.getStats().getWoodcuttingRangeSq());

                    if (path != null)
                    {
                        distanceSq = AiUtil.distanceSqFromTarget(path, blockPos);

                        if (distanceSq < blockling.getStats().getWoodcuttingRangeSq())
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
                failedBlocks.addAll(tree);
                tree.clear();
            }
        }
    }

    private boolean findTreeStart()
    {
        int blocklingX = (int)Math.floor(blockling.posX);
        int blocklingY = (int)Math.floor(blockling.posY);
        int blocklingZ = (int)Math.floor(blockling.posZ);

        int startX = blocklingX - searchRadiusX;
        int startY = blocklingY - searchRadiusY;
        int startZ = blocklingZ - searchRadiusX;

        int endX = blocklingX + searchRadiusX + 1;
        int endY = blocklingY + searchRadiusY + 1;
        int endZ = blocklingZ + searchRadiusX + 1;

        for (int y = startY; y < endY; y++)
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

                    if (blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_WHITELIST_ID).isInWhitelist(testBlock))
                    {
                        if (AiUtil.canSeeBlock(blockling, testPos))
                        {
                            double distanceSq = blockling.getPosition().distanceSq(testPos);

                            if (distanceSq < blockling.getStats().getWoodcuttingRangeSq())
                            {
                                treeStartPos = testPos;
                                return true;
                            }

                            Path testPath = AiUtil.getPathTo(blockling, testPos, blockling.getStats().getWoodcuttingRangeSq());

                            if (testPath != null)
                            {
                                distanceSq = AiUtil.distanceSqFromTarget(testPath, testPos);

                                if (distanceSq < blockling.getStats().getWoodcuttingRangeSq())
                                {
                                    treeStartPos = testPos;
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

    private boolean findTree()
    {
        Set<BlockPos> positionsToTest = new HashSet<>();
        positionsToTest.add(treeStartPos);

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

                        if (tree.contains(surroundingPos))
                        {
                            continue;
                        }

                        if (blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_WHITELIST_ID).isInWhitelist(surroundingBlock))
                        {
                            positionsToTest.add(surroundingPos);
                        }
                    }
                }
            }

            tree.add(testPos);
            positionsToTest.remove(testPos);
        }

        return !tree.isEmpty();
    }
}
