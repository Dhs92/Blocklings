package com.blocklings.entity.ai;

import com.blocklings.block.BlockHelper;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlocklingAIFarm extends BlocklingAIGather
{
    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlockCrops targetCrop;
    private BlockPos targetPos;

    public BlocklingAIFarm(EntityBlockling blockling)
    {
        super(blockling);
        setMutexBits(3);
        repathMaxCount = 20;
        executeMaxTime = 2;
    }

    @Override
    public void resetTask()
    {
        targetCrop = null;
        targetPos = null;

        super.resetTask();
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (!blockling.isTaskActive(Task.FARM)) return false;
        if (!blockling.hasHoe()) return false;

        if (targetPos == null)
        {
            if (!findCrop()) return false;
        }

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (targetPos == null) return false;

        return true;
    }

    @Override
    public void updateTask()
    {
        if (targetPos != null)
        {
            IBlockState targetState = world.getBlockState(targetPos);
            if (!BlockHelper.isCrop(targetState.getBlock()) || !targetCrop.isMaxAge(targetState))
            {
                resetTask();
                return;
            }
            else if (AIHelper.distanceSqTo(blockling, targetPos) < blockling.getBlocklingStats().getFarmingRangeSq() + 1)
            {
                farmBlock();
                resetTask();
                repathTimer = repathMaxTimer;
                return;
            }

            boolean foundPath = false;
            if (repathTimer >= repathMaxTimer)
            {
                if (AIHelper.distanceSqTo(blockling, targetPos) >= blockling.getBlocklingStats().getFarmingRangeSq())
                {
                    Path testPath = AIHelper.getPathTo(blockling, targetPos, blockling.getBlocklingStats().getFarmingRangeSq());
                    if (testPath != null)
                    {
                        if (AIHelper.distanceSqFromPathEnd(testPath, targetPos) < blockling.getBlocklingStats().getFarmingRangeSq())
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
        }

        super.updateTask();
    }

    private void farmBlock()
    {
        IBlockState blockState = world.getBlockState(targetPos);

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

        addDropsToInventoryOrWorld(BlockHelper.getDrops(world, targetPos, blockState, mainStack, offStack), targetPos);

        ItemStack seedStack = ItemStack.EMPTY;
        try
        {
            Method getSeed = ReflectionHelper.findMethod(targetCrop.getClass(), "getSeed", null, null);
            getSeed.setAccessible(true);
            Item seedItem = (Item)getSeed.invoke(targetCrop);
            seedStack = new ItemStack(seedItem);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        if (blockling.getInv().takeStackFromInventory(seedStack))
        {
            world.setBlockState(targetPos, targetCrop.getDefaultState());
        }
        else
        {
            world.destroyBlock(targetPos, false);
        }
    }

    private boolean findCrop()
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

                    if (BlockHelper.isCrop(testBlock))
                    {
                        BlockCrops testBlockCrop = (BlockCrops)testBlock;
                        if (!testBlockCrop.isMaxAge(testState))
                        {
                            continue;
                        }

                        if (AIHelper.distanceSqTo(blockling, testPos) < blockling.getBlocklingStats().getFarmingRangeSq())
                        {
                            targetCrop = testBlockCrop;
                            targetPos = testPos;
                            return true;
                        }

                        Path testPath = AIHelper.getPathTo(blockling, testPos, blockling.getBlocklingStats().getFarmingRangeSq());
                        if (testPath != null)
                        {
                            if (AIHelper.distanceSqFromPathEnd(testPath, testPos) < blockling.getBlocklingStats().getFarmingRangeSq())
                            {
                                targetCrop = testBlockCrop;
                                targetPos = testPos;
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
