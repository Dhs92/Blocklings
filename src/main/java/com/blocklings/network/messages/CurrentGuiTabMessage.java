package com.blocklings.network.messages;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.BlocklingType;
import com.blocklings.util.Tab;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CurrentGuiTabMessage implements IMessage
{
    Tab value;
    int entityId;

    public CurrentGuiTabMessage()
    {
    }

    public CurrentGuiTabMessage(Tab value, int entityID)
    {
        this.value = value;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.value = Tab.values()[buf.readInt()];
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.value.ordinal());
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<CurrentGuiTabMessage, IMessage>
    {
        public IMessage onMessage(CurrentGuiTabMessage msg, MessageContext ctx)
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
                blockling.setCurrentGuiTab(msg.value, ctx.side.isServer());
            }

            return null;
        }
    }
}