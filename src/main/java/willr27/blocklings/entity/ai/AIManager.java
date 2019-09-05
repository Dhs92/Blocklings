package willr27.blocklings.entity.ai;

import net.minecraft.util.ResourceLocation;
import willr27.blocklings.config.BlocklingsConfig;
import willr27.blocklings.entity.ai.goals.*;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.whitelist.BlocklingWhitelist;
import willr27.blocklings.whitelist.WhitelistType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AIManager
{
    public static final int SIT_ID = 0;
    public static final int FOLLOW_ID = 1;
    public static final int WANDER_ID = 2;
    public static final int MINE_NEARBY_ID = 3;

    public BlocklingEntity blockling;

    private List<GoalInfo> goals = new ArrayList<>();

    public AIManager(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        reset();
    }

    public void reset()
    {
        goals.add(new GoalInfo(this, SIT_ID, "Sit", new BlocklingSitGoal(blockling), 0, 0, 0));
        goals.add(new GoalInfo(this, FOLLOW_ID, "Follow", new BlocklingFollowOwnerGoal(blockling), 1, 1, 0));
        goals.add(new GoalInfo(this, WANDER_ID, "Wander", new BlocklingWanderGoal(blockling), 2, 2, 0));
        goals.add(new GoalInfo(this, MINE_NEARBY_ID, "Mine Nearby Ores", new BlocklingMineNearbyGoal(blockling), 3, 6, 0));

        addWhitelists();
        reapplyGoals();
    }

    public void updateGoalsOrder(GoalInfo updatedGoal, int oldPriority, int newPriority)
    {
        if (Math.abs(newPriority - oldPriority) == 1)
        {
            getGoalFromPriority(newPriority).setPriority(oldPriority, false, false);
        }
        else if (newPriority < oldPriority)
        {
            for (GoalInfo goal : goals)
            {
                int priority = goal.getPriority();
                if (priority >= newPriority && priority < oldPriority && goal != updatedGoal)
                {
                    goal.setPriority(priority + 1, false, false);
                }
            }
        }
        else if (newPriority > oldPriority)
        {
            for (GoalInfo goal : goals)
            {
                int priority = goal.getPriority();
                if (priority > oldPriority && priority <= newPriority && goal != updatedGoal)
                {
                    goal.setPriority(priority - 1, false, false);
                }
            }
        }

    }

    public void reorderGoals()
    {
        goals = goals.stream().sorted(Comparator.comparingInt(GoalInfo::getPriority)).collect(Collectors.toList());
    }

    public void reapplyGoals()
    {
        for (GoalInfo goalInfo : goals)
        {
            removeGoal(goalInfo);
            if (goalInfo.isUnlocked() && goalInfo.isActive()) addGoal(goalInfo);
        }
    }

    public List<GoalInfo> getGoals()
    {
        return goals;
    }

    public GoalInfo getGoalFromId(int goalId)
    {
        for (GoalInfo goal : goals)
        {
            if (goal.goalId == goalId)
            {
                return goal;
            }
        }
        return null;
    }

    public GoalInfo getGoalFromPriority(int priority)
    {
        for (GoalInfo goal : goals)
        {
            if (goal.getPriority() == priority)
            {
                return goal;
            }
        }
        return null;
    }

    public boolean isActive(int goalId)
    {
        return getGoalFromId(goalId).isActive();
    }

    private void addGoal(GoalInfo goal)
    {
        blockling.goalSelector.addGoal(goal.getPriority(), goal.goal);
    }

    private void addTargetGoal(GoalInfo goal)
    {
        blockling.targetSelector.addGoal(goal.getPriority(), goal.goal);
    }

    private void removeGoal(GoalInfo goal)
    {
        blockling.goalSelector.removeGoal(goal.goal);
    }

    private void removeTargetGoal(GoalInfo goal)
    {
        blockling.targetSelector.removeGoal(goal.goal);
    }

    private void addWhitelists()
    {
        BlocklingWhitelist mineNearbyWhitelist = new BlocklingWhitelist(blockling, WhitelistType.BLOCK);
        BlocklingsConfig.ORES.get().stream().forEach(s -> mineNearbyWhitelist.put(new ResourceLocation(s), new Random().nextInt(2) == 0));
        getGoalFromId(MINE_NEARBY_ID).addWhitelist(BlocklingMineNearbyGoal.ORE_WHITELIST_ID, mineNearbyWhitelist);
    }

    public BlocklingWhitelist getWhitelist(int goalId, int whitelistId)
    {
        return getGoalFromId(goalId).getWhitelist(whitelistId);
    }
}
