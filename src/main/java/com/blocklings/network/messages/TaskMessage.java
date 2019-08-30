package com.blocklings.network.messages;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TaskMessage implements IMessage
{
    Task task;
    boolean value;
    int entityId;

    public TaskMessage()
    {
    }

    public TaskMessage(Task task, boolean value, int entityID)
    {
        this.task = task;
        this.value = value;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.task = Task.values()[buf.readInt()];
        this.value = buf.readBoolean();
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.task.ordinal());
        buf.writeBoolean(this.value);
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<TaskMessage, IMessage>
    {
        public IMessage onMessage(TaskMessage msg, MessageContext ctx)
        {
            EntityPlayer player = Blocklings.proxy.getPlayer(ctx);
            if (player == null)
            {
                return null;
            }

            Entity entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(msg.entityId);
            if (entity instanceof EntityBlockling)
            {
                EntityBlockling blockling = (EntityBlockling) entity;
                blockling.setTask(msg.task, msg.value, ctx.side.isServer());
            }

            return null;
        }
    }
}