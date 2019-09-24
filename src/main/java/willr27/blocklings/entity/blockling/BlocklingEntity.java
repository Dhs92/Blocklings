package willr27.blocklings.entity.blockling;

import jdk.nashorn.internal.ir.Block;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jline.utils.Log;
import willr27.blocklings.ability.AbilityManager;
import willr27.blocklings.entity.EntityTypes;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.gui.container.containers.EquipmentContainer;
import willr27.blocklings.gui.util.GuiHandler;
import willr27.blocklings.gui.util.Tab;
import willr27.blocklings.inventory.BlocklingInventory;
import willr27.blocklings.item.ItemUtil;
import willr27.blocklings.item.ToolType;
import willr27.blocklings.item.ToolUtil;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.BlocklingTypeMessage;
import willr27.blocklings.network.messages.CustomNameMessage;
import willr27.blocklings.network.messages.GuiInfoMessage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;

public class BlocklingEntity extends TameableEntity implements INamedContainerProvider, IEntityAdditionalSpawnData
{
    public final Random random = new Random();

    public final BlocklingInventory inventory;
    public final AIManager aiManager;
    public final AbilityManager abilityManager;

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

    private boolean firstTick = true;

    public BlocklingEntity(EntityType<? extends TameableEntity> type, World world)
    {
        super(type, world);
        inventory = new BlocklingInventory(this);
        aiManager = new AIManager(this);
        abilityManager = new AbilityManager(this);
        guiInfo = new BlocklingGuiInfo(-1, GuiHandler.STATS_ID, -1, -1);
    }

    public static <T extends Entity> T create(FMLPlayMessages.SpawnEntity packet, World world)
    {
        BlocklingEntity blockling = new BlocklingEntity(EntityTypes.BLOCKLING, world);
        return (T)blockling;
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeAdditional(CompoundNBT c)
    {
        super.writeAdditional(c);

        c.putInt("blockling_type", BlocklingType.TYPES.indexOf(blocklingType));
    }

    @Override
    public void readAdditional(CompoundNBT c)
    {
        super.writeAdditional(c);

        blocklingType = BlocklingType.TYPES.get(c.getInt("blockling_type"));
    }

    @Override
    public void writeSpawnData(PacketBuffer buf)
    {
        buf.writeInt(BlocklingType.TYPES.indexOf(blocklingType));
    }

    @Override
    public void readSpawnData(PacketBuffer buf)
    {
        blocklingType = BlocklingType.TYPES.get(buf.readInt());
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

        if (firstTick && !world.isRemote) // TODO: REMOVE, IENTITYADDITIONALSPAWNDATA
        {
            NetworkHandler.sync(world, new BlocklingTypeMessage(blocklingType, getEntityId()));
            firstTick = false;
        }

        if (getAttackTarget() != null && !getAttackTarget().isAlive()) setAttackTarget(null);

        if (!world.isRemote)
        {
            inventory.detectAndSendChanges();
        }

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
                            //setCustomNameVisible(true);
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
    public boolean canSpawn(IWorld world, SpawnReason spawnReasonIn)
    {
        if (spawnReasonIn == SpawnReason.CHUNK_GENERATION || spawnReasonIn == SpawnReason.NATURAL)
        {
            if (!world.isAirBlock(getPosition()))
            {
                return false;
            }

            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, getBoundingBox().grow(10.0));
            for (LivingEntity entity : entities)
            {
                if (entity instanceof BlocklingEntity)
                {
                    return false;
                }
            }

            List<BlocklingType> potentialTypes = new ArrayList<>();

            typeLoop: for (BlocklingType type : BlocklingType.TYPES)
            {
                if (type.predicates.isEmpty())
                {
                    continue;
                }

                for (BiPredicate<BlocklingEntity, IWorld> predicate : type.predicates)
                {
                    if (!predicate.test(this, world))
                    {
                        continue typeLoop;
                    }
                }

                potentialTypes.add(type);
            }

            if (potentialTypes.isEmpty())
            {
                return false;
            }
            else
            {
                BlocklingType type = potentialTypes.get(random.nextInt(potentialTypes.size()));
                if (rand.nextInt(type.spawnRateReduction) == 0)
                {
                    if (type == BlocklingType.IRON || type == BlocklingType.LAPIS || type == BlocklingType.GOLD || type == BlocklingType.EMERALD || type == BlocklingType.DIAMOND || type == BlocklingType.OBSIDIAN)
                    {
                        Log.info(type.entityTexture);
                        Log.info(getPosition());
                    }
                    if (type == BlocklingType.QUARTZ)
                    {
                        Log.info(type.entityTexture);
                        Log.info(getPosition());
                    }
                    setBlocklingType(type);
                }
                else
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void openGui(PlayerEntity player, int guiId)
    {
        // TODO: CLEAN UP EXTRA PACKETS
        openGui(player, guiId, -1, -1);
    }
    public void openGui(PlayerEntity player, int guiId, int selectedGoalId, int abilityGroupId)
    {
        // TODO: CLEAN UP EXTRA PACKETS
        int recentTab = Tab.hasTab(guiId) ? guiId : guiInfo.mostRecentTabbedGuiId;
        setGuiInfo(new BlocklingGuiInfo(guiId, recentTab, selectedGoalId, abilityGroupId));
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
    public void setBlocklingType(BlocklingType type) { setBlocklingType(type, true); }
    public void setBlocklingType(BlocklingType type, boolean sync) { blocklingType = type; stats.updateTypeBonuses(); if (sync) NetworkHandler.sync(world, new BlocklingTypeMessage(blocklingType, getEntityId())); }
}
