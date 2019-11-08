package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeHooks;
import willr27.blocklings.abilities.Abilities;
import willr27.blocklings.block.BlockUtil;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.AbstractInventory;
import willr27.blocklings.inventory.EquipmentInventory;
import willr27.blocklings.inventory.Utilities.ChestInventory;
import willr27.blocklings.inventory.Utilities.FurnaceInventory;
import willr27.blocklings.utilities.Utility;
import willr27.blocklings.utilities.UtilityManager;

public class BlocklingAutosmeltGoal extends WaterAvoidingRandomWalkingGoal
{
    private BlocklingEntity blockling;

    private EquipmentInventory inv;
    private ChestInventory inv1;
    private ChestInventory inv2;
    private FurnaceInventory finv1;
    private FurnaceInventory finv2;

    public BlocklingAutosmeltGoal(BlocklingEntity blockling)
    {
        super(blockling, 1.0);
//        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;

        inv = blockling.equipmentInventory;
    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.AUTOMSELT_ID)) return false;

        UtilityManager utilityManager = blockling.getUtilityManager();
        if (!utilityManager.hasUtility(Utility.FURNACE)) return false;

        if (blockling.getThousandTimer() % 100 != 0) return false;

        inv1 = null;
        inv2 = null;
        finv1 = null;
        finv2 = null;

        AbstractInventory i1 = utilityManager.getInventory1();
        if (i1 != null)
        {
            if (i1 instanceof ChestInventory) inv1 = (ChestInventory) i1;
            if (i1 instanceof FurnaceInventory) finv1 = (FurnaceInventory) i1;
        }

        AbstractInventory i2 = utilityManager.getInventory2();
        if (i2 != null)
        {
            if (i2 instanceof ChestInventory) inv2 = (ChestInventory) i2;
            if (i2 instanceof FurnaceInventory) finv2 = (FurnaceInventory) i2;
        }

        if (finv1 != null)
        {
            finv1.setInventorySlotContents(FurnaceInventory.OUTPUT_SLOT, inv.addItem(finv1.getStackInSlot(FurnaceInventory.OUTPUT_SLOT)));
            if (inv2 != null) finv1.setInventorySlotContents(FurnaceInventory.OUTPUT_SLOT, inv2.addItem(finv1.getStackInSlot(FurnaceInventory.OUTPUT_SLOT)));

            if (ForgeHooks.getBurnTime(finv1.getStackInSlot(FurnaceInventory.FUEL_SLOT)) <= 0)
            {
                finv1.setInventorySlotContents(FurnaceInventory.FUEL_SLOT, inv.addItem(finv1.getStackInSlot(FurnaceInventory.FUEL_SLOT)));
                if (inv2 != null) finv1.setInventorySlotContents(FurnaceInventory.FUEL_SLOT, inv2.addItem(finv1.getStackInSlot(FurnaceInventory.FUEL_SLOT)));
            }

            findOreForFurnace(finv1);
            findFuelForFurnace(finv1);
        }

        if (finv2 != null)
        {
            finv2.setInventorySlotContents(FurnaceInventory.OUTPUT_SLOT, inv.addItem(finv2.getStackInSlot(FurnaceInventory.OUTPUT_SLOT)));
            if (inv1 != null) finv2.setInventorySlotContents(FurnaceInventory.OUTPUT_SLOT, inv1.addItem(finv2.getStackInSlot(FurnaceInventory.OUTPUT_SLOT)));

            if (ForgeHooks.getBurnTime(finv2.getStackInSlot(FurnaceInventory.FUEL_SLOT)) <= 0)
            {
                finv2.setInventorySlotContents(FurnaceInventory.FUEL_SLOT, inv.addItem(finv2.getStackInSlot(FurnaceInventory.FUEL_SLOT)));
                if (inv1 != null) finv2.setInventorySlotContents(FurnaceInventory.FUEL_SLOT, inv1.addItem(finv2.getStackInSlot(FurnaceInventory.FUEL_SLOT)));
            }

            findOreForFurnace(finv2);
            findFuelForFurnace(finv2);
        }

        return super.shouldExecute();
    }

    private void findFuelForFurnace(FurnaceInventory finv)
    {
        Item fuelItem = finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getItem();
        for (int i = EquipmentInventory.INVENTORY_START_SLOT; i < inv.getSizeInventory() && finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getCount() < finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getMaxStackSize(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();

            if (fuelItem == Items.AIR)
            {
                if (ForgeHooks.getBurnTime(stack) > 0)
                {
                    fuelItem = item;
                }
            }

            if (item == fuelItem)
            {
                inv.setInventorySlotContents(i, finv.addItem(stack, FurnaceInventory.FUEL_SLOT));
            }
        }

        if (inv1 != null)
        {
            for (int i = 0; i < inv1.getSizeInventory() && finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getCount() < finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getMaxStackSize(); i++)
            {
                ItemStack stack = inv1.getStackInSlot(i);
                Item item = stack.getItem();

                if (fuelItem == Items.AIR)
                {
                    if (ForgeHooks.getBurnTime(stack) > 0)
                    {
                        fuelItem = item;
                    }
                }

                if (item == fuelItem)
                {
                    inv1.setInventorySlotContents(i, finv.addItem(stack, FurnaceInventory.FUEL_SLOT));
                }
            }
        }
        else if (inv2 != null)
        {
            for (int i = 0; i < inv2.getSizeInventory() && finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getCount() < finv.getStackInSlot(FurnaceInventory.FUEL_SLOT).getMaxStackSize(); i++)
            {
                ItemStack stack = inv2.getStackInSlot(i);
                Item item = stack.getItem();

                if (fuelItem == Items.AIR)
                {
                    if (ForgeHooks.getBurnTime(stack) > 0)
                    {
                        fuelItem = item;
                    }
                }

                if (item == fuelItem)
                {
                    inv2.setInventorySlotContents(i, finv.addItem(stack, FurnaceInventory.FUEL_SLOT));
                }
            }
        }
    }

    private void findOreForFurnace(FurnaceInventory finv)
    {
        Item itemToSmelt = finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getItem();
        for (int i = EquipmentInventory.INVENTORY_START_SLOT; i < inv.getSizeInventory() && finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getCount() < finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getMaxStackSize(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();

            if (itemToSmelt == Items.AIR)
            {
                if (BlockUtil.isOre(item) && FurnaceInventory.isSmeltable(blockling.world, stack))
                {
                    if (blockling.abilityManager.isBought(Abilities.Mining.AUTOSMELT_WHITELIST))
                    {
                        Block ore = BlockUtil.getOre(item);
                        if (ore == null || blockling.aiManager.getWhitelist(AIManager.AUTOMSELT_ID, AIManager.AUTOMSELT_ORES_WHITELIST_ID).isInBlacklist(ore))
                        {
                            continue;
                        }
                    }

                    itemToSmelt = item;
                }
            }

            if (item == itemToSmelt)
            {
                inv.setInventorySlotContents(i, finv.addItem(stack, FurnaceInventory.INPUT_SLOT));
            }
        }

        if (inv1 != null)
        {
            for (int i = 0; i < inv1.getSizeInventory() && finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getCount() < finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getMaxStackSize(); i++)
            {
                ItemStack stack = inv1.getStackInSlot(i);
                Item item = stack.getItem();

                if (itemToSmelt == Items.AIR)
                {
                    if (BlockUtil.isOre(item) && FurnaceInventory.isSmeltable(blockling.world, stack))
                    {
                        if (blockling.abilityManager.isBought(Abilities.Mining.AUTOSMELT_WHITELIST))
                        {
                            Block ore = BlockUtil.getOre(item);
                            if (ore == null || blockling.aiManager.getWhitelist(AIManager.AUTOMSELT_ID, AIManager.AUTOMSELT_ORES_WHITELIST_ID).isInBlacklist(ore))
                            {
                                continue;
                            }
                        }

                        itemToSmelt = item;
                    }
                }

                if (item == itemToSmelt)
                {
                    inv1.setInventorySlotContents(i, finv.addItem(stack, FurnaceInventory.INPUT_SLOT));
                }
            }
        }
        else if (inv2 != null)
        {
            for (int i = 0; i < inv2.getSizeInventory() && finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getCount() < finv.getStackInSlot(FurnaceInventory.INPUT_SLOT).getMaxStackSize(); i++)
            {
                ItemStack stack = inv2.getStackInSlot(i);
                Item item = stack.getItem();

                if (itemToSmelt == Items.AIR)
                {
                    if (BlockUtil.isOre(item) && FurnaceInventory.isSmeltable(blockling.world, stack))
                    {
                        if (blockling.abilityManager.isBought(Abilities.Mining.AUTOSMELT_WHITELIST))
                        {
                            Block ore = BlockUtil.getOre(item);
                            if (ore == null || blockling.aiManager.getWhitelist(AIManager.AUTOMSELT_ID, AIManager.AUTOMSELT_ORES_WHITELIST_ID).isInBlacklist(ore))
                            {
                                continue;
                            }
                        }

                        itemToSmelt = item;
                    }
                }

                if (item == itemToSmelt)
                {
                    inv2.setInventorySlotContents(i, finv.addItem(stack, FurnaceInventory.INPUT_SLOT));
                }
            }
        }
    }
}
