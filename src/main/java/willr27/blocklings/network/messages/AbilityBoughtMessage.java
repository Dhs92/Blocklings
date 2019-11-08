package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.abilities.Ability;
import willr27.blocklings.abilities.AbilityGroup;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class AbilityBoughtMessage implements IMessage
{
    String abilityId;
    String groupId;
    int entityId;

    private AbilityBoughtMessage() {}
    public AbilityBoughtMessage(Ability ability, AbilityGroup group, int entityId)
    {
        this.abilityId = ability.id;
        this.groupId = group.id;
        this.entityId = entityId;
    }

    public static void encode(AbilityBoughtMessage msg, PacketBuffer buf)
    {
        buf.writeString(msg.abilityId);
        buf.writeString(msg.groupId);
        buf.writeInt(msg.entityId);
    }

    public static AbilityBoughtMessage decode(PacketBuffer buf)
    {
        AbilityBoughtMessage msg = new AbilityBoughtMessage();
        msg.abilityId = buf.readString();
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
                    AbilityGroup group = blockling.abilityManager.getGroup(groupId);
                    Ability ability = group.getAbility(abilityId);
                    blockling.abilityManager.tryBuyAbility(group, ability);
                }
            }
        });
    }
}