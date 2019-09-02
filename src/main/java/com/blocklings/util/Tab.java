package com.blocklings.util;

public enum Tab
{
    STATS("Stats", 0, true, true),
    TASKS("Tasks", 1, true, true),
    EQUIPMENT("Equipment", 2, true, false),
    INVENTORY("Inventory", 3, true, false),
    GENERAL("General", 0, false, true),
    COMBAT("Combat", 1, false, true),
    MINING("Mining", 2, false, true),
    WOODCUTTING("Woodcutting", 3, false, true),
    FARMING("Farming", 4, false, true);

    public String displayName;
    // Order tabs appear
    public int pos;
    public boolean left;
    // Is this a client only tab
    public boolean client;

    Tab(String displayName, int pos, boolean left, boolean client)
    {
        this.displayName = displayName;
        this.pos = pos;
        this.left = left;
        this.client = client;
    }
}
