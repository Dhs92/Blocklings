package com.blocklings.util;

import java.util.ArrayList;
import java.util.List;

public enum Task
{
    GUARD("Guard", 3, 0),
    HUNT("Hunt", 4, 0),
    TANK("Tank", 5, 0),
    MINE("Mine", 6, 0),
    CHOP("Chop", 7, 0),
    FARM("Farm", 8, 0);

    public static final int BUTTON_SIZE = 20;
    public static final int ICON_SIZE = 16;

    public String name;
    public int xTex, yTex;

    Task(String name, int xTex, int yTex)
    {
        this.name = name;
        this.xTex = xTex * ICON_SIZE;
        this.yTex = yTex * ICON_SIZE + 32;
    }
}
