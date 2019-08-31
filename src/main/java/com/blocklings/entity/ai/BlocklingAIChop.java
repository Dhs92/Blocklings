package com.blocklings.entity.ai;

import com.blocklings.block.BlockHelper;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlocklingAIChop extends BlocklingAIGather
{
    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private Set<BlockPos> tree = new LinkedHashSet<>();
    private BlockPos targetPos;

    public BlocklingAIChop(EntityBlockling blockling)
    {
        super(blockling);
        setMutexBits(3);
        repathMaxCount = 20;
    }

    @Override
    public void resetTask()
    {
        tree.clear();
        targetPos = null;

        super.resetTask();
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (!blockling.isTaskActive(Task.CHOP)) return false;
        if (!blockling.hasAxe()) return false;

        if (tree.isEmpty())
        {
            if (!findTreeStart()) return false;
            if (!findTree()) return false;
        }

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (tree.isEmpty()) return false;

        return true;
    }

    @Override
    public void updateTask()
    {
        if (targetPos != null)
        {
            if (!BlockHelper.isLog(world.getBlockState(targetPos).getBlock()))
            {
                tree.remove(tree.toArray()[tree.size()-1]);
            }
            else if (AIHelper.distanceSqTo(blockling, targetPos) < blockling.getBlocklingStats().getWoodcuttingRangeSq() + 1)
            {
                world.destroyBlock((BlockPos)tree.toArray()[tree.size()-1], false);
                tree.remove(tree.toArray()[tree.size()-1]);
                repathTimer = repathMaxTimer;
            }
        }

        boolean foundPath = false;
        if (repathTimer >= repathMaxTimer)
        {
            if (AIHelper.distanceSqTo(blockling, targetPos) >= blockling.getBlocklingStats().getWoodcuttingRangeSq())
            {
                Path testPath = AIHelper.getPathTo(blockling, targetPos, blockling.getBlocklingStats().getWoodcuttingRangeSq());
                if (testPath != null)
                {
                    if (AIHelper.distanceSqFromPathEnd(testPath, targetPos) < blockling.getBlocklingStats().getWoodcuttingRangeSq())
                    {
                        blockling.getNavigator().setPath(testPath, 0.5);
                        foundPath = true;
                    }
                }
            }
            if (!foundPath)
            {
                repathCount++;
            }
            repathTimer = 0;
        }
        if (repathCount > repathMaxCount)
        {
            //resetTask();
        }

        repathTimer++;

        super.updateTask();
    }

    private boolean findTreeStart()
    {
        int blocklingX = (int)blockling.posX;
        int blocklingY = (int)blockling.posY;
        int blocklingZ = (int)blockling.posZ;

        int startX = blocklingX - searchRadiusX;
        int startY = blocklingY - searchRadiusY;
        int startZ = blocklingZ - searchRadiusX;

        int endX = blocklingX + searchRadiusX;
        int endY = blocklingY + searchRadiusY;
        int endZ = blocklingZ + searchRadiusX;

        for (int y = startY; y < endY; y++)
        {
            for (int x = startX; x < endX; x++)
            {
                for (int z = startZ; z < endZ; z++)
                {
                    BlockPos testPos = new BlockPos(x, y, z);
                    IBlockState testState = world.getBlockState(testPos);
                    Block testBlock = testState.getBlock();

                    if (BlockHelper.isLog(testBlock))
                    {
                        if (AIHelper.canSeeBlock(blockling, testPos))
                        {
                            if (AIHelper.distanceSqTo(blockling, testPos) < blockling.getBlocklingStats().getWoodcuttingRangeSq())
                            {
                                targetPos = testPos;
                                return true;
                            }

                            Path testPath = AIHelper.getPathTo(blockling, testPos, blockling.getBlocklingStats().getWoodcuttingRangeSq());
                            if (testPath != null)
                            {
                                if (AIHelper.distanceSqFromPathEnd(testPath, testPos) < blockling.getBlocklingStats().getWoodcuttingRangeSq())
                                {
                                    targetPos = testPos;
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
        positionsToTest.add(targetPos);

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
                        IBlockState surroundingState = world.getBlockState(surroundingPos);
                        Block surroundingBlock = surroundingState.getBlock();

                        // Don't test the current block we are testing
                        if (surroundingPos == testPos)
                        {
                            continue;
                        }

                        // If we have already added this block don't test it
                        if (tree.contains(surroundingPos))
                        {
                            continue;
                        }

                        if (BlockHelper.isLog(surroundingBlock))
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
