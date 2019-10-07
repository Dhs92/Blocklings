package willr27.blocklings.entity.blockling;

public class BlocklingAttributeModifier
{
    public final BlocklingAttribute attribute;
    public final String name;
    public float value;
    public final Operation operation;

    public BlocklingAttributeModifier(BlocklingAttribute attribute, String name, float value, Operation operation)
    {
        this.attribute = attribute;
        this.name = name;
        this.value = value;
        this.operation = operation;
    }

    public void setValue(float value)
    {
        this.value = value;
        attribute.calculateValue();
    }

    public enum Operation
    {
        ADDITION,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL
    }
}
