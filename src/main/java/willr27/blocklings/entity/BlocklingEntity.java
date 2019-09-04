package willr27.blocklings.entity;

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
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.gui.container.containers.EquipmentContainer;
import willr27.blocklings.gui.util.GuiHandler;
import willr27.blocklings.gui.util.Tab;
import willr27.blocklings.inventory.BlocklingInventory;
import willr27.blocklings.item.ItemUtil;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.CurrentGuiMessage;

import javax.annotation.Nullable;

public class BlocklingEntity extends TameableEntity implements INamedContainerProvider
{
    public final BlocklingInventory inventory;
    public final AIManager aiManager;

    private BlocklingStats stats;
    private int currentGuiId;

    public BlocklingEntity(EntityType<? extends TameableEntity> type, World worldIn)
    {
        super(type, worldIn);
        inventory = new BlocklingInventory(this);
        aiManager = new AIManager(this);
        currentGuiId = GuiHandler.STATS_ID;
    }

    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        stats = new BlocklingStats(this);
        stats.registerData();
    }

    @Override
    public void livingTick()
    {
        super.livingTick();
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
                        }
                        else
                        {
                            playTameEffect(false);
                            world.setEntityState(this, (byte)6);
                        }
                    }
                }
            }

            if (!player.isSneaking())
            {

            }
            else
            {
                if (!world.isRemote) openCurrentGui(player);
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

    public void openGui(PlayerEntity player, int guiId)
    {
        // TODO: CLEAN UP EXTRA PACKETS
        if (Tab.hasTab(guiId)) setCurrentGuiId(guiId);
        openCurrentGui(player);
    }
    public void openCurrentGui(PlayerEntity player)
    {
        GuiHandler.openGui(currentGuiId, this, player);
    }

    public int getCurrentGuiId() { return currentGuiId; }
    public void setCurrentGuiId(int value) { setCurrentGuiId(value, true); }
    public void setCurrentGuiId(int value, boolean sync) { currentGuiId = value; if (sync) NetworkHandler.sync(world, new CurrentGuiMessage(currentGuiId, getEntityId())); }
}
