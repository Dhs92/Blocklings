package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class BlocklingAITank extends BlocklingAITarget
{
    public BlocklingAITank(EntityBlockling blockling)
    {
        super(blockling);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (!blockling.isTaskActive(Task.TANK)) return false;
        if (blockling.getAttackTarget() != null) return false;

        EntityLivingBase owner = blockling.getOwner();
        if (owner != null)
        {
            EntityLivingBase target = owner.getRevengeTarget();
            if (target != null && !(target instanceof EntityPlayer) && blockling.getWhitelist(Task.TANK.whitelistId).isWhitelisted(target))
            {
                EntityCreature targeto = (EntityCreature) target;
                targeto.setAttackTarget(blockling);
            }
        }

        return false;
    }
}
