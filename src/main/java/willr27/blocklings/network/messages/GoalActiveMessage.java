package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class GoalActiveMessage implements IMessage
{
    int goalId;
    boolean active;
    int entityId;

    private GoalActiveMessage() {}
    public GoalActiveMessage(int goalId, boolean active, int entityId)
    {
        this.goalId = goalId;
        this.active = active;
        this.entityId = entityId;
    }

    public static void encode(GoalActiveMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.goalId);
        buf.writeBoolean(msg.active);
        buf.writeInt(msg.entityId);
    }

    public static GoalActiveMessage decode(PacketBuffer buf)
    {
        GoalActiveMessage msg = new GoalActiveMessage();
        msg.goalId = buf.readInt();
        msg.active = buf.readBoolean();
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
                    blockling.aiManager.getGoalFromId(goalId).setActive(active, !isClient);
                }
            }
        });
    }
}