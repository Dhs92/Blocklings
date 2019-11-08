package willr27.blocklings.abilities;

import javafx.util.Pair;
import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.ai.goals.BlocklingPlaceTorchesGoal;
import willr27.blocklings.entity.blockling.BlocklingAttribute;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.entity.blockling.BlocklingStats;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.AbilityBoughtMessage;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class AbilityManager
{
    private Map<String, AbilityGroup> groups = new HashMap<>();
    private BlocklingEntity blockling;

    public AbilityManager(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        reset();
    }

    public void reset()
    {
        AbilityGroup general = new AbilityGroup(blockling, AbilityGroup.GENERAL, "General", GuiUtil.GENERAL_BACKGROUND, GuiUtil.GENERAL_ICONS);

        AbilityGroup combat = new AbilityGroup(blockling, AbilityGroup.COMBAT, "Combat", GuiUtil.COMBAT_BACKGROUND, GuiUtil.COMBAT_ICONS);

        AbilityGroup mining = new AbilityGroup(blockling, AbilityGroup.MINING, "Mining", GuiUtil.MINING_BACKGROUND, GuiUtil.MINING_ICONS);
        mining.addAllAbilities(Abilities.Mining.ABILITIES);

        AbilityGroup woodcutting = new AbilityGroup(blockling, AbilityGroup.WOODCUTTING, "Woodcutting", GuiUtil.WOODCUTTING_BACKGROUND, GuiUtil.WOODCUTTING_ICONS);
        woodcutting.addAllAbilities(Abilities.Woodcutting.ABILITIES);

        AbilityGroup farming = new AbilityGroup(blockling, AbilityGroup.FARMING, "Farming", GuiUtil.FARMING_BACKGROUND, GuiUtil.FARMING_ICONS);
        farming.addAllAbilities(Abilities.Farming.ABILITIES);

        groups.put(general.id, general);
        groups.put(combat.id, combat);
        groups.put(mining.id, mining);
        groups.put(woodcutting.id, woodcutting);
        groups.put(farming.id, farming);
    }

    public void stateChanged(AbilityGroup group, Ability ability, AbilityState newState)
    {
        AbilityState currentState = group.getState(ability);

        BlocklingStats stats = blockling.getStats();

        if (newState == AbilityState.BOUGHT)
        {
            if (ability == Abilities.Mining.NOVICE_MINER)
            {
                blockling.aiManager.getGoalFromId(AIManager.MINE_NEARBY_ID).setUnlocked(true);
            }
            else if (ability == Abilities.Mining.MINING_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.MINE_NEARBY_ID).addWhitelist(AIManager.MINE_NEARBY_ORES_WHITELIST_ID, blockling.aiManager.mineNearbyWhitelist);
            }
            else if (ability == Abilities.Mining.FASTER_MINING)
            {
                if (!blockling.world.isRemote) stats.miningInterval.addModifier(stats.miningIntervalFasterMiningAbilityModifier);
            }
            else if (ability == Abilities.Mining.FASTER_MINING_FOR_DURABILITY || ability == Abilities.Mining.FASTER_MINING_FOR_HEALTH || ability == Abilities.Mining.FASTER_MINING_FOR_ORES || ability == Abilities.Mining.FASTER_MINING_IN_DARK)
            {
                if (!blockling.world.isRemote)
                {
                    stats.miningInterval.addModifier(stats.miningIntervalFasterMiningEnhancedAbilityModifier);

                    if (ability == Abilities.Mining.FASTER_MINING_FOR_DURABILITY)
                    {
                        stats.miningIntervalFasterMiningEnhancedAbilityModifier.setValue(0.75f);
                    }
                    else if (ability == Abilities.Mining.FASTER_MINING_FOR_HEALTH)
                    {
                        blockling.setHealth(blockling.getHealth());
                    }
                    else if (ability == Abilities.Mining.FASTER_MINING_IN_DARK)
                    {
                        stats.miningIntervalFasterMiningEnhancedAbilityModifier.setValue(((blockling.world.getLight(blockling.getPosition()) / 15.0f) / 2.0f) + 0.5f);
                    }
                }
            }
            else if (ability == Abilities.Mining.AUTOSMELT)
            {
                blockling.aiManager.getGoalFromId(AIManager.AUTOMSELT_ID).setUnlocked(true);
            }
            else if (ability == Abilities.Mining.AUTOSMELT_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.AUTOMSELT_ID).addWhitelist(AIManager.AUTOMSELT_ORES_WHITELIST_ID, blockling.aiManager.autosmeltOresWhitelist);
            }
            else if (ability == Abilities.Mining.TORCH_PLACER)
            {
                blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).setUnlocked(true);
            }
            else if (ability == Abilities.Mining.TORCH_PLACER_IN_LIGHTER_AREAS)
            {
                ((BlocklingPlaceTorchesGoal)blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).goal).delay = 1;
                ((BlocklingPlaceTorchesGoal)blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).goal).lightLevel = 8;
                ((BlocklingPlaceTorchesGoal)blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).goal).setMutexFlags(EnumSet.noneOf(Goal.Flag.class));
            }


            else if (ability == Abilities.Woodcutting.NOVICE_LUMBERJACK)
            {
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).setUnlocked(true);
            }
            else if (ability == Abilities.Woodcutting.WOODCUTTING_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).addWhitelist(AIManager.CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID, blockling.aiManager.chopNearbyLogsWhitelist);
            }
            else if (ability == Abilities.Woodcutting.FASTER_CHOPPING)
            {
                if (!blockling.world.isRemote) stats.woodcuttingInterval.addModifier(stats.woodcuttingIntervalFasterChoppingAbilityModifier);
            }
            else if (ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_DURABILITY || ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_HEALTH || ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_LOGS || ability == Abilities.Woodcutting.FASTER_CHOPPING_IN_DARK)
            {
                if (!blockling.world.isRemote)
                {
                    stats.woodcuttingInterval.addModifier(stats.woodcuttingIntervalFasterChoppingEnhancedAbilityModifier);

                    if (ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_DURABILITY)
                    {
                        stats.woodcuttingIntervalFasterChoppingEnhancedAbilityModifier.setValue(0.75f);
                    }
                    else if (ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_HEALTH)
                    {
                        blockling.setHealth(blockling.getHealth());
                    }
                    else if (ability == Abilities.Woodcutting.FASTER_CHOPPING_IN_DARK)
                    {
                        stats.woodcuttingIntervalFasterChoppingEnhancedAbilityModifier.setValue(((blockling.world.getLight(blockling.getPosition()) / 15.0f) / 2.0f) + 0.5f);
                    }
                }
            }
            else if (ability == Abilities.Woodcutting.BONEMEAL_NEARBY)
            {
                blockling.aiManager.getGoalFromId(AIManager.BONEMEAL_SAPLINGS_ID).setUnlocked(true);
            }
            else if (ability == Abilities.Woodcutting.REPLANT_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).addWhitelist(AIManager.CHOP_NEARBY_LOGS_SAPLINGS_WHITELIST_ID, blockling.aiManager.chopNearbySaplingsWhitelist);
            }


            else if (ability == Abilities.Farming.NOVICE_FARMER)
            {
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).setUnlocked(true);
            }
            else if (ability == Abilities.Farming.FARMING_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).addWhitelist(AIManager.FARM_NEARBY_CROPS_CROPS_WHITELIST_ID, blockling.aiManager.farmNearbyCropsWhitelist);
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).addWhitelist(AIManager.FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID, blockling.aiManager.farmNearbySeedsWhitelist);
            }
            else if (ability == Abilities.Farming.FASTER_FARMING)
            {
                if (!blockling.world.isRemote) stats.farmingInterval.addModifier(stats.farmingIntervalFasterFarmingAbilityModifier);
            }
            else if (ability == Abilities.Farming.FASTER_FARMING_FOR_DURABILITY || ability == Abilities.Farming.FASTER_FARMING_FOR_HEALTH || ability == Abilities.Farming.FASTER_FARMING_FOR_CROPS || ability == Abilities.Farming.FASTER_FARMING_IN_DARK)
            {
                if (!blockling.world.isRemote)
                {
                    stats.farmingInterval.addModifier(stats.farmingIntervalFasterFarmingEnhancedAbilityModifier);

                    if (ability == Abilities.Farming.FASTER_FARMING_FOR_DURABILITY)
                    {
                        stats.farmingIntervalFasterFarmingEnhancedAbilityModifier.setValue(0.75f);
                    }
                    else if (ability == Abilities.Farming.FASTER_FARMING_FOR_HEALTH)
                    {
                        blockling.setHealth(blockling.getHealth());
                    }
                    else if (ability == Abilities.Farming.FASTER_FARMING_IN_DARK)
                    {
                        stats.farmingIntervalFasterFarmingEnhancedAbilityModifier.setValue(((blockling.world.getLight(blockling.getPosition()) / 15.0f) / 2.0f) + 0.5f);
                    }
                }
            }
            else if (ability == Abilities.Farming.BONEMEAL_NEARBY)
            {
                blockling.aiManager.getGoalFromId(AIManager.BONEMEAL_CROPS_ID).setUnlocked(true);
            }
        }



        else
        {
            if (ability == Abilities.Mining.NOVICE_MINER)
            {
                blockling.aiManager.getGoalFromId(AIManager.MINE_NEARBY_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.MINE_NEARBY_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Mining.MINING_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.MINE_NEARBY_ID).removeWhitelist(AIManager.MINE_NEARBY_ORES_WHITELIST_ID);
            }
            else if (ability == Abilities.Mining.FASTER_MINING)
            {
                if (!blockling.world.isRemote) stats.miningInterval.removeModifier(stats.miningIntervalFasterMiningAbilityModifier);
            }
            else if (ability == Abilities.Mining.FASTER_MINING_FOR_DURABILITY || ability == Abilities.Mining.FASTER_MINING_FOR_HEALTH || ability == Abilities.Mining.FASTER_MINING_FOR_ORES || ability == Abilities.Mining.FASTER_MINING_IN_DARK)
            {
                if (!blockling.world.isRemote) stats.miningInterval.removeModifier(stats.miningIntervalFasterMiningEnhancedAbilityModifier);
            }
            else if (ability == Abilities.Mining.AUTOSMELT)
            {
                blockling.aiManager.getGoalFromId(AIManager.AUTOMSELT_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.AUTOMSELT_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Mining.AUTOSMELT_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.AUTOMSELT_ID).removeWhitelist(AIManager.AUTOMSELT_ORES_WHITELIST_ID);
            }
            else if (ability == Abilities.Mining.TORCH_PLACER)
            {
                blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Mining.TORCH_PLACER_IN_LIGHTER_AREAS)
            {
                ((BlocklingPlaceTorchesGoal)blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).goal).delay = 30;
                ((BlocklingPlaceTorchesGoal)blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).goal).lightLevel = 1;
                ((BlocklingPlaceTorchesGoal)blockling.aiManager.getGoalFromId(AIManager.PLACE_TORCHES_ID).goal).setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            }


            if (ability == Abilities.Woodcutting.NOVICE_LUMBERJACK)
            {
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Woodcutting.WOODCUTTING_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).removeWhitelist(AIManager.CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID);
            }
            else if (ability == Abilities.Woodcutting.FASTER_CHOPPING)
            {
                if (!blockling.world.isRemote) stats.woodcuttingInterval.removeModifier(stats.woodcuttingIntervalFasterChoppingAbilityModifier);
            }
            else if (ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_DURABILITY || ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_HEALTH || ability == Abilities.Woodcutting.FASTER_CHOPPING_FOR_LOGS || ability == Abilities.Woodcutting.FASTER_CHOPPING_IN_DARK)
            {
                if (!blockling.world.isRemote) stats.woodcuttingInterval.removeModifier(stats.woodcuttingIntervalFasterChoppingEnhancedAbilityModifier);
            }
            else if (ability == Abilities.Woodcutting.BONEMEAL_NEARBY)
            {
                blockling.aiManager.getGoalFromId(AIManager.BONEMEAL_SAPLINGS_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.BONEMEAL_SAPLINGS_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Woodcutting.REPLANT_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.CHOP_NEARBY_ID).removeWhitelist(AIManager.CHOP_NEARBY_LOGS_SAPLINGS_WHITELIST_ID);
            }


            if (ability == Abilities.Farming.NOVICE_FARMER)
            {
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Farming.FARMING_WHITELIST)
            {
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).removeWhitelist(AIManager.FARM_NEARBY_CROPS_CROPS_WHITELIST_ID);
                blockling.aiManager.getGoalFromId(AIManager.FARM_NEARBY_ID).removeWhitelist(AIManager.FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID);
            }
            else if (ability == Abilities.Farming.FASTER_FARMING)
            {
                if (!blockling.world.isRemote) stats.farmingInterval.removeModifier(stats.farmingIntervalFasterFarmingAbilityModifier);
            }
            else if (ability == Abilities.Farming.FASTER_FARMING_FOR_DURABILITY || ability == Abilities.Farming.FASTER_FARMING_FOR_HEALTH || ability == Abilities.Farming.FASTER_FARMING_FOR_CROPS || ability == Abilities.Farming.FASTER_FARMING_IN_DARK)
            {
                if (!blockling.world.isRemote) stats.farmingInterval.removeModifier(stats.farmingIntervalFasterFarmingEnhancedAbilityModifier);
            }
            else if (ability == Abilities.Farming.BONEMEAL_NEARBY)
            {
                blockling.aiManager.getGoalFromId(AIManager.BONEMEAL_CROPS_ID).setActive(false, false);
                blockling.aiManager.getGoalFromId(AIManager.BONEMEAL_CROPS_ID).setUnlocked(false);
            }
        }
    }

    public boolean canBuyAbility(AbilityGroup group, Ability ability)
    {
        int pointsNeeded = ability.getSkillPointsRequired();
        if (blockling.getStats().skillPoints.getInt() < pointsNeeded && group.allParentsBought(ability) && !group.hasConflict(ability))
        {
            return false;
        }

        for (Pair<String, Float> levelRequirement : ability.getLevelRequirements())
        {
            BlocklingAttribute attribute = blockling.getStats().getAttribute(levelRequirement.getKey());
            if (attribute.getFloat() < levelRequirement.getValue())
            {
                return false;
            }
        }

        if (group.hasConflict(ability))
        {
            return false;
        }

        return true;
    }

    public void tryBuyAbility(AbilityGroup group, Ability ability)
    {
        if (blockling.world.isRemote)
        {
            NetworkHandler.sendToServer(new AbilityBoughtMessage(ability, group, blockling.getEntityId()));
            return;
        }

        if (!canBuyAbility(group, ability))
        {
            return;
        }

        blockling.getStats().skillPoints.incBaseValue(-ability.getSkillPointsRequired());
        group.setState(ability, AbilityState.BOUGHT);
    }

    public AbilityGroup getGroup(String id)
    {
        return groups.get(id);
    }

    public AbilityState getState(Ability ability)
    {
        for (AbilityGroup group : groups.values())
        {
            if (group.getAbilities().contains(ability))
            {
                return group.getState(ability);
            }
        }

        return AbilityState.LOCKED;
    }

    public boolean isBought(Ability ability)
    {
     return getState(ability) == AbilityState.BOUGHT;
    }
}
