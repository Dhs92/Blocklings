package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class GoalPriorityMessage implements IMessage
{
    int goalId;
    int newPriority;
    int entityId;

    private GoalPriorityMessage() {}
    public GoalPriorityMessage(int goalId, int newPriority, int entityId)
    {
        this.goalId = goalId;
        this.newPriority = newPriority;
        this.entityId = entityId;
    }

    public static void encode(GoalPriorityMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.goalId);
        buf.writeInt(msg.newPriority);
        buf.writeInt(msg.entityId);
    }

    public static GoalPriorityMessage decode(PacketBuffer buf)
    {
        GoalPriorityMessage msg = new GoalPriorityMessage();
        msg.goalId = buf.readInt();
        msg.newPriority = buf.readInt();
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
                    blockling.aiManager.getGoalFromId(goalId).setPriority(newPriority, true, !isClient);
                }
            }
        });
    }
}