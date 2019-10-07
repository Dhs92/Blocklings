package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import willr27.blocklings.entity.blockling.BlocklingEntity;

public class BlocklingFollowOwnerGoal extends FollowOwnerGoal
{
    private BlocklingEntity blockling;

    public BlocklingFollowOwnerGoal(BlocklingEntity blockling)
    {
        super(blockling, 1.0, 2.5f, 6.0f);
        this.blockling = blockling;
    }

    @Override
    public void resetTask()
    {
    }

    @Override
    public boolean shouldExecute()
    {
        return super.shouldExecute();
    }
}
