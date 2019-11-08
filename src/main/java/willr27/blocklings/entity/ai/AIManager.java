package willr27.blocklings.entity.ai;

import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.config.BlocklingsConfig;
import willr27.blocklings.entity.EntityUtil;
import willr27.blocklings.entity.ai.goals.*;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.whitelist.BlocklingWhitelist;
import willr27.blocklings.whitelist.WhitelistType;

import java.util.*;
import java.util.stream.Collectors;

public class AIManager
{
    private static int id = 0;
    public static final int FOLLOW_ID = id++;
    public static final int WANDER_ID = id++;
    public static final int ATTACK_MELEE_ID = id++;
    public static final int HURT_BY_ID = id++;
    public static final int OWNER_HURT_BY_ID = id++;
    public static final int OWNER_HURT_ID = id++;
    public static final int MINE_NEARBY_ID = id++;
    public static final int CHOP_NEARBY_ID = id++;
    public static final int FARM_NEARBY_ID = id++;

    public static final int AUTOMSELT_ID = id++;
    public static final int PLACE_TORCHES_ID = id++;
    public static final int BONEMEAL_SAPLINGS_ID = id++;
    public static final int BONEMEAL_CROPS_ID = id++;

    static { id = 0; }
    public static final int HURT_BY_WHITELIST_ID = id++;
    public static final int OWNER_HURT_BY_WHITELIST_ID = id++;
    public static final int OWNER_HURT_WHITELIST_ID = id++;
    public static final int MINE_NEARBY_ORES_WHITELIST_ID = id++;
    public static final int CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID = id++;
    public static final int CHOP_NEARBY_LOGS_SAPLINGS_WHITELIST_ID = id++;
    public static final int BONEMEAL_SAPLINGS_WHITELIST_ID = id++;
    public static final int FARM_NEARBY_CROPS_CROPS_WHITELIST_ID = id++;
    public static final int FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID = id++;
    public static final int BONEMEAL_CROPS_WHITELIST_ID = id++;

    public static final int AUTOMSELT_ORES_WHITELIST_ID = id++;

    public static final Map<Integer, int[]> GOALS_TO_WHITELISTS = new LinkedHashMap<>();
    static
    {
        GOALS_TO_WHITELISTS.put(HURT_BY_ID, new int[] {HURT_BY_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(OWNER_HURT_BY_ID, new int[] {OWNER_HURT_BY_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(OWNER_HURT_ID, new int[] {OWNER_HURT_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(MINE_NEARBY_ID, new int[] {MINE_NEARBY_ORES_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(CHOP_NEARBY_ID, new int[] {CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID, CHOP_NEARBY_LOGS_SAPLINGS_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(FARM_NEARBY_ID, new int[] {FARM_NEARBY_CROPS_CROPS_WHITELIST_ID, FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID});

        GOALS_TO_WHITELISTS.put(BONEMEAL_SAPLINGS_ID, new int[] {BONEMEAL_SAPLINGS_WHITELIST_ID});
        GOALS_TO_WHITELISTS.put(BONEMEAL_CROPS_ID, new int[] {BONEMEAL_CROPS_WHITELIST_ID});

        GOALS_TO_WHITELISTS.put(AUTOMSELT_ID, new int[] {AUTOMSELT_ORES_WHITELIST_ID});
    }

    public BlocklingWhitelist mineNearbyWhitelist;
    public BlocklingWhitelist chopNearbyLogsWhitelist;
    public BlocklingWhitelist chopNearbySaplingsWhitelist;
    public BlocklingWhitelist farmNearbyCropsWhitelist;
    public BlocklingWhitelist farmNearbySeedsWhitelist;

    public BlocklingWhitelist bonemealSaplingsWhitelist;
    public BlocklingWhitelist bonemealCropsWhitelist;

    public BlocklingWhitelist autosmeltOresWhitelist;

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

        blockling.goalSelector.addGoal(i++ , new SwimGoal(blockling));

        goals.add(new GoalInfo(this, ATTACK_MELEE_ID, "Melee Attack", "Your blockling will use melee attacks to attack its target.", new BlocklingAttackMeleeGoal(blockling), true, false, i++, 4, 0));
        goals.add(new GoalInfo(this, HURT_BY_ID, "Retaliate", "If something attacks your blockling it will attack back.", new BlocklingAttackedGoal(blockling), true, false, i++, 3, 0));
        goals.add(new GoalInfo(this, OWNER_HURT_BY_ID, "Attack Owner's Attacker", "If something attacks you your blockling will attack it back.", new BlocklingOwnerAttackedGoal(blockling), true, false, i++, 3, 0));
        goals.add(new GoalInfo(this, OWNER_HURT_ID, "Attack Owner's Target", "If you attack something your blockling will attack it too.", new BlocklingOwnerAttackGoal(blockling), true, false, i++, 3, 0));
        goals.add(new GoalInfo(this, MINE_NEARBY_ID, "Mine Nearby Ores", "Your blockling will mine nearby ores using a pickaxe.", new BlocklingMineNearbyGoal(blockling), false, false, i++, 6, 0));
        goals.add(new GoalInfo(this, CHOP_NEARBY_ID, "Chop Nearby Trees", "Your blockling will chop nearby tress using an axe.", new BlocklingChopNearbyGoal(blockling), false, false, i++, 7, 0));
        goals.add(new GoalInfo(this, FARM_NEARBY_ID, "Farm Nearby Crops", "Your blockling will harvest and replant nearby crops using a hoe.", new BlocklingFarmCropsNearbyGoal(blockling), false, false, i++, 8, 0));
        goals.add(new GoalInfo(this, FOLLOW_ID, "Follow", "Your blockling will follow your around.", new BlocklingFollowOwnerGoal(blockling), true, false, i++, 1, 0));
        goals.add(new GoalInfo(this, WANDER_ID, "Wander", "Your blockling is free to wander wherever they want.", new BlocklingWanderGoal(blockling), true, false, i++, 2, 0));

        goals.add(new GoalInfo(this, AUTOMSELT_ID, "Autosmelt", "Your blockling will try to smelt ores in their inventory if they have a furnace utility.", new BlocklingAutosmeltGoal(blockling), false, false, i++, 6, 0));
        goals.add(new GoalInfo(this, PLACE_TORCHES_ID, "Place Torches", "Your blockling will place torches in low light levels.", new BlocklingPlaceTorchesGoal(blockling), false, false, i++, 2, 0));
        goals.add(new GoalInfo(this, BONEMEAL_SAPLINGS_ID, "Fertilise Saplings", "Your blockling will fertilise nearby saplings with bonemeal.", new BlocklingBonemealSaplingsGoal(blockling), false, false, i++, 2, 0));
        goals.add(new GoalInfo(this, BONEMEAL_CROPS_ID, "Fertilise Crops", "Your blockling will fertilise nearby crops with bonemeal.", new BlocklingBonemealCropsGoal(blockling), false, false, i++, 2, 0));

        addWhitelists();
        reapplyGoals();
    }

    private void addWhitelists()
    {
        EntityUtil.init(blockling.world);

        BlocklingWhitelist hurtByWhitelist = new BlocklingWhitelist(HURT_BY_WHITELIST_ID, "Mobs", blockling, WhitelistType.ENTITY);
        BlocklingsConfig.getEntities().stream().forEach(s -> hurtByWhitelist.put(new ResourceLocation(s), true));
        getGoalFromId(HURT_BY_ID).addWhitelist(HURT_BY_WHITELIST_ID, hurtByWhitelist);

        BlocklingWhitelist ownerHurtByWhitelist = new BlocklingWhitelist(OWNER_HURT_BY_WHITELIST_ID, "Mobs", blockling, WhitelistType.ENTITY);
        BlocklingsConfig.getEntities().stream().forEach(s -> ownerHurtByWhitelist.put(new ResourceLocation(s), true));
        getGoalFromId(OWNER_HURT_BY_ID).addWhitelist(OWNER_HURT_BY_WHITELIST_ID, ownerHurtByWhitelist);

        BlocklingWhitelist ownerHurtWhitelist = new BlocklingWhitelist(OWNER_HURT_WHITELIST_ID, "Mobs", blockling, WhitelistType.ENTITY);
        BlocklingsConfig.getEntities().stream().forEach(s -> ownerHurtWhitelist.put(new ResourceLocation(s), true));
        getGoalFromId(OWNER_HURT_ID).addWhitelist(OWNER_HURT_WHITELIST_ID, ownerHurtWhitelist);

        mineNearbyWhitelist = new BlocklingWhitelist(MINE_NEARBY_ORES_WHITELIST_ID, "Ores", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getOres().stream().forEach(s -> mineNearbyWhitelist.put(new ResourceLocation(s), true));

        chopNearbyLogsWhitelist = new BlocklingWhitelist(CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID, "Logs", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getLogsSaplings().keySet().stream().forEach(s -> chopNearbyLogsWhitelist.put(new ResourceLocation(s), true));

        chopNearbySaplingsWhitelist = new BlocklingWhitelist(CHOP_NEARBY_LOGS_SAPLINGS_WHITELIST_ID, "Saplings", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getLogsSaplings().values().stream().forEach(s -> chopNearbySaplingsWhitelist.put(new ResourceLocation(s), true));

        bonemealSaplingsWhitelist = new BlocklingWhitelist(BONEMEAL_SAPLINGS_WHITELIST_ID, "Saplings", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getLogsSaplings().values().stream().forEach(s -> bonemealSaplingsWhitelist.put(new ResourceLocation(s), true));

        farmNearbyCropsWhitelist = new BlocklingWhitelist(FARM_NEARBY_CROPS_CROPS_WHITELIST_ID, "Crops", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getCropsSeeds().keySet().stream().forEach(s -> farmNearbyCropsWhitelist.put(new ResourceLocation(s), true));
        getGoalFromId(FARM_NEARBY_ID).addWhitelist(FARM_NEARBY_CROPS_CROPS_WHITELIST_ID, farmNearbyCropsWhitelist);

        farmNearbySeedsWhitelist = new BlocklingWhitelist(FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID, "Seeds", blockling, WhitelistType.ITEM);
        BlocklingsConfig.getCropsSeeds().values().stream().forEach(s -> farmNearbySeedsWhitelist.put(new ResourceLocation(s), true));
        getGoalFromId(FARM_NEARBY_ID).addWhitelist(FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID, farmNearbySeedsWhitelist);

        bonemealCropsWhitelist = new BlocklingWhitelist(BONEMEAL_CROPS_WHITELIST_ID, "Crops", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getCropsSeeds().keySet().stream().forEach(s -> bonemealCropsWhitelist.put(new ResourceLocation(s), true));

        autosmeltOresWhitelist = new BlocklingWhitelist(AUTOMSELT_ORES_WHITELIST_ID, "Ores", blockling, WhitelistType.BLOCK);
        BlocklingsConfig.getOres().stream().forEach(s -> autosmeltOresWhitelist.put(new ResourceLocation(s), true));
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
