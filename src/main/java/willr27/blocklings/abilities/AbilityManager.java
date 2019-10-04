package willr27.blocklings.abilities;

import javafx.util.Pair;
import willr27.blocklings.entity.blockling.BlocklingEntity;
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

    public boolean canBuyAbility(AbilityGroup group, Ability ability)
    {
        int pointsNeeded = ability.getSkillPointsRequired();
        if (blockling.getStats().getSkillPoints() < pointsNeeded && group.allParentsBought(ability) && !group.hasConflict(ability))
        {
            return false;
        }

        for (Pair<Integer, Integer> levelRequirement : ability.getLevelRequirements())
        {
            if (blockling.getStats().getLevel(levelRequirement.getKey()) < levelRequirement.getValue())
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

        blockling.getStats().incSkillPoints(-ability.getSkillPointsRequired());
        group.setState(ability, AbilityState.BOUGHT);
    }

    public AbilityGroup getGroup(int id)
    {
        return groups.get(id);
    }
}
