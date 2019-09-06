package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;

public class BlocklingAttackedGoal extends Goal
{
    private BlocklingEntity blockling;

    public BlocklingAttackedGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public void resetTask()
    {
        blockling.setAttackTarget(null);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.HURT_BY_ID)) return false;

        LivingEntity entity = blockling.getAttackingEntity();
        if (entity != null)
        {
            if (blockling.aiManager.getWhitelist(AIManager.HURT_BY_WHITELIST_ID).isInWhitelist(entity))
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
