package willr27.blocklings.abilities;

import javafx.util.Pair;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingAttribute;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.entity.blockling.BlocklingStats;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.AbilityBoughtMessage;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager
{
    private Map<Integer, AbilityGroup> groups = new HashMap<>();
    private BlocklingEntity blockling;

    public AbilityManager(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        reset();
    }

    public void reset()
    {
        AbilityGroup general = new AbilityGroup(blockling, AbilityGroup.GENERAL, "General");

        AbilityGroup combat = new AbilityGroup(blockling, AbilityGroup.COMBAT, "Combat");

        AbilityGroup mining = new AbilityGroup(blockling, AbilityGroup.MINING, "Mining");
        mining.addAllAbilities(Abilities.Mining.ABILITIES);

        AbilityGroup woodcutting = new AbilityGroup(blockling, AbilityGroup.WOODCUTTING, "Woodcutting");

        AbilityGroup farming = new AbilityGroup(blockling, AbilityGroup.FARMING, "Farming");

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
            else if (ability == Abilities.Mining.FASTER_MINING)
            {
                if (!blockling.world.isRemote) stats.miningInterval.addModifier(stats.miningIntervalFasterMiningAbilityModifier);
            }
            else if (ability == Abilities.Mining.FASTER_MINING_FOR_DURABILITY || ability == Abilities.Mining.FASTER_MINING_FOR_HEALTH || ability == Abilities.Mining.FASTER_MINING_FOR_ORES || ability == Abilities.Mining.FASTER_MINING_IN_DARK)
            {
                if (!blockling.world.isRemote) stats.miningInterval.addModifier(stats.miningIntervalFasterMiningEnhancedAbilityModifier);
            }
        }
        else
        {
            if (ability == Abilities.Mining.NOVICE_MINER)
            {
                blockling.aiManager.getGoalFromId(AIManager.MINE_NEARBY_ID).setUnlocked(false);
            }
            else if (ability == Abilities.Mining.FASTER_MINING)
            {
                if (!blockling.world.isRemote) stats.miningInterval.removeModifier(stats.miningIntervalFasterMiningAbilityModifier);
            }
            else if (ability == Abilities.Mining.FASTER_MINING_FOR_DURABILITY || ability == Abilities.Mining.FASTER_MINING_FOR_HEALTH || ability == Abilities.Mining.FASTER_MINING_FOR_ORES || ability == Abilities.Mining.FASTER_MINING_IN_DARK)
            {
                if (!blockling.world.isRemote) stats.miningInterval.removeModifier(stats.miningIntervalFasterMiningEnhancedAbilityModifier);
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

    public AbilityGroup getGroup(int id)
    {
        return groups.get(id);
    }
}
