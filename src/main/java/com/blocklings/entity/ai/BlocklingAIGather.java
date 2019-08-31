package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.State;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class BlocklingAIGather extends EntityAIBase
{
    protected EntityBlockling blockling;
    protected World world;

    protected int repathMaxTimer = 20;
    protected int repathTimer = 0;
    protected int repathMaxCount = 3;
    protected int repathCount = 0;

    protected int executeMaxTime = 10;
    protected int executeTimer = 0;

    public BlocklingAIGather(EntityBlockling blockling)
    {
        this.blockling = blockling;
        this.world = blockling.world;
        setMutexBits(3);
    }

    @Override
    public void resetTask()
    {
        repathTimer = repathMaxTimer;
        repathCount = 0;

        super.resetTask();
    }

    @Override
    public boolean shouldExecute()
    {
        if (executeTimer >= executeMaxTime)
        {
            executeTimer = 0;
        }
        else
        {
            executeTimer++;
            return false;
        }

        if (blockling.getState() == State.SIT) return false;

        return true;
    }

    protected void addDropsToInventoryOrWorld(NonNullList<ItemStack> drops)
    {
        for (ItemStack stack : drops)
        {
            ItemStack remainderStack = blockling.getInv().addItem(stack);
            if (!remainderStack.isEmpty()) InventoryHelper.spawnItemStack(world, blockling.posX, blockling.posY, blockling.posZ, remainderStack);
        }
    }
}
