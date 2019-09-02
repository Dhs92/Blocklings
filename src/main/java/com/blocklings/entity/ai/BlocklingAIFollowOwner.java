package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.State;
import net.minecraft.entity.ai.EntityAIFollowOwner;

public class BlocklingAIFollowOwner extends EntityAIFollowOwner
{
    private EntityBlockling blockling;

    private int followTimer = 0;
    private int followTimerMax = 20;

    public BlocklingAIFollowOwner(EntityBlockling blockling)
    {
        super(blockling, 1.0, 2.0f, 2.0f);
        this.blockling = blockling;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getState() != State.FOLLOW) return false;

        if (followTimer > 5)
        {
            followTimer = 0;
            return super.shouldExecute();
        }

        followTimer++;
        return false;
    }
}
