package com.blocklings.network.messages;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.network.NetworkHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jline.utils.Log;

public class InventoryStackMessage implements IMessage
{
    int slotIndex;
    ItemStack stack;
    int entityID;

    public InventoryStackMessage()
    {
    }

    public InventoryStackMessage(int slotIndex, ItemStack stack, int entityID)
    {
        this.slotIndex = slotIndex;
        this.stack = stack;
        this.entityID = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.slotIndex = buf.readInt();
        this.stack = ByteBufUtils.readItemStack(buf);
        this.entityID = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.slotIndex);
        ByteBufUtils.writeItemStack(buf, this.stack);
        buf.writeInt(this.entityID);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<InventoryStackMessage, IMessage>
    {
        public IMessage onMessage(InventoryStackMessage msg, MessageContext ctx)
        {
            EntityPlayer player = Blocklings.proxy.getPlayer(ctx);
            if (player == null)
            {
                return null;
            }

            Entity entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(msg.entityID);
            if (entity instanceof EntityBlockling)
            {
                EntityBlockling blockling = (EntityBlockling) entity;

                //Log.info("Setting slot " + msg.slotIndex + " to " + msg.stack + " on " + ctx.side);
                blockling.getInv().setInventorySlotContents(msg.slotIndex, msg.stack, false);

                if (ctx.side.isServer())
                {
                    NetworkHelper.sendToAll(new InventoryStackMessage(msg.slotIndex, msg.stack, msg.entityID));
                }
            }

            return null;
        }
    }
}