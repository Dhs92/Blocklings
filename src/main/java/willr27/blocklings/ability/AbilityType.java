package willr27.blocklings.ability;

public enum AbilityType
{
    STAT(0),
    AI(1),
    UTILITY(2),
    OTHER(3);

    public int textureX;

    AbilityType(int textureX)
    {
        this.textureX = textureX;
    }
}
