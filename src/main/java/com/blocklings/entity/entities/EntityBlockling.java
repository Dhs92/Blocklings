package com.blocklings.entity.entities;

import com.blocklings.Blocklings;
import com.blocklings.entity.ai.BlocklingAIFollowOwner;
import com.blocklings.entity.ai.BlocklingAISit;
import com.blocklings.entity.ai.BlocklingAIWander;
import com.blocklings.inventory.inventories.InventoryBlockling;
import com.blocklings.item.ItemHelper;
import com.blocklings.network.NetworkHelper;
import com.blocklings.network.messages.BlocklingTypeMessage;
import com.blocklings.network.messages.CurrentGuiTabMessage;
import com.blocklings.network.messages.OpenGuiMessage;
import com.blocklings.network.messages.StateMessage;
import com.blocklings.util.*;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityBlockling extends EntityTameable
{
    private BlocklingStats blocklingStats;

    /* Single inventory containing all items */
    private InventoryBlockling inv;

    /* Which gui tab is currently selected */
    private Tab currentGuiTab;

    private BlocklingType blocklingType;

    private State state;
    private boolean[] blocklingTasks;

    private BlocklingAISit aiSit;
    private BlocklingAIFollowOwner aiFollowOwner;
    private BlocklingAIWander aiWander;

    // Base stats
    public static final double BASE_MAX_HEALTH = 10;
    public static final double BASE_MOVEMENT_SPEED = 0.5;
    public static final double BASE_ATTACK_DAMAGE = 1.0;

    public EntityBlockling(World world)
    {
        super(world);

        setSize(blocklingStats.getScale(), blocklingStats.getScale());
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        setupInventory();

        blocklingStats = new BlocklingStats(this);
        blocklingStats.setScale(1.0f, false);

        currentGuiTab = Tab.TASKS;
        blocklingType = BlocklingType.blocklingTypes.get(new Random().nextInt(11));

        state = State.WANDER;
        blocklingTasks = new boolean[Task.values().length];
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BASE_MAX_HEALTH);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_MOVEMENT_SPEED);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE);
    }


    @Override
    protected void initEntityAI()
    {
        aiSit = new BlocklingAISit(this);
        aiFollowOwner = new BlocklingAIFollowOwner(this);
        aiWander = new BlocklingAIWander(this);

        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, aiSit);
        tasks.addTask(7, aiFollowOwner);
        tasks.addTask(8, aiWander);
        tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(10, new EntityAILookIdle(this));
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entity)
    {
        return null;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        eatMaterial();

        if (!world.isRemote && rand.nextInt(30) == 0)
        {
            playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.7f);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        boolean isMainHand = hand.equals(EnumHand.MAIN_HAND);

        if (!isMainHand)
        {
            return super.processInteract(player, hand);
        }

        if (!player.isSneaking())
        {
            if (ItemHelper.isFlower(item))
            {
                if (!isTamed())
                {
                    if (!player.capabilities.isCreativeMode) stack.shrink(1);
                    if (!world.isRemote)
                    {
                        playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.3f, 1.5f);
                        if (rand.nextInt(3) == 0)
                        {
                            setTamed(player);
                        }
                        else
                        {
                            playTameEffect(false);
                            world.setEntityState(this, (byte) 6);
                        }
                    }
                }

            }
            else if (ItemHelper.isEquipable(item))
            {
                if (!world.isRemote) setHeldItem(EnumHand.MAIN_HAND, stack);
            }
            else
            {
                if (!world.isRemote)
                {
                    setBlocklingType(BlocklingType.blocklingTypes.get(new Random().nextInt(11)));
                }
            }
        }
        else // Sneaking
        {
            if (ItemHelper.isEquipable(item))
            {
                if (!world.isRemote) setHeldItem(EnumHand.OFF_HAND, stack);
            }
            else
            {
                openGui(player, false);
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public ItemStack getHeldItem(EnumHand hand)
    {
        if (hand == EnumHand.MAIN_HAND)
        {
            return getHeldItemMainhand();
        }
        else if (hand == EnumHand.OFF_HAND)
        {
            return getHeldItemOffhand();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getHeldItemOffhand()
    {
        return inv.getStackInSlot(InventoryBlockling.OFF_HAND_SLOT);
    }

    @Override
    public ItemStack getHeldItemMainhand()
    {
        return inv.getStackInSlot(InventoryBlockling.MAIN_HAND_SLOT);
    }

    @Override
    public void setHeldItem(EnumHand hand, ItemStack stack)
    {
        int index = hand == EnumHand.MAIN_HAND ? InventoryBlockling.MAIN_HAND_SLOT : InventoryBlockling.OFF_HAND_SLOT;
        inv.setInventorySlotContents(index, stack);
    }

    private void eatMaterial()
    {
        if (!tryEatMaterial())
        {
            eatTimer = -1;
        }
    }

    private int eatTimer = -1;
    private boolean tryEatMaterial()
    {
        ItemStack stack = inv.getStackInSlot(InventoryBlockling.UPGRADE_MATERIAL_SLOT);
        if (stack.isEmpty() || world.isRemote)
        {
            return false;
        }

        if (eatTimer == -1)
        {
            eatTimer = 20;
        }
        else if (eatTimer == 0)
        {
            BlocklingType blocklingType = BlocklingType.getTypeFromItemStack(stack);
            if (blocklingType == null || this.blocklingType == blocklingType)
            {
                return false;
            }

            ItemStack newStack = stack.copy();
            newStack.shrink(1);
            inv.setInventorySlotContents(InventoryBlockling.UPGRADE_MATERIAL_SLOT, newStack);
            if (rand.nextInt(4) == 0)
            {
                setBlocklingType(blocklingType);
            }
        }

        eatTimer--;
        return true;
    }

    private void setTamed(EntityPlayer player)
    {
        setTamedBy(player);
        navigator.clearPath();
        setAttackTarget(null);
        setState(State.FOLLOW);
        playTameEffect(true);
        world.setEntityState(this, (byte) 7);
        setCustomNameTag(!getCustomNameTag().equals("") ? getCustomNameTag() : "Blockling");
    }

    public void openGui(EntityPlayer player)
    {
        openGui(player, true);
    }
    public void openGui(EntityPlayer player, boolean sync)
    {
        if ((world.isRemote && currentGuiTab.client) || (!world.isRemote && !currentGuiTab.client))
        {
            player.openGui(Blocklings.instance, currentGuiTab.ordinal(), world, getEntityId(), 0, 0);
        }
        if (sync) NetworkHelper.sync(world, new OpenGuiMessage(getEntityId()));
    }

    public BlocklingStats getBlocklingStats()
    {
        return blocklingStats;
    }

    private void setupInventory()
    {
        InventoryBlockling invTemp = inv;
        inv = new InventoryBlockling(this, "Inventory");
        inv.setCustomName("Blockling Inventory");

        if (invTemp != null)
        {
            int slotsToCheck = Math.min(invTemp.getSizeInventory(), inv.getSizeInventory());

            for (int i = 0; i < slotsToCheck; i++)
            {
                ItemStack stack = invTemp.getStackInSlot(i);

                if (stack != null)
                {
                    inv.setInventorySlotContents(i, stack);
                }
            }
        }
    }
    public InventoryBlockling getInv()
    {
        return inv;
    }

    public Tab getCurrentGuiTab()
    {
        return currentGuiTab;
    }
    public void setCurrentGuiTab(Tab value)
    {
        setCurrentGuiTab(value, true);
    }
    public void setCurrentGuiTab(Tab value, boolean sync)
    {
        currentGuiTab = value;
        if (sync) NetworkHelper.sync(world, new CurrentGuiTabMessage(currentGuiTab, getEntityId()));
    }

    public BlocklingType getBlocklingType()
    {
        return blocklingType;
    }
    public void setBlocklingType(BlocklingType value)
    {
        setBlocklingType(value, true);
    }
    public void setBlocklingType(BlocklingType value, boolean sync)
    {
        blocklingType = value;
        if (sync) NetworkHelper.sync(world, new BlocklingTypeMessage(blocklingType, getEntityId()));
    }

    public State getState()
    {
        return state;
    }
    public void setState(State value)
    {
        setState(value, true);
    }
    public void setState(State value, boolean sync)
    {
        state = value;
        if (sync) NetworkHelper.sync(world, new StateMessage(state, getEntityId()));
    }

    public boolean isTaskActive(Task task)
    {
        return blocklingTasks[task.ordinal()];
    }
    public void toggleTask(Task task)
    {
        setTask(task, !blocklingTasks[task.ordinal()]);
    }
    public void setTask(Task task, boolean value)
    {
        setTask(task, value, true);
    }
    public void setTask(Task task, boolean value, boolean sync)
    {
        blocklingTasks[task.ordinal()] = value;
        if (sync) NetworkHelper.sync(world, new StateMessage(state, getEntityId()));
    }
}






/*
 * When first spawned:
 *
 * [SERVER] entityInit
 * [SERVER] constructor
 * [SERVER] writeSpawnData
 * [SERVER] writeEntityToNBT
 * [SERVER] readEntityFromNBT
 * [CLIENT] entityInit
 * [CLIENT] constructor
 * [CLIENT] readSpawnData
 */

/*
 * When spawned from then on:
 *
 * [SERVER] entityInit
 * [SERVER] constructor
 * [SERVER] readEntityFromNBT
 * [SERVER] writeEntityToNBT
 * [SERVER] writeSpawnData
 * [CLIENT] entityInit
 * [CLIENT] constructor
 * [CLIENT] readSpawnData
 */