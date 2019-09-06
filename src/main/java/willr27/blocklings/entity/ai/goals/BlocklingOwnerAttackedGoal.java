package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;

public class BlocklingOwnerAttackedGoal extends Goal
{
    private BlocklingEntity blockling;

    public BlocklingOwnerAttackedGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.OWNER_HURT_BY_ID)) return false;

        LivingEntity entity = blockling.getOwner().getAttackingEntity();
        if (entity != null)
        {
            blockling.setAttackTarget(entity);
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
