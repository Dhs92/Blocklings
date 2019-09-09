package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;

public class BlocklingOwnerAttackGoal extends Goal
{
    private BlocklingEntity blockling;

    public BlocklingOwnerAttackGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public void resetTask()
    {

    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.OWNER_HURT_ID)) return false;

        LivingEntity entity = blockling.getOwner().getLastAttackedEntity();
        if (entity != null)
        {
            if (blockling.aiManager.getWhitelist(AIManager.OWNER_HURT_WHITELIST_ID).isInWhitelist(entity))
            {
                blockling.setAttackTarget(entity);
            }
        }

        return blockling.getAttackTarget() != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return blockling.getAttackTarget() != null;
    }

    @Override
    public void tick()
    {

    }
}
