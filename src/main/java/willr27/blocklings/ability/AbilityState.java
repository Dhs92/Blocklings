package willr27.blocklings.ability;

public enum AbilityState
{
    LOCKED(0xffff0000),
    UNLOCKED(0xff00ff00),
    BOUGHT(0xff0000ff);

    public int colour;

    AbilityState(int colour)
    {
        this.colour = colour;
    }
}
