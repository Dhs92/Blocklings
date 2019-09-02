package com.blocklings.gui.containers;

import com.blocklings.inventory.inventories.InventoryBlockling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlank extends Container
{
    public ContainerBlank()
    {

    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return false;
    }
}
