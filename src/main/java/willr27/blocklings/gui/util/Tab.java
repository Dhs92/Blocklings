package willr27.blocklings.gui.util;

import willr27.blocklings.abilities.AbilityGroup;

import java.util.ArrayList;
import java.util.List;

public enum Tab
{
    STATS("Stats", GuiHandler.STATS_ID, "", 0, 0, true),
    TASKS("Tasks", GuiHandler.TASKS_ID, "", 1, 0, true),
    EQUIPMENT("Equipment", GuiHandler.EQUIPMENT_ID, "", 2, 0, true),
    UTILITY_1("Utility 1", GuiHandler.UTILITY_ID, "", 3, 0, true),
    UTILITY_2("Utility 2", GuiHandler.UTILITY_ID, "", 3, 0, true),
    GENERAL("General", GuiHandler.GENERAL_ID, AbilityGroup.GENERAL, 0, 1, false),
    COMBAT("Combat", GuiHandler.COMBAT_ID, AbilityGroup.COMBAT, 1, 1, false),
    MINING("Mining", GuiHandler.MINING_ID, AbilityGroup.MINING, 2, 1, false),
    WOODCUTTING("Woodcutting", GuiHandler.WOODCUTTING_ID, AbilityGroup.WOODCUTTING, 3, 1, false),
    FARMING("Farming", GuiHandler.FARMING_ID, AbilityGroup.FARMING, 4, 1, false);

    public String name;
    public int guiId;
    public String abilityGroupId;
    public int textureX, textureY;
    public boolean left;

    Tab(String name, int guiId, String abilityGroupId, int textureX, int textureY, boolean left)
    {
        this.name = name;
        this.guiId = guiId;
        this.abilityGroupId = abilityGroupId;
        this.textureX = textureX;
        this.textureY = textureY;
        this.left = left;
    }

    public static List<Tab> leftTabs = new ArrayList<>();
    public static List<Tab> rightTabs = new ArrayList<>();
    static
    {
        for (Tab tab : values())
        {
            if (tab.left) leftTabs.add(tab);
            else rightTabs.add(tab);
        }
    }

    public static boolean hasTab(int guiId)
    {
        return getTab(guiId) != null;
    }

    public static Tab getTab(int guiId)
    {
        for (Tab tab: values())
        {
            if (tab.guiId == guiId)
            {
                return tab;
            }
        }

        return null;
    }
}
