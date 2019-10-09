package willr27.blocklings.abilities;

import javafx.util.Pair;
import willr27.blocklings.gui.util.widgets.AbilityWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Abilities
{
    public static class Mining
    {
        public static final List<Ability> ABILITIES = new ArrayList<>();
        private static int id = 200;

        public static final Ability NOVICE_MINER = create();
        public static final Ability FASTER_MINING = create();
        public static final Ability FASTER_MINING_FOR_HEALTH = create();
        public static final Ability FASTER_MINING_FOR_ORES = create();
        public static final Ability FASTER_MINING_FOR_DURABILITY = create();
        public static final Ability FASTER_MINING_IN_DARK = create();
        public static final Ability AUTOSMELT = create();
        public static final Ability AUTOSMELT_WHITELIST = create();
        public static final Ability FUEL_EFFICIENT = create();
        public static final Ability AUTOSMELT_XP = create();
        public static final Ability TORCH_PLACER = create();
        public static final Ability TORCH_CRAFTER = create();

        static
        {
            NOVICE_MINER.setGeneralInfo(id++, AbilityType.AI, "Novice Miner", "Unlocks the \"Mine Nearby Ores\" task.");
            FASTER_MINING.setGeneralInfo(id++, AbilityType.STAT, "Efficiency", "Bonus 10% mining speed.");
            FASTER_MINING_FOR_HEALTH.setGeneralInfo(id++, AbilityType.STAT, "Adrenaline", "The lower your blockling's health, the faster they will mine.");
            FASTER_MINING_FOR_ORES.setGeneralInfo(id++, AbilityType.STAT, "Momentum", "Your blockling mines faster for each consecutive ore mined.");
            FASTER_MINING_FOR_DURABILITY.setGeneralInfo(id++, AbilityType.STAT, "Hasty", "Your blockling mines 25% faster but their pickaxes use 2x the durability.");
            FASTER_MINING_IN_DARK.setGeneralInfo(id++, AbilityType.STAT, "Night Owl", "Your blockling mines faster the lower the light level.");
            AUTOSMELT.setGeneralInfo(id++, AbilityType.AI, "Autosmelt", "Your blockling will smelt ores in their inventory. Requires furnace utility.");
            AUTOSMELT_WHITELIST.setGeneralInfo(id++, AbilityType.OTHER, "Ore Whitelist", "Unlocks the ore whitelist for smelting.");
            FUEL_EFFICIENT.setGeneralInfo(id++, AbilityType.OTHER, "Fuel Efficient", "50% Chance to not use fuel when smelting.");
            AUTOSMELT_XP.setGeneralInfo(id++, AbilityType.OTHER, "Bonus XP", "Smelting can sometimes yield bonus xp.");
            TORCH_PLACER.setGeneralInfo(id++, AbilityType.OTHER, "Keep It Light", "Your blockling can place down torches in dark areas.");
            TORCH_CRAFTER.setGeneralInfo(id++, AbilityType.UTILITY, "Torch Crafter TODO", "Your blockling can craft torches as needed given the correct materials. Requires crafting table utility.");

            NOVICE_MINER.setGuiInfo(0, 0, 0, 0, 0xdddddd);
            FASTER_MINING.setGuiInfo(70, 0, 1, 0, 0xffd56d);
            FASTER_MINING_FOR_HEALTH.setGuiInfo(140, -50, 2, 0, 0xb72626);
            FASTER_MINING_FOR_ORES.setGuiInfo(140, 50, 3, 0, 0xad79b5);
            FASTER_MINING_FOR_DURABILITY.setGuiInfo(210, -50, 4, 0, 0x4eb2aa);
            FASTER_MINING_IN_DARK.setGuiInfo(210, 50, 5, 0, 0x2b2a3d);
            AUTOSMELT.setGuiInfo(-70, -100, 6, 0, 0xffde00);
            AUTOSMELT_WHITELIST.setGuiInfo(-70, -190, 7, 0, 0xff9c00);
            FUEL_EFFICIENT.setGuiInfo(-110, -240, 7, 0, 0xffa800);
            AUTOSMELT_XP.setGuiInfo(-30, -240, 8, 0, 0xdcff00);
            TORCH_PLACER.setGuiInfo(70, -100, 9, 0, 0xffe700);
            TORCH_CRAFTER.setGuiInfo(70, -240, 0, 1, 0xffa800);

            FASTER_MINING.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            FASTER_MINING_FOR_HEALTH.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            FASTER_MINING_FOR_ORES.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            FASTER_MINING_FOR_DURABILITY.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            FASTER_MINING_IN_DARK.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            AUTOSMELT.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            AUTOSMELT_WHITELIST.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_SHORTEST_SPLIT);
            FUEL_EFFICIENT.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_LONGEST_SPLIT);
            AUTOSMELT_XP.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_LONGEST_SPLIT);
            TORCH_PLACER.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
            TORCH_CRAFTER.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_SHORTEST_SPLIT);

            NOVICE_MINER.setSkillPointsRequired(1);
            FASTER_MINING.setSkillPointsRequired(1);
            FASTER_MINING_FOR_HEALTH.setSkillPointsRequired(3);
            FASTER_MINING_FOR_ORES.setSkillPointsRequired(3);
            FASTER_MINING_FOR_DURABILITY.setSkillPointsRequired(3);
            FASTER_MINING_IN_DARK.setSkillPointsRequired(3);
            AUTOSMELT.setSkillPointsRequired(2);
            AUTOSMELT_WHITELIST.setSkillPointsRequired(2);
            FUEL_EFFICIENT.setSkillPointsRequired(1);
            AUTOSMELT_XP.setSkillPointsRequired(1);
            TORCH_PLACER.setSkillPointsRequired(2);
            TORCH_CRAFTER.setSkillPointsRequired(3);

            FASTER_MINING.setLevelRequirements(new Pair<>("miningLevel", 5.0f));
            FASTER_MINING_FOR_HEALTH.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
            FASTER_MINING_FOR_ORES.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
            FASTER_MINING_FOR_DURABILITY.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
            FASTER_MINING_IN_DARK.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
            AUTOSMELT.setLevelRequirements(new Pair<>("miningLevel", 15.0f));
            AUTOSMELT_WHITELIST.setLevelRequirements(new Pair<>("miningLevel", 30.0f));
            FUEL_EFFICIENT.setLevelRequirements(new Pair<>("miningLevel", 45.0f));
            AUTOSMELT_XP.setLevelRequirements(new Pair<>("miningLevel", 45.0f));
            TORCH_PLACER.setLevelRequirements(new Pair<>("miningLevel", 20.0f));
            TORCH_CRAFTER.setLevelRequirements(new Pair<>("miningLevel", 40.0f));

            FASTER_MINING.setParents(NOVICE_MINER);
            FASTER_MINING_FOR_HEALTH.setParents(FASTER_MINING);
            FASTER_MINING_FOR_ORES.setParents(FASTER_MINING);
            FASTER_MINING_FOR_DURABILITY.setParents(FASTER_MINING);
            FASTER_MINING_IN_DARK.setParents(FASTER_MINING);
            AUTOSMELT.setParents(NOVICE_MINER);
            AUTOSMELT_WHITELIST.setParents(AUTOSMELT);
            FUEL_EFFICIENT.setParents(AUTOSMELT);
            AUTOSMELT_XP.setParents(AUTOSMELT);
            TORCH_PLACER.setParents(NOVICE_MINER);
            TORCH_CRAFTER.setParents(TORCH_PLACER);

            createConflictGroup(FASTER_MINING_FOR_HEALTH, FASTER_MINING_FOR_ORES, FASTER_MINING_FOR_DURABILITY, FASTER_MINING_IN_DARK);
            createConflictGroup(FUEL_EFFICIENT, AUTOSMELT_XP);
        }

        private static Ability create()
        {
            Ability ability = new Ability();
            ABILITIES.add(ability);
            return ability;
        }
    }

    private static void createConflictGroup(Ability... abilities)
    {
        for (int i = 0; i < abilities.length; i++)
        {
            Ability ability = abilities[i];
            List<Ability> conflicts = new LinkedList<>(Arrays.asList(abilities));
            conflicts.remove(i);
            ability.setConflicts(Arrays.copyOf(conflicts.toArray(), conflicts.size(), Ability[].class));
        }
    }
}
