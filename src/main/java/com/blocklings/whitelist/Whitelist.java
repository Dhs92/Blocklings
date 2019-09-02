package com.blocklings.whitelist;

import net.minecraft.util.ResourceLocation;

import java.util.TreeMap;

public abstract class Whitelist extends TreeMap<ResourceLocation, Boolean>
{
    public boolean isWhitelisted(ResourceLocation entry)
    {
        return get(entry);
    }

    public boolean isBlacklisted(ResourceLocation entry)
    {
        return !isWhitelisted(entry);
    }

    public void setEntry(ResourceLocation entry, boolean value)
    {
        replace(entry, value);
    }

    public void toggleEntry(ResourceLocation entry)
    {
        setEntry(entry, !get(entry));
    }

    public void setAll(boolean value)
    {
        for (ResourceLocation entry : keySet())
        {
            setEntry(entry, value);
        }
    }

    public void toggleAll()
    {
        for (ResourceLocation entry : keySet())
        {
            toggleEntry(entry);
        }
    }
}
