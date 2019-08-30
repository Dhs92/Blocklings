package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.State;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIWander;

public class BlocklingAIWander extends EntityAIWander
{
    private EntityBlockling blockling;

    public BlocklingAIWander(EntityBlockling blockling)
    {
        super(blockling, 1.0, 10);
        this.blockling = blockling;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getState() != State.WANDER)
        {
            return false;
        }

        return super.shouldExecute();
    }
}
