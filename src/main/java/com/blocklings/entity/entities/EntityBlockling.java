package com.blocklings.entity.entities;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityBlockling extends EntityTameable
{
    public EntityBlockling(World world)
    {
        super(world);

        setSize(1.0f, 1.0f);
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entity)
    {
        return null;
    }
}
