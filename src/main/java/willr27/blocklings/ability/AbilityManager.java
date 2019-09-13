package willr27.blocklings.ability;

import willr27.blocklings.entity.blockling.BlocklingEntity;

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
        AbilityGroup general = new AbilityGroup(AbilityGroup.GENERAL, "General");

        AbilityGroup combat = new AbilityGroup(AbilityGroup.COMBAT, "Combat");

        AbilityGroup mining = new AbilityGroup(AbilityGroup.MINING, "Mining");
        mining.addAbility(Abilities.NEARBY_MINING);
        mining.addAbility(Abilities.LUCK);

        AbilityGroup woodcutting = new AbilityGroup(AbilityGroup.WOODCUTTING, "Woodcutting");

        AbilityGroup farming = new AbilityGroup(AbilityGroup.FARMING, "Farming");

        groups.put(general.id, general);
        groups.put(combat.id, combat);
        groups.put(mining.id, mining);
        groups.put(woodcutting.id, woodcutting);
        groups.put(farming.id, farming);
    }

    public AbilityGroup getGroup(int id)
    {
        return groups.get(id);
    }
}
