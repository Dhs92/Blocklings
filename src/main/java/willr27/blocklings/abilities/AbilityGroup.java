package willr27.blocklings.abilities;

import net.minecraft.util.ResourceLocation;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.AbilityMessage;

import java.util.*;

public class AbilityGroup
{
    private static int i = 0;
    public static final String GENERAL = "cf5f4d12-03c1-475c-a4a6-fee8484e8ec4";
    public static final String COMBAT = "adfab53d-03e7-47e1-8dbe-cf40ee597045";
    public static final String MINING = "c28f70f5-e775-489f-ba08-5d53d1e4200f";
    public static final String WOODCUTTING = "2297bd04-0ea9-401f-a690-9774a9785f75";
    public static final String FARMING = "e71f5788-1a88-41df-8311-c397d5174d51";

    private final BlocklingEntity blockling;
    public final String id;
    public final String name;
    public final ResourceLocation background;
    public final ResourceLocation icons;

    private Map<Ability, AbilityState> abilities = new HashMap<>();

    public AbilityGroup(BlocklingEntity blockling, String id, String name, ResourceLocation background, ResourceLocation icons)
    {
        this.blockling = blockling;
        this.id = id;
        this.name = name;
        this.background = background;
        this.icons = icons;
    }

    public boolean contains(Ability ability)
    {
        return abilities.containsKey(ability);
    }

    public List<Ability> getAbilities()
    {
        return Arrays.asList(abilities.keySet().toArray(new Ability[0]));
    }
    public Ability getAbility(String id)
    {
        for (Ability ability : abilities.keySet())
        {
            if (id.equals(ability.id)) return ability;
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
    public void setState(String abilityId, AbilityState state)
    {
        setState(abilityId, state, true);
    }
    public void setState(Ability ability, AbilityState state, boolean sync)
    {
        setState(ability.id, state, sync);
    }
    public void setState(String abilityId, AbilityState state, boolean sync)
    {
        Ability ability = getAbility(abilityId);
        blockling.abilityManager.stateChanged(this, ability, state);
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
