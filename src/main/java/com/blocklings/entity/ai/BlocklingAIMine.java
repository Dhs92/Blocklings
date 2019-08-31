package com.blocklings.entity.ai;

import com.blocklings.block.BlockHelper;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class BlocklingAIMine extends BlocklingAIGather
{
    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private Set<BlockPos> vein = new HashSet<>();
    private BlockPos targetPos;

    public BlocklingAIMine(EntityBlockling blockling)
    {
        super(blockling);
        setMutexBits(3);
    }

    @Override
    public void resetTask()
    {
        vein.clear();
        targetPos = null;

        super.resetTask();
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (!blockling.isTaskActive(Task.MINE)) return false;
        if (!blockling.hasPickaxe()) return false;

        if (vein.isEmpty())
        {
            if (!findVeinStart()) return false;
            if (!findVein()) return false;
        }

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (vein.isEmpty()) return false;

        return true;
    }

    @Override
    public void updateTask()
    {
        if (targetPos != null)
        {
            if (!BlockHelper.isOre(world.getBlockState(targetPos).getBlock()))
            {
                vein.remove(targetPos);
                targetPos = null;
            }
            else if (AIHelper.distanceSqTo(blockling, targetPos) < blockling.getBlocklingStats().getMiningRangeSq() + 1)
            {
                mineBlock(targetPos);
                vein.remove(targetPos);
                targetPos = null;
                repathTimer = repathMaxTimer;
            }
        }

        if (repathTimer >= repathMaxTimer)
        {
            boolean foundPath = false;
            int highestY = -1;
            for (BlockPos testPos : vein)
            {
                if (!AIHelper.canSeeBlock(blockling, testPos))
                {
                    continue;
                }

                if (AIHelper.distanceSqTo(blockling, testPos) < blockling.getBlocklingStats().getMiningRangeSq())
                {
                    targetPos = testPos;
                    highestY = testPos.getY();
                    foundPath = true;
                    break;
                }

                Path testPath = AIHelper.getPathTo(blockling, testPos, blockling.getBlocklingStats().getMiningRangeSq());
                if (testPath != null)
                {
                    if (AIHelper.distanceSqFromPathEnd(testPath, testPos) < blockling.getBlocklingStats().getMiningRangeSq())
                    {
                        if (testPos.getY() > highestY)
                        {
                            blockling.getNavigator().setPath(testPath, 0.5);
                            targetPos = testPos;
                            highestY = testPos.getY();
                            foundPath = true;
                        }
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
            resetTask();
        }

        repathTimer++;

        super.updateTask();
    }

    private void mineBlock(BlockPos blockPos)
    {
        IBlockState blockState = world.getBlockState(blockPos);

        ItemStack mainStack = blockling.getHeldItemMainhand();
        ItemStack offStack = blockling.getHeldItemOffhand();

        boolean canMainHarvest = mainStack.canHarvestBlock(blockState);
        boolean canOffHarvest = offStack.canHarvestBlock(blockState);

        if (!canMainHarvest)
        {
            mainStack = ItemStack.EMPTY;
        }
        if (!canOffHarvest)
        {
            offStack = ItemStack.EMPTY;
        }

        world.destroyBlock(blockPos, false);
        addDropsToInventoryOrWorld(BlockHelper.getDrops(world, blockPos, blockState, mainStack, offStack));
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

        for (int y = startY; y > endY; y--)
        {
            for (int x = startX; x < endX; x++)
            {
                for (int z = startZ; z < endZ; z++)
                {
                    BlockPos testPos = new BlockPos(x, y, z);
                    IBlockState testState = world.getBlockState(testPos);
                    Block testBlock = testState.getBlock();

                    if (BlockHelper.isOre(testBlock))
                    {
                        if (AIHelper.canSeeBlock(blockling, testPos))
                        {
                            if (AIHelper.distanceSqTo(blockling, testPos) < blockling.getBlocklingStats().getMiningRangeSq())
                            {
                                targetPos = testPos;
                                return true;
                            }

                            Path testPath = AIHelper.getPathTo(blockling, testPos, blockling.getBlocklingStats().getMiningRangeSq());
                            if (testPath != null)
                            {
                                if (AIHelper.distanceSqFromPathEnd(testPath, testPos) < blockling.getBlocklingStats().getMiningRangeSq())
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

    private boolean findVein()
    {
        Set<BlockPos> positionsToTest = new HashSet<>();
        positionsToTest.add(targetPos);
        targetPos = null;

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
                        if (vein.contains(surroundingPos))
                        {
                            continue;
                        }

                        if (BlockHelper.isOre(surroundingBlock))
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
