package willr27.blocklings.gui.container.containers.utilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.container.containers.PlayerContainerListener;
import willr27.blocklings.gui.slots.CraftingTableSlot;
import willr27.blocklings.gui.slots.FurnaceFuelSlot;
import willr27.blocklings.gui.slots.FurnaceOutputSlot;
import willr27.blocklings.inventory.Utilities.CraftingTableInventory;

public class UtilityContainer extends Container
{
    private static final int playerInvX = 8;
    private static final int playerInvY = 74;

    private PlayerInventory playerInv;
    private BlocklingEntity blockling;

    private PlayerEntity player;

    public UtilityContainer(int id, PlayerInventory playerInv)
    {
        super(null, id);
    }

    public UtilityContainer(int id, PlayerEntity player, BlocklingEntity blockling)
    {
        this(id, player.inventory);
        this.player = player;
        this.playerInv = player.inventory;
        this.blockling = blockling;

        if (!player.world.isRemote)
        {
            addListener(new PlayerContainerListener((ServerPlayerEntity) player));
        }

        switch (blockling.getUtilityManager().getUtility(blockling.getGuiInfo().utility))
        {
            case CHEST: bindChest(); break;
            case CRAFTING_TABLE: bindCraftingTable(); break;
            case FURNACE: bindFurnace(); break;
        }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }

    private void bindChest()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, playerInvX + (j * 18), playerInvY + (i * 18)));
            }
        }
        for (int i = 0; i < 9; i++)
        {
            addSlot(new Slot(playerInv, i, playerInvX + (i * 18), playerInvY + 58));
        }

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility), j + i * 9, playerInvX + (j * 18), -2 + (i * 18)));
            }
        }
    }

    private void bindCraftingTable()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, playerInvX + (j * 18), playerInvY + (i * 18)));
            }
        }
        for (int i = 0; i < 9; i++)
        {
            addSlot(new Slot(playerInv, i, playerInvX + (i * 18), playerInvY + 58));
        }

        addSlot(new CraftingTableSlot((CraftingTableInventory) blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility), 9, 124, 25));

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                addSlot(new Slot(blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility), j + i * 3, playerInvX + (j * 18) + 22, 7 + (i * 18)));
            }
        }
    }

    private void bindFurnace()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, playerInvX + (j * 18), playerInvY + (i * 18)));
            }
        }
        for (int i = 0; i < 9; i++)
        {
            addSlot(new Slot(playerInv, i, playerInvX + (i * 18), playerInvY + 58));
        }

        addSlot(new Slot(blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility), 0, playerInvX + 48, playerInvY - 67));
        addSlot(new FurnaceFuelSlot(blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility), 1, playerInvX + 48, playerInvY - 31));
        addSlot(new FurnaceOutputSlot(blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility), 2, playerInvX + 108, playerInvY - 49));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);
        return stack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
