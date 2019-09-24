package willr27.blocklings.ability;

import javafx.util.Pair;
import willr27.blocklings.gui.util.AbilityWidget;

import java.awt.*;
import java.util.Arrays;

public class Ability
{
    public int id;
    public AbilityType type;
    public String name;
    public String description;
    public int x;
    public int y;
    public int textureX;
    public int textureY;
    public Color colour;
    public AbilityWidget.ConnectionType connectionType;
    private Pair<Integer, Integer>[] levelRequirements = new Pair[0];
    private Ability[] parents = new Ability[0];
    private Ability[] conflicts = new Ability[0];

    public Ability()
    {
    }

    public void setGeneralInfo(int id, AbilityType type, String name, String description)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void setGuiInfo(int x, int y, int textureX, int textureY, int colour)
    {
        this.x = x;
        this.y = y;
        this.textureX = textureX;
        this.textureY = textureY;
        this.colour = new Color(colour);
    }

    public void setConnectionType(AbilityWidget.ConnectionType connectionType)
    {
        this.connectionType = connectionType;
    }

    public Pair<Integer, Integer>[] getLevelRequirements()
    {
        return levelRequirements;
    }
    public void setLevelRequirements(Pair<Integer, Integer>... levelRequirements)
    {
        this.levelRequirements = levelRequirements;
    }

    public Ability[] getParents()
    {
        return Arrays.copyOf(parents, parents.length);
    }
    public void setParents(Ability... parents)
    {
        this.parents = parents;
    }

    public Ability[] getConflicts()
    {
        return Arrays.copyOf(conflicts, conflicts.length);
    }
    public void setConflicts(Ability... conflicts)
    {
        this.conflicts = conflicts;
    }
}