package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class BlocklingAIHunt extends BlocklingAITarget
{
    public BlocklingAIHunt(EntityBlockling blockling)
    {
        super(blockling);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (!blockling.isTaskActive(Task.HUNT)) return false;
        if (blockling.getAttackTarget() != null) return false;

        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(blockling, new AxisAlignedBB(blockling.getPositionVector().subtract(20, 20, 20), blockling.getPositionVector().addVector(20, 20, 20)));
        if (!entities.isEmpty())
        {
            for (Entity entity : entities)
            {
                if (!(entity instanceof EntityPlayer) && blockling.getWhitelist(Task.HUNT.whitelistId).isWhitelisted(entity))
                {
                    blockling.setAttackTarget((EntityLivingBase) entity);
                }
            }
        }

        return blockling.getAttackTarget() != null;
    }
}
