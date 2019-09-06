package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;

public class BlocklingAttackMeleeGoal extends MeleeAttackGoal
{
    private BlocklingEntity blockling;

    public BlocklingAttackMeleeGoal(BlocklingEntity blockling)
    {
        super(blockling, 1.0, true);
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
        if (!blockling.aiManager.isActive(AIManager.ATTACK_MELEE_ID)) return false;

        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting();
    }

    @Override
    public void tick()
    {
        super.tick();
    }
}
