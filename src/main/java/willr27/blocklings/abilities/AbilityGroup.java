package willr27.blocklings.abilities;

import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.AbilityMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityGroup
{
    private static int i = 0;
    public static final int GENERAL = i++;
    public static final int COMBAT = i++;
    public static final int MINING = i++;
    public static final int WOODCUTTING = i++;
    public static final int FARMING = i++;

    private final BlocklingEntity blockling;
    public final int id;
    public final String name;

    private Map<Ability, AbilityState> abilities = new HashMap<>();

    public AbilityGroup(BlocklingEntity blockling, int id, String name)
    {
        this.blockling = blockling;
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
    public Ability getAbility(int id)
    {
        for (Ability ability : abilities.keySet())
        {
            if (id == ability.id) return ability;
        }
        return null;
    }
    public void addAbility(Ability ability)
    {
        abilities.put(ability, ability.getParents().length == 0 ? AbilityState.UNLOCKED : AbilityState.LOCKED);
    }
    public void addAllAbilities(List<Ability> abilities)
    {
        for (Ability ability : abilities)
        {
            addAbility(ability);
        }
    }

    public AbilityState getState(Ability ability)
    {
        return abilities.get(ability);
    }
    public void setState(Ability ability, AbilityState state)
    {
        setState(ability.id, state, true);
    }
    public void setState(int abilityId, AbilityState state)
    {
        setState(abilityId, state, true);
    }
    public void setState(Ability ability, AbilityState state, boolean sync)
    {
        setState(ability.id, state, sync);
    }
    public void setState(int abilityId, AbilityState state, boolean sync)
    {
        Ability ability = getAbility(abilityId);
        abilities.replace(ability, state);
        if (state == AbilityState.BOUGHT)
        {
            for (Ability child : findChildren(ability))
            {
                if (allParentsBought(child)) setState(child, AbilityState.UNLOCKED, sync);
            }
        }
        if (sync) NetworkHandler.sync(blockling.world, new AbilityMessage(ability, state, this, blockling.getEntityId()));
    }

    public boolean allParentsBought(Ability ability)
    {
        return allParentsHaveState(ability, AbilityState.BOUGHT);
    }

    public boolean allParentsHaveState(Ability ability, AbilityState state)
    {
        boolean bought = true;
        for (Ability parent : ability.getParents())
        {
            if (getState(parent) != state)
            {
                bought = false;
                break;
            }
        }
        return bought;
    }

    public boolean parentHasState(Ability ability, AbilityState state)
    {
        for (Ability parent : ability.getParents())
        {
            if (getState(parent) == state) return true;
        }
        return false;
    }

    public List<Ability> findChildren(Ability ability)
    {
        List<Ability> children = new ArrayList<>();
        for (Ability child : abilities.keySet())
        {
            for (Ability parent : child.getParents())
            {
                if (parent == ability)
                {
                    children.add(child);
                }
            }
        }
        return children;
    }

    public boolean hasConflict(Ability ability)
    {
        return !findConflicts(ability).isEmpty();
    }
    public List<Ability> findConflicts(Ability ability)
    {
        List<Ability> conflicts = new ArrayList<>();

        for (Ability conflict : ability.getConflicts())
        {
            if (getState(conflict) == AbilityState.BOUGHT)
            {
                conflicts.add(conflict);
            }
        }

        return conflicts;
    }
}
