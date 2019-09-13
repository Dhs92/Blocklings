package willr27.blocklings.ability;

public enum AbilityType
{
    STAT(0),
    AI(1),
    OTHER(2);

    public int textureX;

    AbilityType(int textureX)
    {
        this.textureX = textureX;
    }
}
