package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.abilities.Ability;
import willr27.blocklings.abilities.AbilityGroup;
import willr27.blocklings.abilities.AbilityState;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class AbilityMessage implements IMessage
{
    String abilityId;
    int stateOrdinal;
    String groupId;
    int entityId;

    private AbilityMessage() {}
    public AbilityMessage(Ability ability, AbilityState state, AbilityGroup group, int entityId)
    {
        this.abilityId = ability.id;
        this.stateOrdinal = state.ordinal();
        this.groupId = group.id;
        this.entityId = entityId;
    }

    public static void encode(AbilityMessage msg, PacketBuffer buf)
    {
        buf.writeString(msg.abilityId);
        buf.writeInt(msg.stateOrdinal);
        buf.writeString(msg.groupId);
        buf.writeInt(msg.entityId);
    }

    public static AbilityMessage decode(PacketBuffer buf)
    {
        AbilityMessage msg = new AbilityMessage();
        msg.abilityId = buf.readString();
        msg.stateOrdinal = buf.readInt();
        msg.groupId = buf.readString();
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
                    blockling.abilityManager.getGroup(groupId).setState(abilityId, AbilityState.values()[stateOrdinal], !isClient);
                }
            }
        });
    }
}