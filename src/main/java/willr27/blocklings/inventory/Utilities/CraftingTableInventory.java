package willr27.blocklings.inventory.Utilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.utilities.Utility;

import java.util.Optional;

public class CraftingTableInventory extends UtilityInventory
{
    public CraftingTableInventory(BlocklingEntity blockling, int utilityIndex)
    {
        super(blockling, 10, Utility.CRAFTING_TABLE, utilityIndex);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        super.setInventorySlotContents(index, stack);
        onCraftingMatrixChanged();
    }

    private void onCraftingMatrixChanged()
    {
        CraftingInventory craftingInv = new CraftingInventory(new Container(null, 0)
        {
            @Override
            public boolean canInteractWith(PlayerEntity playerIn)
            {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = getStackInSlot(i);
            if (stack == null) return;
            craftingInv.setInventorySlotContents(i, stack);
        }

        Optional<ICraftingRecipe> recipe = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInv, world);

        if (recipe.isPresent())
        {
            ICraftingRecipe reci = recipe.get();
            ItemStack stack = reci.getCraftingResult(craftingInv);
            if (!ItemStack.areItemStacksEqual(getStackInSlot(9), stack)) setInventorySlotContents(9, stack);
        }
        else
        {
            if (getStackInSlot(9) != null && !getStackInSlot(9).isEmpty()) setInventorySlotContents(9, ItemStack.EMPTY);
        }
    }
}
