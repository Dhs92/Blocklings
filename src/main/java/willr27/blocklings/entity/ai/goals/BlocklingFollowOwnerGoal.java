package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.entity.ai.AIManager;

import java.util.EnumSet;

public class BlocklingFollowOwnerGoal extends FollowOwnerGoal
{
    private BlocklingEntity blockling;

    public BlocklingFollowOwnerGoal(BlocklingEntity blockling)
    {
        super(blockling, 1.0, 2, 5);
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.aiManager.isActive(AIManager.SIT_ID)) return false;

        return super.shouldExecute();
    }
}
