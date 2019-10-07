package willr27.blocklings.entity.blockling;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.network.IMessage;
import willr27.blocklings.network.NetworkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlocklingAttribute
{
    private final BlocklingEntity blockling;
    private final World world;
    public final String name;
    public final String displayName;
    public float baseValue;
    private float value;
    private String displayString;
    public final boolean displayAsInt;
    private final List<BlocklingAttributeModifier> modifiers = new ArrayList<>();
    protected Runnable callback;
    protected Function<BlocklingAttribute, String> displayStringFunction = att -> "" + att.getInt();

    public BlocklingAttribute(BlocklingEntity blockling, String name, String displayName, float baseValue, boolean displayAsInt)
    {
        this.blockling = blockling;
        this.world = blockling.world;
        this.name = name;
        this.displayName = displayName;
        this.baseValue = baseValue;
        this.value = baseValue;
        this.displayString = "" + getInt();
        this.displayAsInt = displayAsInt;
    }

    public void setCallback(Runnable callback)
    {
        this.callback = callback;
    }

    public void setDisplayStringFunction(Function<BlocklingAttribute, String> displayStringFunction)
    {
        this.displayStringFunction = displayStringFunction;
    }

    public void calculateValue()
    {
        value = 0;
        float tempBase = baseValue;
        boolean end = false;

        for (BlocklingAttributeModifier modifier : modifiers)
        {
            if (modifier.operation == BlocklingAttributeModifier.Operation.ADDITION)
            {
                value += modifier.value;
            }
            else if (modifier.operation == BlocklingAttributeModifier.Operation.MULTIPLY_BASE)
            {
                tempBase *= modifier.value;
            }
            else if (modifier.operation == BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL)
            {
                if (!end)
                {
                    value += tempBase;
                    end = true;
                }
                value *= modifier.value;
            }
        }

        if (!end)
        {
            value += tempBase;
        }

        setValue(value, true); // TODO: IS THIS WEIRD?
    }

    protected void setValue(float value, boolean sync)
    {
        this.value = value;
        this.displayString = displayStringFunction.apply(this);
        if (callback != null) callback.run();
        if (sync) NetworkHandler.sync(world, new BlocklingAttributeMessage(blockling.getStats().attributes.indexOf(this), value, blockling.getEntityId()));
    }

    public void addModifier(BlocklingAttributeModifier modifier)
    {
        // Don't add if modifier is already applied
        if (modifiers.contains(modifier))
        {
            return;
        }

        // Add total multiplications last
        if (modifier.operation != BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL)
        {
            modifiers.add(0, modifier);
        }
        else
        {
            modifiers.add(modifier);
        }

        // Calculate new value
        calculateValue();
    }
    public void removeModifier(BlocklingAttributeModifier modifier)
    {
        // Remove if exists
        modifiers.remove(modifier);

        // Recalculate value
        calculateValue();
    }

    public float getBaseValue()
    {
        return baseValue;
    }
    public void incBaseValue(float amount)
    {
        setBaseValue(baseValue + amount);
    }
    public void setBaseValue(float baseValue)
    {
        this.baseValue = baseValue;
        calculateValue();
    }

    public float getFloat()
    {
        return value;
    }

    /*
    ** Rounds value to nearest int.
     */
    public int getInt()
    {
        return Math.round(value);
    }

    /*
    ** Returns the display value for the attribute, default is casted to int.
     */
    public String getDisplayString()
    {
        return displayString;
    }


    public static class BlocklingAttributeMessage implements IMessage
    {
        int index;
        float value;
        int entityId;

        private BlocklingAttributeMessage() {}
        public BlocklingAttributeMessage(int index, float value, int entityId)
        {
            this.index = index;
            this.value = value;
            this.entityId = entityId;
        }

        public static void encode(BlocklingAttributeMessage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeFloat(msg.value);
            buf.writeInt(msg.entityId);
        }

        public static BlocklingAttributeMessage decode(PacketBuffer buf)
        {
            BlocklingAttributeMessage msg = new BlocklingAttributeMessage();
            msg.index = buf.readInt();
            msg.value = buf.readFloat();
            msg.entityId = buf.readInt();

            return msg;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                NetworkEvent.Context context = ctx.get();
                boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

                PlayerEntity player = isClient ? Minecraft.getInstance().player : ctx.get().getSender();
                if (player != null)
                {
                    BlocklingEntity blockling = (BlocklingEntity) player.world.getEntityByID(entityId);
                    if (blockling != null)
                    {
                        BlocklingAttribute attribute = blockling.getStats().attributes.get(index);
                        attribute.setValue(value, !isClient);
                    }
                }
            });
        }
    }
}
