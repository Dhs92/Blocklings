package com.blocklings.gui.containers;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.inventory.inventories.InventoryBlockling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEquipmentBlockling extends Container
{
    private EntityBlockling blockling;
    private InventoryBlockling blocklingInv;
    private InventoryPlayer playerInv;

    private int playerInventoryX = 36;
    private int playerInventoryY = 84;

    public ContainerEquipmentBlockling(EntityBlockling blockling, InventoryPlayer playerInv, InventoryBlockling blocklingInv)
    {
        this.blockling = blockling;
        this.blocklingInv = blocklingInv;
        this.playerInv = playerInv;

        bindBlocklingInventory();
        bindPlayerInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    private void bindBlocklingInventory()
    {
        addSlotToContainer(new Slot(blocklingInv, InventoryBlockling.UPGRADE_MATERIAL_SLOT, 65, 16));
        addSlotToContainer(new Slot(blocklingInv, InventoryBlockling.MAIN_HAND_SLOT, 83, 44));
        addSlotToContainer(new Slot(blocklingInv, InventoryBlockling.OFF_HAND_SLOT, 47, 44));
    }

    private void bindPlayerInventory()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, playerInventoryX + (j * 18), playerInventoryY + (i * 18)));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(playerInv, i, playerInventoryX + (i * 18), playerInventoryY + 58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack = slot.getStack();
            stack = itemStack.copy();

            if (slotIndex == InventoryBlockling.UPGRADE_MATERIAL_SLOT || slotIndex == InventoryBlockling.MAIN_HAND_SLOT || slotIndex == InventoryBlockling.OFF_HAND_SLOT)
            {
                if (!this.mergeItemStack(itemStack, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
//                if (ItemHelper.isUpgradeMaterial(itemStack))
//                {
//                    int stackSize = itemStack.getCount();
//                    itemStack.setCount(1);
//
//                    if (!this.mergeItemStack(itemStack, InventoryBlockling.UPGRADE_MATERIAL_SLOT, InventoryBlockling.UPGRADE_MATERIAL_SLOT + 1, false))
//                    {
//                        itemStack.setCount(stackSize);
//                        return ItemStack.EMPTY;
//                    }
//                    else
//                    {
//                        itemStack.setCount(stackSize - 1);
//                        return ItemStack.EMPTY;
//                    }
//                }
//                else
                if (!this.mergeItemStack(itemStack, 0, 3, false))
                {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (!blockling.world.isRemote)
        {
            ItemStack slotStack = slot.getStack();
            slot.inventory.setInventorySlotContents(slot.getSlotIndex(), slotStack);
        }

        return stack;
    }
}
