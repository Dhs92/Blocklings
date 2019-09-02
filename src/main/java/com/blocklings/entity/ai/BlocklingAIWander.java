package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.State;
import net.minecraft.entity.ai.EntityAIWander;

public class BlocklingAIWander extends EntityAIWander
{
    private EntityBlockling blockling;

    public BlocklingAIWander(EntityBlockling blockling)
    {
        super(blockling, 0.5, 30);
        this.blockling = blockling;
        setMutexBits(3);
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
