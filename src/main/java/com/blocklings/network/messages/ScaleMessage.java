package com.blocklings.network.messages;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.Tab;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ScaleMessage implements IMessage
{
    float value;
    int entityId;

    public ScaleMessage()
    {
    }

    public ScaleMessage(float value, int entityID)
    {
        this.value = value;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.value = buf.readFloat();
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(this.value);
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<ScaleMessage, IMessage>
    {
        public IMessage onMessage(ScaleMessage msg, MessageContext ctx)
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
                blockling.getBlocklingStats().setScale(msg.value, ctx.side.isServer());
            }

            return null;
        }
    }
}