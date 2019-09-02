package com.blocklings.util;

public enum State
{
    SIT("Sit", 0, 0),
    FOLLOW("Follow", 1, 0),
    WANDER("Wander", 2, 0);

    public static final int BUTTON_SIZE = 20;
    public static final int ICON_SIZE = 16;

    public String name;
    public int xTex, yTex;

    State(String name, int xTex, int yTex)
    {
        this.name = name;
        this.xTex = xTex * ICON_SIZE;
        this.yTex = yTex * ICON_SIZE + 32;
    }
}
