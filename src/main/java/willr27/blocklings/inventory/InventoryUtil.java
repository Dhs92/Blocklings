package willr27.blocklings.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.Utilities.ChestInventory;

public class InventoryUtil
{
    public static class FindResult
    {
        public final AbstractInventory inv;
        public final int index;

        public FindResult(AbstractInventory inv, int index)
        {
            this.inv = inv;
            this.index = index;
        }
    }

    public static FindResult find(BlocklingEntity blockling, Item item)
    {
        int equip = blockling.equipmentInventory.find(item);
        if (equip != -1) return new FindResult(blockling.equipmentInventory, equip);

        AbstractInventory inv1 = blockling.getUtilityManager().getInventory1();
        if (inv1 instanceof ChestInventory)
        {
            ChestInventory chestInv1 = (ChestInventory) inv1;
            int chest = chestInv1.find(item);
            if (chest != -1) return new FindResult(chestInv1, chest);
        }

        AbstractInventory inv2 = blockling.getUtilityManager().getInventory2();
        if (inv2 instanceof ChestInventory)
        {
            ChestInventory chestInv2 = (ChestInventory) inv2;
            int chest = chestInv2.find(item);
            if (chest != -1) return new FindResult(chestInv2, chest);
        }

        return new FindResult(null, -1);
    }

    public static boolean take(BlocklingEntity blockling, Item item)
    {
        FindResult result = find(blockling, item);

        if (result.index != -1)
        {
            result.inv.decrStackSize(result.index, 1);
        }

        return result.index != -1;
    }

    public static ItemStack add(BlocklingEntity blockling, ItemStack stack)
    {
        stack = blockling.equipmentInventory.addItem(stack);

        AbstractInventory inv1 = blockling.getUtilityManager().getInventory1();
        if (inv1 instanceof ChestInventory)
        {
            ChestInventory chestInv1 = (ChestInventory) inv1;
            stack = chestInv1.addItem(stack);
        }

        AbstractInventory inv2 = blockling.getUtilityManager().getInventory2();
        if (inv2 instanceof ChestInventory)
        {
            ChestInventory chestInv2 = (ChestInventory) inv2;
            stack = chestInv2.addItem(stack);
        }

        return stack;
    }
}
