package com.blocklings.whitelist;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.network.NetworkHelper;
import com.blocklings.network.messages.whitelist.BlocklingWhitelistAllMessage;
import com.blocklings.network.messages.whitelist.BlocklingWhitelistSingleMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class BlocklingWhitelist extends Whitelist
{
    public final UUID id;

    private EntityBlockling blockling;

    public BlocklingWhitelist(EntityBlockling blockling)
    {
        this.blockling = blockling;
        id = UUID.randomUUID();
    }

    public BlocklingWhitelist(EntityBlockling blockling, UUID id)
    {
        this.blockling = blockling;
        this.id = id;
    }

    public BlocklingWhitelist(EntityBlockling blockling, Map whitelist)
    {
        this(blockling);
        clear();
        putAll(whitelist);
    }

    public BlocklingWhitelist(EntityBlockling blockling, UUID id, Map whitelist)
    {
        this(blockling, id);
        clear();
        putAll(whitelist);
    }

    public boolean isWhitelisted(Entity entity)
    {
        return get(EntityList.getKey(entity));
    }

    public boolean isBlacklisted(Entity entity)
    {
        return !isWhitelisted(entity);
    }

    @Override
    public void setEntry(ResourceLocation entry, boolean value)
    {
        setEntry(entry, value, true);
    }

    public void setEntry(ResourceLocation entry, boolean value, boolean sync)
    {
        super.setEntry(entry, value);
        if (sync) NetworkHelper.sync(blockling.world, new BlocklingWhitelistSingleMessage(id, entry, value, blockling.getEntityId()));
    }

    @Override
    public void toggleEntry(ResourceLocation entry)
    {
        toggleEntry(entry, true);
    }

    public void toggleEntry(ResourceLocation entry, boolean sync)
    {
        setEntry(entry, !get(entry), sync);
    }

    @Override
    public void setAll(boolean value)
    {
        setAll(value, true);
    }

    public void setAll(boolean value, boolean sync)
    {
        for (ResourceLocation entry : keySet())
        {
            setEntry(entry, value, false);
        }
        if (sync) NetworkHelper.sync(blockling.world, new BlocklingWhitelistAllMessage(id, this, blockling.getEntityId()));
    }

    @Override
    public void toggleAll()
    {
        toggleAll(true);
    }

    public void toggleAll(boolean sync)
    {
        for (ResourceLocation entry : keySet())
        {
            toggleEntry(entry, false);
        }
        if (sync) NetworkHelper.sync(blockling.world, new BlocklingWhitelistAllMessage(id, this, blockling.getEntityId()));
    }
}
