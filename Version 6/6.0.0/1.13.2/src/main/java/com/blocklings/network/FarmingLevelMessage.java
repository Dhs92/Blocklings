package com.blocklings.network;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.main.Blocklings;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FarmingLevelMessage implements IMessage
{
    int value;
    int id;

    public FarmingLevelMessage()
    {
    }

    public FarmingLevelMessage(int value, int entityID)
    {
        this.value = value;
        this.id = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.value = buf.readInt();
        this.id = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.value);
        buf.writeInt(this.id);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<FarmingLevelMessage, IMessage>
    {
        public IMessage onMessage(FarmingLevelMessage message, MessageContext ctx)
        {
            Entity entity = null;

            if ((ctx.side.isClient()) && (Blocklings.proxy.getPlayer(ctx) != null))
            {
                entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(message.id);

                if (entity instanceof EntityBlockling)
                {
                    EntityBlockling blockling = (EntityBlockling) entity;

                    blockling.setFarmingLevelFromPacket(message.value);
                }
            }
            else if (ctx.side.isServer() && Blocklings.proxy.getPlayer(ctx) != null)
            {
                entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(message.id);

                if ((entity instanceof EntityBlockling))
                {
                    EntityBlockling blockling = (EntityBlockling) entity;

                    blockling.setFarmingLevelFromPacket(message.value);
                }
            }

            return null;
        }
    }
}