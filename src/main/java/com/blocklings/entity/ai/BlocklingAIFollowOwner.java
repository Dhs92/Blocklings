package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.State;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISit;

public class BlocklingAIFollowOwner extends EntityAIFollowOwner
{
    private EntityBlockling blockling;

    public BlocklingAIFollowOwner(EntityBlockling blockling)
    {
        super(blockling, 1.0, 2.0f, 8.0f);
        this.blockling = blockling;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getState() != State.FOLLOW)
        {
            return false;
        }

        return super.shouldExecute();
    }
}
