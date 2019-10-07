package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.EnumSet;

public class BlocklingWanderGoal extends WaterAvoidingRandomWalkingGoal
{
    private BlocklingEntity blockling;

    public BlocklingWanderGoal(BlocklingEntity blockling)
    {
        super(blockling, 1.0);
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public boolean shouldExecute()
    {
        return super.shouldExecute();
    }
}
