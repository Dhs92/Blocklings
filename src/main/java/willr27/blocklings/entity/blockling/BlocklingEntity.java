package willr27.blocklings.entity.blockling;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.gui.container.containers.EquipmentContainer;
import willr27.blocklings.gui.util.GuiHandler;
import willr27.blocklings.gui.util.Tab;
import willr27.blocklings.inventory.BlocklingInventory;
import willr27.blocklings.item.ItemUtil;
import willr27.blocklings.item.ToolType;
import willr27.blocklings.item.ToolUtil;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.CustomNameMessage;
import willr27.blocklings.network.messages.GuiInfoMessage;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;

public class BlocklingEntity extends TameableEntity implements INamedContainerProvider
{
    public final Random random = new Random();

    public final BlocklingInventory inventory;
    public final AIManager aiManager;

    private BlocklingStats stats;
    private BlocklingGuiInfo guiInfo;
    private BlocklingType blocklingType;

    private int actionTimer = -1;
    private int actionInterval;
    private boolean actionFinsihed;
    private BlockPos blockBreaking;
    private Hand previousAttackingHand;

    private int thousandTimer;
    private boolean hasMoved;
    private boolean hasPerformedAction;
    private boolean hasWorked;
    private Vec3d hasMovedLastPosition = new Vec3d(0, 0, 0);

    public BlocklingEntity(EntityType<? extends TameableEntity> type, World worldIn)
    {
        super(type, worldIn);
        inventory = new BlocklingInventory(this);
        aiManager = new AIManager(this);
        guiInfo = new BlocklingGuiInfo(-1, GuiHandler.STATS_ID, -1);
        blocklingType = BlocklingType.OAK_LOG;
    }

    protected void registerAttributes()
    {
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        super.registerAttributes();
        stats = new BlocklingStats(this);
        stats.registerData();
    }

    @Override
    public void livingTick()
    {
        super.livingTick();

        if (getAttackTarget() != null && !getAttackTarget().isAlive()) setAttackTarget(null);

        if (!world.isRemote) inventory.detectAndSendChanges();

        updateHasWorked();
        updateActionTimer();
        updateThousandTimer();
    }

    private void updateThousandTimer()
    {
        if (thousandTimer > 1000) thousandTimer = 0;
        thousandTimer++;
    }

    private void updateHasWorked()
    {
        if (thousandTimer % 20 == 0)
        {
            if (hasMovedLastPosition.distanceTo(getPositionVec()) < 0.01) hasMoved = false;
            else hasMoved = true;
            if (!hasPerformedAction && !hasMoved) hasWorked = false;
            else hasWorked = true;
            hasMovedLastPosition = getPositionVector();
            hasPerformedAction = false;
        }
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();

        if (hand == Hand.MAIN_HAND)
        {
            if (!isTamed())
            {
                if (!world.isRemote)
                {
                    if (ItemUtil.isFlower(item))
                    {
                        if (!player.isCreative()) stack.shrink(1);
                        if (rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player))
                        {
                            setTamedBy(player);
                            navigator.clearPath();
                            setAttackTarget((LivingEntity)null);
                            playTameEffect(true);
                            world.setEntityState(this, (byte)7);
                            aiManager.getGoalFromId(AIManager.FOLLOW_ID).setActive(true, true);
                            if (getCustomName() == null) setName("Blockling");
                        }
                        else
                        {
                            playTameEffect(false);
                            world.setEntityState(this, (byte)6);
                        }
                    }
                }
            }
            else
            {
                if (!player.isSneaking())
                {
                    if (ToolUtil.isTool(item))
                    {
                        setHeldItem(Hand.MAIN_HAND, stack.copy());
                    }
                }
                else
                {
                    if (ToolUtil.isTool(item))
                    {
                        setHeldItem(Hand.OFF_HAND, stack.copy());
                    }
                    else if (!world.isRemote)
                    {
                        openCurrentGui(player);
                    }
                }
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity)
    {
        ItemStack mainStack = getHeldItemMainhand();
        ItemStack offStack = getHeldItemOffhand();
        Item mainItem = mainStack.getItem();
        Item offItem = offStack.getItem();
        ToolType mainType = ToolType.getToolType(mainItem);
        ToolType offType = ToolType.getToolType(offItem);

        Hand attackingHand = previousAttackingHand;
        boolean dualWielding = false;

        if (mainType == ToolType.WEAPON && offType == ToolType.WEAPON) { attackingHand = previousAttackingHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND; dualWielding = true; }
        else if (mainType == ToolType.WEAPON) attackingHand = Hand.MAIN_HAND;
        else if (offType == ToolType.WEAPON) attackingHand = Hand.OFF_HAND;
        else if (!mainStack.isEmpty() && !offStack.isEmpty()) attackingHand = previousAttackingHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        else if (!mainStack.isEmpty()) attackingHand = Hand.MAIN_HAND;
        else if (!offStack.isEmpty()) attackingHand = Hand.OFF_HAND;
        else attackingHand = previousAttackingHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;

        ItemStack weaponStack = getHeldItem(attackingHand);

        float attackDamage = (float)stats.getAttackDamage();
        float knockbackAmount = (float)getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue();

        if (entity instanceof LivingEntity)
        {
            attackDamage += EnchantmentHelper.getModifierForCreature(weaponStack, ((LivingEntity)entity).getCreatureAttribute());
            knockbackAmount += (float)EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, weaponStack);
        }

        Collection<AttributeModifier> modifiers =  weaponStack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        for (AttributeModifier modifier : modifiers)
        {
            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) attackDamage += dualWielding ? modifier.getAmount() / 2.0 : modifier.getAmount();
        }

        int fireAspectAmount = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, weaponStack);
        if (fireAspectAmount > 0)
        {
            entity.setFire(fireAspectAmount * 4);
        }

        boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        if (flag)
        {
            if (knockbackAmount > 0.0F && entity instanceof LivingEntity)
            {
                ((LivingEntity)entity).knockBack(this, knockbackAmount * 0.5F, (double) MathHelper.sin(rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(rotationYaw * ((float)Math.PI / 180F))));
                setMotion(getMotion().mul(0.6D, 1.0D, 0.6D));
            }

            weaponStack.attemptDamageItem(1, random, null);
            stats.incCombatXp((int) attackDamage);
        }

        previousAttackingHand = attackingHand;

        return flag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public ItemStack getHeldItemMainhand()
    {
        return inventory.getStackInSlot(BlocklingInventory.MAIN_SLOT);
    }

    @Override
    public ItemStack getHeldItemOffhand()
    {
        return inventory.getStackInSlot(BlocklingInventory.OFF_SLOT);
    }

    @Override
    public ItemStack getHeldItem(Hand hand)
    {
        return hand == Hand.MAIN_HAND ? getHeldItemMainhand() : getHeldItemOffhand();
    }

    @Override
    public void setHeldItem(Hand hand, ItemStack stack)
    {
        int slot = hand == Hand.MAIN_HAND ? BlocklingInventory.MAIN_SLOT : BlocklingInventory.OFF_SLOT;
        inventory.setInventorySlotContents(slot, stack);
    }

    public boolean isHoldingToolType(ToolType type, Hand hand)
    {
        return ToolType.isTooltype(type, getHeldItem(hand).getItem());
    }
    public boolean isHoldingToolType(ToolType type)
    {
        return isHoldingToolType(type, Hand.MAIN_HAND) || isHoldingToolType(type, Hand.OFF_HAND);
    }

    public boolean hasToolType(ToolType type)
    {
        return inventory.findToolType(type) != -1;
    }

    public void switchToToolType(ToolType type)
    {
        ItemStack mainStack = getHeldItemMainhand();
        ItemStack offStack = getHeldItemOffhand();
        Item mainItem = mainStack.getItem();
        Item offItem = offStack.getItem();
        ToolType mainType = ToolType.getToolType(mainItem);
        ToolType offType = ToolType.getToolType(offItem);

        if (mainType != type)
        {
            int slot = inventory.findToolType(type, BlocklingInventory.INVENTORY_START_SLOT, BlocklingInventory.INVENTORY_END_SLOT);
            if (slot != -1)
            {
                ItemStack newTool = inventory.getStackInSlot(slot);
                setHeldItem(Hand.MAIN_HAND, newTool);
                inventory.setInventorySlotContents(slot, mainStack);
            }
        }
        if (offType != type)
        {
            int slot = inventory.findToolType(type, BlocklingInventory.INVENTORY_START_SLOT, BlocklingInventory.INVENTORY_END_SLOT);
            if (slot != -1)
            {
                ItemStack newTool = inventory.getStackInSlot(slot);
                setHeldItem(Hand.OFF_HAND, newTool);
                inventory.setInventorySlotContents(slot, offStack);
            }
        }
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable)
    {
        return null;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player)
    {
        return new EquipmentContainer(id, inv, inventory);
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        if (getBlockBreaking() != null)
        {
            world.sendBlockBreakProgress(getEntityId(), getBlockBreaking(), -1);
        }

        super.onDeath(cause);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn)
    {
        if (spawnReasonIn == SpawnReason.NATURAL)
        {
            for (Predicate<BlocklingEntity> predicate : blocklingType.predicates)
            {
                if (!predicate.test(this))
                {
                    return false;
                }
            }
        }

        return super.canSpawn(worldIn, spawnReasonIn);
    }

    public void openGui(PlayerEntity player, int guiId)
    {
        // TODO: CLEAN UP EXTRA PACKETS
        openGui(player, guiId, -1);
    }
    public void openGui(PlayerEntity player, int guiId, int selectedGoalId)
    {
        // TODO: CLEAN UP EXTRA PACKETS
        int recentTab = Tab.hasTab(guiId) ? guiId : guiInfo.mostRecentTabbedGuiId;
        setGuiInfo(new BlocklingGuiInfo(guiId, recentTab, selectedGoalId));
        GuiHandler.openGui(guiId, this, player);
    }
    public void openCurrentGui(PlayerEntity player)
    {
        GuiHandler.openGui(guiInfo.mostRecentTabbedGuiId, this, player);
    }

    public BlocklingGuiInfo getGuiInfo() { return guiInfo; }
    public void setGuiInfo(BlocklingGuiInfo value) { setGuiInfo(value, true); }
    public void setGuiInfo(BlocklingGuiInfo value, boolean sync) { guiInfo = value; if (sync) NetworkHandler.sync(world, new GuiInfoMessage(guiInfo, getEntityId())); }

    public void setName(String name) { setName(name, true); }
    public void setName(String name, boolean sync) { setCustomName(new StringTextComponent(name)); if (sync) NetworkHandler.sync(world, new CustomNameMessage(name, getEntityId())); }

    public BlocklingStats getStats() { return stats; }

    public int getActionInvterval() { return actionInterval; }
    public int getActionTimer() { return actionTimer; }
    public boolean isPerformingAction() { return actionTimer != -1; }
    public boolean hasFinishedAction() { return actionFinsihed; }
    public void setFinishedAction(boolean value) { actionFinsihed = value; }
    public void startAction(int interval) { actionInterval = interval; actionTimer = 0; setFinishedAction(false); }
    public void stopAction() { setBlockBreaking(null); actionTimer = -1; }
    private void updateActionTimer() { if (actionTimer >= actionInterval) { setFinishedAction(true); stopAction(); } else if (actionTimer < actionInterval && actionTimer != -1) { actionTimer++; hasPerformedAction = true; } }

    public BlockPos getBlockBreaking() { return blockBreaking; }
    public void setBlockBreaking(BlockPos blockPos) { blockBreaking = blockPos; }

    public int getThousandTimer() { return thousandTimer; }
    public boolean hasMoved() { return hasMoved; }
    public boolean hasPerformedAction() { return hasPerformedAction; }
    public boolean hasWorked() { return hasWorked; }

    public BlocklingType getBlocklingType() { return blocklingType; }
}
