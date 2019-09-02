package com.blocklings.network.messages.whitelist;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class BlocklingWhitelistSingleMessage implements IMessage
{
    UUID id;
    ResourceLocation entityResource;
    boolean value;
    int entityId;

    public BlocklingWhitelistSingleMessage()
    {
    }

    public BlocklingWhitelistSingleMessage(UUID id, ResourceLocation entityResource, boolean value, int entityID)
    {
        this.id = id;
        this.entityResource = entityResource;
        this.value = value;
        this.entityId = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.entityResource = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        this.value = buf.readBoolean();
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.id.toString());
        ByteBufUtils.writeUTF8String(buf, this.entityResource.toString());
        buf.writeBoolean(this.value);
        buf.writeInt(this.entityId);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<BlocklingWhitelistSingleMessage, IMessage>
    {
        public IMessage onMessage(BlocklingWhitelistSingleMessage msg, MessageContext ctx)
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
                blockling.setWhitelistEntry(msg.id, msg.entityResource, msg.value, ctx.side.isServer());
            }

            return null;
        }
    }
}