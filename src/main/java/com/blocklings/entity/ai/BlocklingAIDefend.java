package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;

public class BlocklingAIDefend extends BlocklingAITarget
{
    public BlocklingAIDefend(EntityBlockling blockling)
    {
        super(blockling);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (blockling.getAttackTarget() != null) return false;

        blockling.setAttackTarget(blockling.getRevengeTarget());

        return false;
    }
}
