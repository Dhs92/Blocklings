package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.State;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;

public class BlocklingAISit extends EntityAISit
{
    private EntityBlockling blockling;

    public BlocklingAISit(EntityBlockling blockling)
    {
        super(blockling);
        this.blockling = blockling;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getState() != State.SIT)
        {
            return false;
        }

        return super.shouldExecute();
    }
}
