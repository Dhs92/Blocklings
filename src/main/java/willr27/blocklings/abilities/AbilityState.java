package willr27.blocklings.abilities;

public enum AbilityState
{
    LOCKED(0x343434),
    UNLOCKED(0xf4f4f4),
    BOUGHT(0xffc409);

    public int colour;

    AbilityState(int colour)
    {
        this.colour = colour;
    }
}
