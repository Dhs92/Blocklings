package willr27.blocklings.ability;

import java.util.HashMap;
import java.util.Map;

public class AbilityGroup
{
    private static int i = 0;
    public static final int GENERAL = i++;
    public static final int COMBAT = i++;
    public static final int MINING = i++;
    public static final int WOODCUTTING = i++;
    public static final int FARMING = i++;

    public final int id;
    public final String name;

    private Map<Ability, AbilityState> abilities = new HashMap<>();

    public AbilityGroup(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public boolean contains(Ability ability)
    {
        return abilities.containsKey(ability);
    }

    public Ability[] getAbilities()
    {
        return abilities.keySet().toArray(new Ability[0]);
    }
    public void addAbility(Ability ability)
    {
        abilities.put(ability, AbilityState.LOCKED);
    }

    public AbilityState getState(Ability ability)
    {
        return abilities.get(ability);
    }
    public void setState(Ability ability, AbilityState state)
    {
        abilities.replace(ability, state);
    }
}
