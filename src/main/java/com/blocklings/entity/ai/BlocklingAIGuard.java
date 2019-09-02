package com.blocklings.entity.ai;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class BlocklingAIGuard extends BlocklingAITarget
{
    public BlocklingAIGuard(EntityBlockling blockling)
    {
        super(blockling);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute()) return false;

        if (!blockling.isTaskActive(Task.GUARD)) return false;
        if (blockling.getAttackTarget() != null) return false;

        EntityLivingBase owner = blockling.getOwner();
        if (owner != null)
        {
            EntityLivingBase target = owner.getRevengeTarget();
            if (target != null && !(target instanceof EntityPlayer) && blockling.getWhitelist(Task.GUARD.whitelistId).isWhitelisted(target))
            {
                blockling.setAttackTarget(target);
            }
            else
            {
                target = owner.getLastAttackedEntity();
                if (target != null && !(target instanceof EntityPlayer) && blockling.getWhitelist(Task.GUARD.whitelistId).isWhitelisted(target))
                {
                    blockling.setAttackTarget(target);
                }
            }
        }

        return blockling.getAttackTarget() != null;
    }
}
