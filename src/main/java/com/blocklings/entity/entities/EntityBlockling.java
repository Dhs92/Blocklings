package com.blocklings.entity.entities;

import com.blocklings.Blocklings;
import com.blocklings.entity.ai.*;
import com.blocklings.gui.screens.configs.ConfigInfo;
import com.blocklings.inventory.inventories.InventoryBlockling;
import com.blocklings.item.ItemHelper;
import com.blocklings.network.NetworkHelper;
import com.blocklings.network.messages.*;
import com.blocklings.util.State;
import com.blocklings.util.Tab;
import com.blocklings.util.Task;
import com.blocklings.whitelist.BlocklingWhitelist;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class EntityBlockling extends EntityTameable
{
    private BlocklingStats blocklingStats;

    /* Single inventory containing all items */
    private InventoryBlockling inv;

    /* Which gui tab is currently selected */
    private Tab currentGuiTab;

    public boolean isInGui;

    private BlocklingType blocklingType;

    private State state;
    private boolean[] blocklingActiveTasks;
    private Task[] blocklingPrioritisedTasks;
    private Map<Task, EntityAIBase> tasksToAI;

    private BlocklingAISit aiSit;
    private BlocklingAIFollowOwner aiFollowOwner;
    private BlocklingAIWander aiWander;

    private BlocklingAIGuard aiGuard;
    private BlocklingAIHunt aiHunt;
    private BlocklingAITank aiTank;
    private BlocklingAIMine aiMine;
    private BlocklingAIChop aiChop;
    private BlocklingAIFarm aiFarm;

    public int targetResetTimer = 0;
    public int targetResetTimerMax = 20;

    private List<BlocklingWhitelist> whitelists;

    public ConfigInfo configInfo;

    // Base stats
    public static final double BASE_MAX_HEALTH = 10.0;
    public static final double BASE_ARMOUR = 10.0;
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
        blocklingStats.setScale(0.75f, false);
        blocklingStats.setCombatLevel(rand.nextInt(20), false);
        blocklingStats.setMiningLevel(rand.nextInt(20), false);
        blocklingStats.setWoodcuttingLevel(rand.nextInt(20), false);
        blocklingStats.setFarmingLevel(rand.nextInt(20), false);
        blocklingStats.setCombatXp(rand.nextInt(getXpUntilNextLevel(blocklingStats.getCombatLevel())), false);
        blocklingStats.setMiningXp(rand.nextInt(getXpUntilNextLevel(blocklingStats.getMiningLevel())), false);
        blocklingStats.setWoodcuttingXp(rand.nextInt(getXpUntilNextLevel(blocklingStats.getWoodcuttingLevel())), false);
        blocklingStats.setFarmingXp(rand.nextInt(getXpUntilNextLevel(blocklingStats.getFarmingLevel())), false);

        currentGuiTab = Tab.TASKS;
        blocklingType = BlocklingType.blocklingTypes.get(new Random().nextInt(11));

        state = State.WANDER;
        blocklingActiveTasks = new boolean[Task.values().length];
        blocklingPrioritisedTasks = new Task[Task.values().length];
        for (Task task : Task.values())
        {
            blocklingPrioritisedTasks[task.ordinal()] = task;
        }

        whitelists = new ArrayList<>();
        Map<ResourceLocation, Boolean> temp = new TreeMap<>();
        for (ResourceLocation entry : EntityList.getEntityNameList())
        {
            Class clazz = EntityList.getClass(entry);
            if (clazz != null && EntityLivingBase.class.isAssignableFrom(clazz)) temp.put(entry, rand.nextInt(2) == 0);
        }
        whitelists.add(new BlocklingWhitelist(this, Task.GUARD.whitelistId, temp));
        whitelists.add(new BlocklingWhitelist(this, Task.HUNT.whitelistId, temp));
        whitelists.add(new BlocklingWhitelist(this, Task.TANK.whitelistId, temp));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BASE_MAX_HEALTH);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(BASE_ARMOUR);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_MOVEMENT_SPEED);

        blocklingStats.updateBlocklingTypeStats();
    }

    @Override
    protected void initEntityAI()
    {
        aiSit = new BlocklingAISit(this);
        aiFollowOwner = new BlocklingAIFollowOwner(this);
        aiWander = new BlocklingAIWander(this);

        aiGuard = new BlocklingAIGuard(this);
        aiHunt = new BlocklingAIHunt(this);
        aiTank = new BlocklingAITank(this);
        aiMine = new BlocklingAIMine(this);
        aiChop = new BlocklingAIChop(this);
        aiFarm = new BlocklingAIFarm(this);

        tasksToAI = new HashMap<>();
        tasksToAI.put(Task.GUARD, aiGuard);
        tasksToAI.put(Task.HUNT, aiHunt);
        tasksToAI.put(Task.TANK, aiTank);
        tasksToAI.put(Task.MINE, aiMine);
        tasksToAI.put(Task.CHOP, aiChop);
        tasksToAI.put(Task.FARM, aiFarm);

        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, aiSit);
        tasks.addTask(2, new BlocklingAIAttackMelee(this));
        tasks.addTask(3, new BlocklingAIDefend(this));
        tasks.addTask(10, aiFollowOwner);
        tasks.addTask(11, aiWander);
        tasks.addTask(12, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(12, new EntityAILookIdle(this));

        reapplyAI();
    }

    private void reapplyAI()
    {
        for (Task task : blocklingPrioritisedTasks)
        {
            EntityAIBase ai = tasksToAI.get(task);
            tasks.removeTask(ai);
            tasks.addTask(getTaskPriority(task) + 4, ai);
        }
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

        if (!world.isRemote)
        {
            if (targetResetTimer > targetResetTimerMax)
            {
                targetResetTimer = 0;
                setAttackTarget(null);
            }

            targetResetTimer++;
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        eatMaterial();
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
                if (!world.isRemote) setHeldItem(EnumHand.MAIN_HAND, stack.copy());
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
                if (!world.isRemote) setHeldItem(EnumHand.OFF_HAND, stack.copy());
            }
            else
            {
                openGui(player, false);
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        if (entityIn instanceof EntityLivingBase)
        {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entityIn;
            entityLivingBase.setRevengeTarget(this);
        }

        double damage = getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        entityIn.attackEntityFrom(DamageSource.GENERIC, (float)damage);

        return super.attackEntityAsMob(entityIn);
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

    public boolean hasAxe()
    {
        return hasAxe(EnumHand.MAIN_HAND) || hasAxe(EnumHand.OFF_HAND);
    }
    public boolean hasAxe(EnumHand hand)
    {
        return ItemHelper.isAxe(getHeldItem(hand).getItem());
    }

    public boolean hasHoe()
    {
        return hasHoe(EnumHand.MAIN_HAND) || hasHoe(EnumHand.OFF_HAND);
    }
    public boolean hasHoe(EnumHand hand)
    {
        return ItemHelper.isHoe(getHeldItem(hand).getItem());
    }

    public boolean hasPickaxe()
    {
        return hasPickaxe(EnumHand.MAIN_HAND) || hasPickaxe(EnumHand.OFF_HAND);
    }
    public boolean hasPickaxe(EnumHand hand)
    {
        return ItemHelper.isPickaxe(getHeldItem(hand).getItem());
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

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    public void openGui(EntityPlayer player)
    {
        openGui(player, true);
    }
    public void openGui(EntityPlayer player, boolean sync)
    {
        if ((world.isRemote && currentGuiTab.client) || (!world.isRemote && !currentGuiTab.client))
        {
            openGui(player, currentGuiTab.ordinal(), sync);
        }
        else
        {
            if (sync) NetworkHelper.sync(world, new OpenGuiMessage(currentGuiTab.ordinal(), getEntityId()));
        }
    }
    public void openGui(EntityPlayer player, int guiId)
    {
        openGui(player, guiId, true);
    }
    public void openGui(EntityPlayer player, int guiId, boolean sync)
    {
        player.openGui(Blocklings.instance, guiId, world, getEntityId(), 0, 0);
        if (sync) NetworkHelper.sync(world, new OpenGuiMessage(guiId, getEntityId()));
    }
    public void openConfigGui(EntityPlayer player, int guiId, int prevGuiId, UUID whitelist)
    {
        if (getWhitelist(whitelist) == null) return;
        configInfo = new ConfigInfo();
        configInfo.prevGuiId = prevGuiId;
        configInfo.whitelist = whitelist;
        player.openGui(Blocklings.instance, guiId, world, getEntityId(), 0, 0);
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
        blocklingStats.updateBlocklingTypeStats();
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
        setSitting(state == State.SIT);
        if (sync) NetworkHelper.sync(world, new StateMessage(state, getEntityId()));
    }

    public boolean isTaskActive(Task task)
    {
        return blocklingActiveTasks[task.ordinal()];
    }
    public void toggleTask(Task task)
    {
        setTask(task, !blocklingActiveTasks[task.ordinal()]);
    }
    public void toggleTask(Task task, boolean sync)
    {
        setTask(task, !blocklingActiveTasks[task.ordinal()], sync);
    }
    public void setTask(Task task, boolean value)
    {
        setTask(task, value, true);
    }
    public void setTask(Task task, boolean value, boolean sync)
    {
        blocklingActiveTasks[task.ordinal()] = value;
        if (!world.isRemote) reapplyAI();
        if (sync) NetworkHelper.sync(world, new TaskActiveMessage(task, value, getEntityId()));
    }

    public Task[] getTasksInPriorityOrder()
    {
        return blocklingPrioritisedTasks;
    }
    public Task getTaskWithPriority(int priority)
    {
        return blocklingPrioritisedTasks[priority];
    }
    public int getTaskPriority(Task task)
    {
        for (int i = 0; i < blocklingPrioritisedTasks.length; i++)
        {
            if (blocklingPrioritisedTasks[i] == task) return i;
        }
        return -1;
    }
    public void setTaskPriority(Task task, int newPriority)
    {
        setTaskPriority(task, newPriority, true);
    }
    public void setTaskPriority(Task task, int newPriority, boolean sync)
    {
        int oldPriority = getTaskPriority(task);
        if (oldPriority == newPriority) return;

        if (newPriority < oldPriority)
        {
            for (int i = oldPriority; i > newPriority; i--)
            {
                blocklingPrioritisedTasks[i] = blocklingPrioritisedTasks[i - 1];
            }
        }
        else
        {
            for (int i = oldPriority; i < newPriority; i++)
            {
                blocklingPrioritisedTasks[i] = blocklingPrioritisedTasks[i + 1];
            }
        }
        blocklingPrioritisedTasks[newPriority] = task;
        if (!world.isRemote) reapplyAI();
        if (sync) NetworkHelper.sync(world, new TaskPriorityMessage(task, newPriority, getEntityId()));
    }

    public BlocklingWhitelist getWhitelist(UUID id)
    {
        for (int i = 0; i < whitelists.size(); i++)
        {
            if (id.equals(whitelists.get(i).id))
            {
                return whitelists.get(i);
            }
        }

        return null;
    }
    public void setWhitelist(UUID id, BlocklingWhitelist whitelist, boolean sync)
    {
        for (int i = 0; i < whitelists.size(); i++)
        {
            if (id.equals(whitelists.get(i).id))
            {
                whitelists.remove(i);
                whitelists.add(whitelist);
                break;
            }
        }
    }
    public void setWhitelistEntry(UUID id, ResourceLocation entry, boolean value, boolean sync)
    {
        for (int i = 0; i < whitelists.size(); i++)
        {
            if (id.equals(whitelists.get(i).id))
            {
                whitelists.get(i).setEntry(entry, value, sync);
                break;
            }
        }
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