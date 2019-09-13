package willr27.blocklings.ability;

public class Abilities
{
    // MINING

    public static final Ability NEARBY_MINING = new Ability(0, AbilityType.AI, "Basic Miner", "Unlocks the \"Mine Nearby Ores\" task.", null, 0, 0, 3, 1);
    public static final Ability LUCK = new Ability(1, AbilityType.STAT, "Luck", "Increases the blockling's luck when mining.", NEARBY_MINING, 30, 40, 0, 2);
}
