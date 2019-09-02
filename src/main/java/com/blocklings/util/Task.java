package com.blocklings.util;

import java.util.UUID;

public enum Task
{
    GUARD("Guard", 3, 0, "6b535728-1ae0-44f8-a5ed-e666d1ea4af8"),
    HUNT("Hunt", 4, 0, "9cc457ea-3569-4421-b77e-b175196841da"),
    TANK("Tank", 5, 0, "9374b8ac-c11a-40e7-9243-cdd72d0261ec"),
    MINE("Mine", 6, 0, "00000000-0000-0000-0000-000000000000"),
    CHOP("Chop", 7, 0, "00000000-0000-0000-0000-000000000000"),
    FARM("Farm", 8, 0, "00000000-0000-0000-0000-000000000000");

    public static final int BUTTON_SIZE = 20;
    public static final int ICON_SIZE = 16;

    public String name;
    public int xTex, yTex;
    public UUID whitelistId;

    Task(String name, int xTex, int yTex, String whitelistId)
    {
        this.name = name;
        this.xTex = xTex * ICON_SIZE;
        this.yTex = yTex * ICON_SIZE + 32;
        this.whitelistId = UUID.fromString(whitelistId);
    }
}
