package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.item.ToolType;

import java.util.EnumSet;

public class BlocklingAttackMeleeGoal extends Goal
{
    public static final ToolType TOOL_TYPE = ToolType.WEAPON;

    private BlocklingEntity blockling;

    public BlocklingAttackMeleeGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public void resetTask()
    {
        blockling.setAttackTarget(null);
        blockling.stopAction();
    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.ATTACK_MELEE_ID)) return false;

        return blockling.getAttackTarget() != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting();
    }

    @Override
    public void tick()
    {
        blockling.switchToToolType(TOOL_TYPE);

        LivingEntity target = blockling.getAttackTarget();
        blockling.getNavigator().tryMoveToEntityLiving(target, 1.0);

        if (blockling.getDistanceSq(target.posX, target.getBoundingBox().minY, target.posZ) < 4.0f)
        {
            if (blockling.hasFinishedAction())
            {
                blockling.attackEntityAsMob(target);
            }

            if (!blockling.isPerformingAction())
            {
                blockling.startAction((int) blockling.getStats().combatInterval.getFloat());
            }
        }
    }
}
