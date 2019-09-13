package willr27.blocklings.ability;

public class Ability
{
    public final int id;
    public final AbilityType type;
    public final String name;
    public final String description;
    public final Ability parent;
    public final int x;
    public final int y;
    public final int textureX;
    public final int textureY;

    public Ability(int id, AbilityType type, String name, String description, Ability parent, int x, int y, int textureX, int textureY)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.textureX = textureX;
        this.textureY = textureY;
    }
}
