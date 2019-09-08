package willr27.blocklings.entity.blockling;

import net.minecraft.entity.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.jline.utils.Log;
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

    private int blockBreakTimer = -1;
    private int blockBreakInterval;
    private boolean blockBroken;
    private BlockPos blockBreaking;

    private int thousandTimer;
    private boolean hasMoved;
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

        updateHasMoved();
        updateBlockBreakTimer();
        updateThousandTimer();
    }

    private void updateThousandTimer()
    {
        if (thousandTimer > 1000) thousandTimer = 0;
        thousandTimer++;
    }

    private void updateHasMoved()
    {
        if (thousandTimer % 20 == 0)
        {
            if (hasMovedLastPosition.distanceTo(getPositionVec()) < 0.05) hasMoved = false;
            else hasMoved = true;
            hasMovedLastPosition = getPositionVector();
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
                        setHeldItem(Hand.MAIN_HAND, stack);
                    }
                }
                else
                {
                    if (ToolUtil.isTool(item))
                    {
                        setHeldItem(Hand.OFF_HAND, stack);
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
        return super.attackEntityAsMob(entity);
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

    public boolean hasToolType(ToolType type, Hand hand)
    {
        return ToolType.isTooltype(type, getHeldItem(hand).getItem());
    }
    public boolean hasToolType(ToolType type)
    {
        return hasToolType(type, Hand.MAIN_HAND) || hasToolType(type, Hand.OFF_HAND);
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

    public int getBlockBreakInterval() { return blockBreakInterval; }
    public int getBlockBreakTimer() { return blockBreakTimer; }
    public boolean isBreakingBlock() { return blockBreakTimer != -1; }
    public boolean hasBrokenBlock() { return blockBroken; }
    public void setBrokenBlock(boolean value) { blockBroken = value; }
    public void startBreakingBlock(BlockPos blockPos, int interval) { setBlockBreaking(blockPos); blockBreakInterval = interval; blockBreakTimer = 0; setBrokenBlock(false); }
    public void stopBreakingBlock() { setBlockBreaking(null); blockBreakTimer = -1; }
    private void updateBlockBreakTimer() { if (blockBreakTimer >= blockBreakInterval) { setBrokenBlock(true); stopBreakingBlock(); } else if (blockBreakTimer < blockBreakInterval && blockBreakTimer != -1) blockBreakTimer++; }

    public BlockPos getBlockBreaking() { return blockBreaking; }
    public void setBlockBreaking(BlockPos blockPos) { blockBreaking = blockPos; }

    public int getThousandTimer() { return thousandTimer; }
    public boolean hasMoved() { return hasMoved; }

    public BlocklingType getBlocklingType() { return blocklingType; }
}
