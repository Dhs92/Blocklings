package com.blocklings.network.messages.whitelist;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.whitelist.BlocklingWhitelist;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class BlocklingWhitelistAllMessage implements IMessage
{
    UUID id;
    TreeMap<ResourceLocation, Boolean> whitelist;
    int entityId;

    public BlocklingWhitelistAllMessage()
    {
    }

    public BlocklingWhitelistAllMessage(UUID id, TreeMap<ResourceLocation, Boolean> whitelist, int entityID)
    {
        this.id = id;
        this.whitelist = whitelist;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.whitelist = new TreeMap();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            this.whitelist.put(new ResourceLocation(ByteBufUtils.readUTF8String(buf)), buf.readBoolean());
        }
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.id.toString());
        buf.writeInt(this.whitelist.size());
        for (Map.Entry entry : this.whitelist.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, ((ResourceLocation)entry.getKey()).toString());
            buf.writeBoolean((boolean)entry.getValue());
        }
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<BlocklingWhitelistAllMessage, IMessage>
    {
        public IMessage onMessage(BlocklingWhitelistAllMessage msg, MessageContext ctx)
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
                blockling.setWhitelist(msg.id, new BlocklingWhitelist(blockling, msg.id, msg.whitelist), ctx.side.isServer());
            }

            return null;
        }
    }
}