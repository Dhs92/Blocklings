package willr27.blocklings.utilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.Utilities.ChestInventory;
import willr27.blocklings.inventory.Utilities.CraftingTableInventory;
import willr27.blocklings.inventory.Utilities.FurnaceInventory;
import willr27.blocklings.inventory.Utilities.UtilityInventory;

import java.util.function.BiFunction;

public enum Utility
{
    CHEST("Chest", ChestInventory::new, Items.CHEST),
    CRAFTING_TABLE("Crafting Table", CraftingTableInventory::new, Items.CRAFTING_TABLE),
    FURNACE("Furnace", FurnaceInventory::new, Items.FURNACE);

    public String name;
    public BiFunction<BlocklingEntity, Integer, ? extends UtilityInventory> inventory;
    public Item[] items;

    Utility(String name, BiFunction<BlocklingEntity, Integer, ? extends UtilityInventory> inventory, Item... items)
    {
        this.name = name;
        this.inventory = inventory;
        this.items = items;
    }

    public static Utility getUtility(ItemStack stack)
    {
        return getUtility(stack.getItem());
    }

    public static Utility getUtility(Item item)
    {
        for (Utility utility : Utility.values())
        {
            for (Item testItem : utility.items)
            {
                if (item == testItem)
                {
                    return utility;
                }
            }
        }

        return null;
    }
}
