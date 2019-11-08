package willr27.blocklings.inventory.Utilities;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import willr27.blocklings.abilities.Abilities;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.utilities.Utility;

import java.util.Optional;

public class FurnaceInventory extends UtilityInventory
{
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private boolean burning;
    private int smeltTime;
    private int burnTime;
    private int smeltCount;
    private int burnCount;

    public FurnaceInventory(BlocklingEntity blockling, int utilityIndex)
    {
        super(blockling, 3, Utility.FURNACE, utilityIndex);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        super.setInventorySlotContents(index, stack);

        if (index == INPUT_SLOT)
        {
            ItemStack inputStack = getStackInSlot(INPUT_SLOT);
            if (canSmelt(inputStack))
            {
                smeltTime = getSmeltTime(inputStack);
            }
            else
            {
                smeltTime = -1;
                smeltCount = 0;
            }
        }

        tryStartBurning();
    }

    public void tick()
    {
        if (burning)
        {
            burnCount++;

            if (smeltTime > -1)
            {
                ItemStack inputStack = getStackInSlot(INPUT_SLOT);
                if (!inputStack.isEmpty())
                {
                    smeltCount++;

                    if (smeltCount >= smeltTime)
                    {
                        if (!world.isRemote)
                        {
                            IRecipe recipe = getRecipe(inputStack).get();
                            ItemStack resultStack = recipe.getRecipeOutput();
                            addItem(resultStack.copy(), OUTPUT_SLOT);
                            decrStackSize(INPUT_SLOT, 1);

                            if (blockling.abilityManager.isBought(Abilities.Mining.AUTOSMELT_XP))
                            {
                                if (recipe instanceof AbstractCookingRecipe)
                                {
                                    float xp = ((AbstractCookingRecipe)recipe).getExperience();
                                    blockling.getStats().miningXp.incBaseValue(xp);
                                }
                            }
                        }

                        smeltCount = 0;
                    }
                }
            }

            if (burnCount > burnTime)
            {
                burning = false;
                burnTime = 0;
                burnCount = 0;

                tryStartBurning();

                if (!burning)
                {
                    smeltCount = 0;
                }
            }
        }
    }

    /*
    ** Attempts to burn an item of fuel.
     */
    private void tryStartBurning()
    {
        if (burning)
        {
            return;
        }

        for (ItemStack stack : stacks)
        {
            if (stack == null)
            {
                return;
            }
        }

        ItemStack fuelStack = getStackInSlot(FUEL_SLOT);
        if (fuelStack.isEmpty())
        {
            return;
        }

        ItemStack inputStack = getStackInSlot(INPUT_SLOT);
        if (canSmelt(inputStack))
        {
            burning = true;
            burnTime = ForgeHooks.getBurnTime(fuelStack);
            burnCount = 0;

            if (!world.isRemote)
            {
                if (fuelStack.hasContainerItem())
                {
                    setInventorySlotContents(FUEL_SLOT, fuelStack.getContainerItem());
                }
                else
                {
                    int amount = 1;
                    if (blockling.abilityManager.isBought(Abilities.Mining.FUEL_EFFICIENT))
                    {
                        if (blockling.random.nextInt(2) == 0)
                        {
                            amount = 0;
                        }
                    }
                    decrStackSize(FUEL_SLOT, amount);
                }
            }
        }
    }

    /*
    ** Can the current stack be smelted at least once?
     */
    private boolean canSmelt(ItemStack stack)
    {
        FurnaceRecipe recipe = getRecipe(stack).orElse(null);

        if (recipe == null)
        {
            return false;
        }

        ItemStack resultStack = recipe.getRecipeOutput();
        if (!couldAddItem(resultStack, OUTPUT_SLOT))
        {
            return false;
        }

        return true;
    }

    private Optional<FurnaceRecipe> getRecipe(ItemStack stack)
    {
        return getRecipe(world, stack);
    }

    private static Optional<FurnaceRecipe> getRecipe(World world, ItemStack stack)
    {
        return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world);
    }

    private int getSmeltTime(ItemStack stack)
    {
        return  getSmeltTime(world, stack);
    }

    private static int getSmeltTime(World world, ItemStack stack)
    {
        return getRecipe(world, stack).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean isSmeltable(World world, ItemStack stack)
    {
        return getRecipe(world, stack).isPresent();
    }

    public boolean isBurning()
    {
        return burning;
    }

    public float getSmeltProgress()
    {
        return smeltCount / (float) smeltTime;
    }

    public float getBurnProgress()
    {
        return burnCount / (float) burnTime;
    }
}
