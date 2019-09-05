package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;

public class BlocklingFarmCropsNearbyGoal extends Goal
{
    private BlocklingEntity blockling;

    public BlocklingFarmCropsNearbyGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public void startExecuting()
    {

    }

    @Override
    public void resetTask()
    {

    }

    @Override
    public boolean shouldExecute()
    {
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return true;
    }

    @Override
    public void tick()
    {

    }
}
