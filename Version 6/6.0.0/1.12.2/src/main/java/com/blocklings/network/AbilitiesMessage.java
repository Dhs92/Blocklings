package com.blocklings.network;

import com.blocklings.abilities.Ability;
import com.blocklings.abilities.AbilityGroup;
import com.blocklings.entities.EntityBlockling;
import com.blocklings.main.Blocklings;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AbilitiesMessage implements IMessage
{
    AbilityGroup generalAbilities;
    AbilityGroup combatAbilities;
    AbilityGroup miningAbilities;
    AbilityGroup woodcuttingAbilities;
    int id;

    public AbilitiesMessage()
    {
    }

    public AbilitiesMessage(AbilityGroup generalAbilities, AbilityGroup combatAbilities, AbilityGroup miningAbilities, AbilityGroup woodcuttingAbilities, int entityID)
    {
        this.generalAbilities = generalAbilities;
        this.combatAbilities = combatAbilities;
        this.miningAbilities = miningAbilities;
        this.woodcuttingAbilities = woodcuttingAbilities;
        this.id = entityID;
    }

    public void fromBytes(ByteBuf buf)
    {
        int g = buf.readInt();
        int c = buf.readInt();
        int m = buf.readInt();
        int w = buf.readInt();

        generalAbilities = new AbilityGroup();
        combatAbilities = new AbilityGroup();
        miningAbilities = new AbilityGroup();
        woodcuttingAbilities = new AbilityGroup();

        generalAbilities.id = buf.readInt();
        combatAbilities.id = buf.readInt();
        miningAbilities.id = buf.readInt();
        woodcuttingAbilities.id = buf.readInt();

        generalAbilities.groupName = ByteBufUtils.readUTF8String(buf);
        combatAbilities.groupName = ByteBufUtils.readUTF8String(buf);
        miningAbilities.groupName = ByteBufUtils.readUTF8String(buf);
        woodcuttingAbilities.groupName = ByteBufUtils.readUTF8String(buf);

        List<Ability> generalAbilitiesList = new ArrayList<Ability>();
        List<Ability> combatAbilitiesList = new ArrayList<Ability>();
        List<Ability> miningAbilitiesList = new ArrayList<Ability>();
        List<Ability> woodcuttingAbilitiesList = new ArrayList<Ability>();

        for (int i = 0; i < g; i++)
        {
            Ability ability = new Ability();
            ability.id = buf.readInt();
            ability.parentId = buf.readInt();
            ability.state = Ability.State.values()[buf.readInt()];
            ability.colour = new Color(buf.readInt());
            ability.textureX = buf.readInt();
            ability.textureY = buf.readInt();
            ability.width = buf.readInt();
            ability.height = buf.readInt();
            ability.x = buf.readInt();
            ability.y = buf.readInt();
            ability.name = ByteBufUtils.readUTF8String(buf);
            ability.description = ByteBufUtils.readUTF8String(buf);
            generalAbilitiesList.add(ability);
        }
        for (Ability ability : generalAbilitiesList)
        {
            for (Ability ability2 : generalAbilitiesList)
            {
                if (ability.parentId == -1) continue;

                if (ability.parentId == ability2.id)
                {
                    ability.parentAbility = ability2;
                }
            }
        }
        for (int i = 0; i < c; i++)
        {
            Ability ability = new Ability();
            ability.id = buf.readInt();
            ability.parentId = buf.readInt();
            ability.state = Ability.State.values()[buf.readInt()];
            ability.colour = new Color(buf.readInt());
            ability.textureX = buf.readInt();
            ability.textureY = buf.readInt();
            ability.width = buf.readInt();
            ability.height = buf.readInt();
            ability.x = buf.readInt();
            ability.y = buf.readInt();
            ability.name = ByteBufUtils.readUTF8String(buf);
            ability.description = ByteBufUtils.readUTF8String(buf);
            combatAbilitiesList.add(ability);
        }
        for (Ability ability : combatAbilitiesList)
        {
            for (Ability ability2 : combatAbilitiesList)
            {
                if (ability.parentId == -1) continue;

                if (ability.parentId == ability2.id)
                {
                    ability.parentAbility = ability2;
                }
            }
        }
        for (int i = 0; i < m; i++)
        {
            Ability ability = new Ability();
            ability.id = buf.readInt();
            ability.parentId = buf.readInt();
            ability.state = Ability.State.values()[buf.readInt()];
            ability.colour = new Color(buf.readInt());
            ability.textureX = buf.readInt();
            ability.textureY = buf.readInt();
            ability.width = buf.readInt();
            ability.height = buf.readInt();
            ability.x = buf.readInt();
            ability.y = buf.readInt();
            ability.name = ByteBufUtils.readUTF8String(buf);
            ability.description = ByteBufUtils.readUTF8String(buf);
            miningAbilitiesList.add(ability);
        }
        for (Ability ability : miningAbilitiesList)
        {
            for (Ability ability2 : miningAbilitiesList)
            {
                if (ability.parentId == -1) continue;

                if (ability.parentId == ability2.id)
                {
                    ability.parentAbility = ability2;
                }
            }
        }
        for (int i = 0; i < w; i++)
        {
            Ability ability = new Ability();
            ability.id = buf.readInt();
            ability.parentId = buf.readInt();
            ability.state = Ability.State.values()[buf.readInt()];
            ability.colour = new Color(buf.readInt());
            ability.textureX = buf.readInt();
            ability.textureY = buf.readInt();
            ability.width = buf.readInt();
            ability.height = buf.readInt();
            ability.x = buf.readInt();
            ability.y = buf.readInt();
            ability.name = ByteBufUtils.readUTF8String(buf);
            ability.description = ByteBufUtils.readUTF8String(buf);
            woodcuttingAbilitiesList.add(ability);
        }
        for (Ability ability : woodcuttingAbilitiesList)
        {
            for (Ability ability2 : woodcuttingAbilitiesList)
            {
                if (ability.parentId == -1) continue;

                if (ability.parentId == ability2.id)
                {
                    ability.parentAbility = ability2;
                }
            }
        }

        generalAbilities.abilities = generalAbilitiesList;
        combatAbilities.abilities = combatAbilitiesList;
        miningAbilities.abilities = miningAbilitiesList;
        woodcuttingAbilities.abilities = woodcuttingAbilitiesList;

        this.id = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(generalAbilities.abilities.size());
        buf.writeInt(combatAbilities.abilities.size());
        buf.writeInt(miningAbilities.abilities.size());
        buf.writeInt(woodcuttingAbilities.abilities.size());

        buf.writeInt(generalAbilities.id);
        buf.writeInt(combatAbilities.id);
        buf.writeInt(miningAbilities.id);
        buf.writeInt(woodcuttingAbilities.id);

        ByteBufUtils.writeUTF8String(buf, generalAbilities.groupName);
        ByteBufUtils.writeUTF8String(buf, combatAbilities.groupName);
        ByteBufUtils.writeUTF8String(buf, miningAbilities.groupName);
        ByteBufUtils.writeUTF8String(buf, woodcuttingAbilities.groupName);

        for (Ability ability : generalAbilities.abilities)
        {
            buf.writeInt(ability.id);
            if (ability.parentAbility != null) buf.writeInt(ability.parentAbility.id);
            else buf.writeInt(-1);
            buf.writeInt(ability.state.ordinal());
            buf.writeInt(ability.colour.getRGB());
            buf.writeInt(ability.textureX);
            buf.writeInt(ability.textureY);
            buf.writeInt(ability.width);
            buf.writeInt(ability.height);
            buf.writeInt(ability.x);
            buf.writeInt(ability.y);
            ByteBufUtils.writeUTF8String(buf, ability.name);
            ByteBufUtils.writeUTF8String(buf, ability.description);
        }
        for (Ability ability : combatAbilities.abilities)
        {
            buf.writeInt(ability.id);
            if (ability.parentAbility != null) buf.writeInt(ability.parentAbility.id);
            else buf.writeInt(-1);
            buf.writeInt(ability.state.ordinal());
            buf.writeInt(ability.colour.getRGB());
            buf.writeInt(ability.textureX);
            buf.writeInt(ability.textureY);
            buf.writeInt(ability.width);
            buf.writeInt(ability.height);
            buf.writeInt(ability.x);
            buf.writeInt(ability.y);
            ByteBufUtils.writeUTF8String(buf, ability.name);
            ByteBufUtils.writeUTF8String(buf, ability.description);
        }
        for (Ability ability : miningAbilities.abilities)
        {
            buf.writeInt(ability.id);
            if (ability.parentAbility != null) buf.writeInt(ability.parentAbility.id);
            else buf.writeInt(-1);
            buf.writeInt(ability.state.ordinal());
            buf.writeInt(ability.colour.getRGB());
            buf.writeInt(ability.textureX);
            buf.writeInt(ability.textureY);
            buf.writeInt(ability.width);
            buf.writeInt(ability.height);
            buf.writeInt(ability.x);
            buf.writeInt(ability.y);
            ByteBufUtils.writeUTF8String(buf, ability.name);
            ByteBufUtils.writeUTF8String(buf, ability.description);
        }
        for (Ability ability : woodcuttingAbilities.abilities)
        {
            buf.writeInt(ability.id);
            if (ability.parentAbility != null) buf.writeInt(ability.parentAbility.id);
            else buf.writeInt(-1);
            buf.writeInt(ability.state.ordinal());
            buf.writeInt(ability.colour.getRGB());
            buf.writeInt(ability.textureX);
            buf.writeInt(ability.textureY);
            buf.writeInt(ability.width);
            buf.writeInt(ability.height);
            buf.writeInt(ability.x);
            buf.writeInt(ability.y);
            ByteBufUtils.writeUTF8String(buf, ability.name);
            ByteBufUtils.writeUTF8String(buf, ability.description);
        }

        buf.writeInt(this.id);
    }

    public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<AbilitiesMessage, IMessage>
    {
        public IMessage onMessage(AbilitiesMessage message, MessageContext ctx)
        {
            Entity entity = null;

            if ((ctx.side.isClient()) && (Blocklings.proxy.getPlayer(ctx) != null))
            {
                entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(message.id);

                if (entity instanceof EntityBlockling)
                {
                    EntityBlockling blockling = (EntityBlockling) entity;

                    blockling.generalAbilities = message.generalAbilities;
                    blockling.combatAbilities = message.combatAbilities;
                    blockling.miningAbilities = message.miningAbilities;
                    blockling.woodcuttingAbilities = message.woodcuttingAbilities;
                }
            }
            else if (ctx.side.isServer() && Blocklings.proxy.getPlayer(ctx) != null)
            {
                entity = Blocklings.proxy.getPlayer(ctx).world.getEntityByID(message.id);

                if ((entity instanceof EntityBlockling))
                {
                    EntityBlockling blockling = (EntityBlockling) entity;

                    blockling.generalAbilities = message.generalAbilities;
                    blockling.combatAbilities = message.combatAbilities;
                    blockling.miningAbilities = message.miningAbilities;
                    blockling.woodcuttingAbilities = message.woodcuttingAbilities;
                }
            }

            return null;
        }
    }
}