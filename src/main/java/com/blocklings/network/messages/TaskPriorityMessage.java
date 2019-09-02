package com.blocklings.network.messages;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Task;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TaskPriorityMessage implements IMessage
{
    Task task;
    int priority;
    int entityId;

    public TaskPriorityMessage()
    {
    }

    public TaskPriorityMessage(Task task, int priority, int entityID)
    {
        this.task = task;
        this.priority = priority;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.task = Task.values()[buf.readInt()];
        this.priority = buf.readInt();
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.task.ordinal());
        buf.writeInt(this.priority);
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<TaskPriorityMessage, IMessage>
    {
        public IMessage onMessage(TaskPriorityMessage msg, MessageContext ctx)
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
                blockling.setTaskPriority(msg.task, msg.priority, ctx.side.isServer());
            }

            return null;
        }
    }
}