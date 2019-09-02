package com.blocklings.network.messages;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenGuiMessage implements IMessage
{
    int guiId;
    int entityId;

    public OpenGuiMessage()
    {
    }

    public OpenGuiMessage(int guiId, int entityID)
    {
        this.guiId = guiId;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.guiId = buf.readInt();
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.guiId);
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<OpenGuiMessage, IMessage>
    {
        public IMessage onMessage(OpenGuiMessage msg, MessageContext ctx)
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
                blockling.openGui(player, msg.guiId, ctx.side.isServer());
            }

            return null;
        }
    }
}