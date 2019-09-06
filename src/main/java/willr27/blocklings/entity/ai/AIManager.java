package willr27.blocklings.entity.ai;

import net.minecraft.util.ResourceLocation;
import willr27.blocklings.config.BlocklingsConfig;
import willr27.blocklings.entity.ai.goals.*;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.whitelist.BlocklingWhitelist;
import willr27.blocklings.whitelist.WhitelistType;

import java.util.*;
import java.util.stream.Collectors;

public class AIManager
{
    public static final int SIT_ID = 0;
    public static final int FOLLOW_ID = 1;
    public static final int WANDER_ID = 2;
    public static final int MINE_NEARBY_ID = 3;
    public static final int CHOP_NEARBY_ID = 4;
    public static final int FARM_NEARBY_ID = 5;

    public static final int MINE_NEARBY_ORES_WHITELIST_ID = 0;
    public static final int CHOP_NEARBY_LOGS_WHITELIST_ID = 1;

    public static final Map<Integer, int[]> GOALS_TO_WHITELISTS = new LinkedHashMap<>();
    static
    {
        GOALS_TO_WHITELISTS.put(MINE_NEARBY_ID, new int[] {MINE_NEARBY_ORES_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(CHOP_NEARBY_ID, new int[] {CHOP_NEARBY_LOGS_WHITELIST_ID});
    }

    public BlocklingEntity blockling;

    private List<GoalInfo> goals = new ArrayList<>();

    public AIManager(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        reset();
    }

    public void reset()
    {
        int i = 0;
        goals.add(new GoalInfo(this, MINE_NEARBY_ID, "Mine Nearby Ores", new BlocklingMineNearbyGoal(blockling), i++, 6, 0));
        goals.add(new GoalInfo(this, CHOP_NEARBY_ID, "Chop Nearby Trees", new BlocklingChopNearbyGoal(blockling), i++, 7, 0));
        goals.add(new GoalInfo(this, FOLLOW_ID, "Follow", new BlocklingFollowOwnerGoal(blockling), i++, 1, 0));
        goals.add(new GoalInfo(this, SIT_ID, "Sit", new BlocklingSitGoal(blockling), i++, 0, 0));
        goals.add(new GoalInfo(this, WANDER_ID, "Wander", new BlocklingWanderGoal(blockling), i++, 2, 0));

        addWhitelists();
        reapplyGoals();
    }

    private void addWhitelists()
    {
        BlocklingWhitelist mineNearbyWhitelist = new BlocklingWhitelist(MINE_NEARBY_ORES_WHITELIST_ID, blockling, WhitelistType.BLOCK);
        BlocklingsConfig.ORES.get().stream().forEach(s -> mineNearbyWhitelist.put(new ResourceLocation(s), new Random().nextInt(2) == 0));
        getGoalFromId(MINE_NEARBY_ID).addWhitelist(MINE_NEARBY_ORES_WHITELIST_ID, mineNearbyWhitelist);

        BlocklingWhitelist chopNearbyWhitelist = new BlocklingWhitelist(CHOP_NEARBY_LOGS_WHITELIST_ID, blockling, WhitelistType.BLOCK);
        BlocklingsConfig.LOGS.get().stream().forEach(s -> chopNearbyWhitelist.put(new ResourceLocation(s), new Random().nextInt(2) == 0));
        getGoalFromId(CHOP_NEARBY_ID).addWhitelist(CHOP_NEARBY_LOGS_WHITELIST_ID, chopNearbyWhitelist);
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

    public BlocklingWhitelist getWhitelist(int goalId, int whitelistId)
    {
        return getGoalFromId(goalId).getWhitelist(whitelistId);
    }
    public BlocklingWhitelist getWhitelist(int whitelistId)
    {
        for (GoalInfo goal : goals)
        {
            BlocklingWhitelist whitelist = goal.getWhitelist(whitelistId);
            if (whitelist != null) return whitelist;
        }
        return null;
    }

    public static int[] getWhitelistIdsForGoal(int goalId)
    {
        return GOALS_TO_WHITELISTS.get(goalId);
    }
}
