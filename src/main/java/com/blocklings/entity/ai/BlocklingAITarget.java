package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class BlocklingAITarget extends EntityAIBase
{
    protected EntityBlockling blockling;
    protected World world;

    public BlocklingAITarget(EntityBlockling blockling)
    {
        this.blockling = blockling;
        this.world = blockling.world;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getAttackTarget() != null && !blockling.getAttackTarget().isEntityAlive())
        {
            blockling.setAttackTarget(null);
        }

        return true;
    }
}
