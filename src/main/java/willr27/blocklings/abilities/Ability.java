package willr27.blocklings.abilities;

import javafx.util.Pair;
import willr27.blocklings.gui.util.widgets.AbilityWidget;

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
    private int skillPointsRequired;
    private Pair<String, Float>[] levelRequirements = new Pair[0];
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

    public int getSkillPointsRequired()
    {
        return skillPointsRequired;
    }
    public void setSkillPointsRequired(int value)
    {
        skillPointsRequired = value;
    }

    public Pair<String, Float>[] getLevelRequirements()
    {
        return levelRequirements;
    }
    public void setLevelRequirements(Pair<String, Float>... levelRequirements)
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
