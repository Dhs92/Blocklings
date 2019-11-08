package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import willr27.blocklings.abilities.Abilities;
import willr27.blocklings.block.BlockUtil;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.ai.AiUtil;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.InventoryUtil;
import willr27.blocklings.item.DropUtil;
import willr27.blocklings.item.ToolType;
import willr27.blocklings.whitelist.BlocklingWhitelist;

import java.util.*;

public class BlocklingFarmCropsNearbyGoal extends Goal
{
    public static final ToolType TOOL_TYPE = ToolType.HOE;

    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlocklingEntity blockling;
    private World world;

    private Set<BlockPos> failedBlocks = new LinkedHashSet<>();
    private BlockPos targetPos;

    private float cropsHarvested = 0;

    public BlocklingFarmCropsNearbyGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
        this.world = blockling.world;
    }

    @Override
    public void startExecuting()
    {

    }

    @Override
    public void resetTask()
    {
        resetTarget();
        cropsHarvested = 0;

        if (blockling.abilityManager.isBought(Abilities.Farming.FASTER_FARMING_FOR_CROPS))
        {
            blockling.getStats().farmingIntervalFasterFarmingEnhancedAbilityModifier.setValue(1.0f);
        }
    }

    private void resetTarget()
    {
        targetPos = null;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getThousandTimer() % 80 == 0) failedBlocks.clear();
        if (!blockling.aiManager.isActive(AIManager.FARM_NEARBY_ID)) return false;
        if (!blockling.hasToolType(TOOL_TYPE)) return false;

        if (!findCrop()) return false;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (targetPos == null) return false;
        if (!blockling.hasToolType(TOOL_TYPE)) return false;

        return true;
    }

    @Override
    public void tick()
    {
        blockling.switchToToolType(TOOL_TYPE);
        moveToTarget();
        tryHarvestTarget();
    }

    private void tryHarvestTarget()
    {
        if (targetPos != null)
        {
            double distanceSq = blockling.getPosition().distanceSq(targetPos);

            if (distanceSq < blockling.getStats().farmingRangeSq.getFloat())
            {
                if (blockling.hasFinishedAction())
                {
                    BlockState targetState = world.getBlockState(targetPos);
                    Block targetBlock = targetState.getBlock();
                    Item seed = BlockUtil.getSeed((CropsBlock) targetBlock);

                    ItemStack mainStack = blockling.isHoldingToolType(TOOL_TYPE, Hand.MAIN_HAND) ? blockling.getHeldItemMainhand() : ItemStack.EMPTY;
                    ItemStack offStack = blockling.isHoldingToolType(TOOL_TYPE, Hand.OFF_HAND) ? blockling.getHeldItemOffhand() : ItemStack.EMPTY;
                    List<ItemStack> drops = DropUtil.getDrops(blockling, targetPos, mainStack, offStack);
                    addDropsToInventoryOrWorld(drops, targetPos);

                    int itemDamage = blockling.abilityManager.isBought(Abilities.Farming.FASTER_FARMING_FOR_DURABILITY) ? 2 : 1;
                    mainStack.attemptDamageItem(itemDamage, new Random(), null);
                    offStack.attemptDamageItem(itemDamage, new Random(), null);

                    BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.FARM_NEARBY_ID, AIManager.FARM_NEARBY_CROPS_SEEDS_WHITELIST_ID);
                    if ((whitelist == null && BlockUtil.isSeed(seed)) || (whitelist != null && whitelist.isInWhitelist(seed)))
                    {
                        int slot = blockling.equipmentInventory.find(seed);
                        if (slot != -1)
                        {
                            blockling.equipmentInventory.getStackInSlot(slot).shrink(1);
                            world.setBlockState(targetPos, targetBlock.getDefaultState());
                        }
                        else
                        {
                            world.destroyBlock(targetPos, false);
                        }
                    }
                    else
                    {
                        world.destroyBlock(targetPos, false);
                    }
                    resetTarget();

                    cropsHarvested++; // TODO: WON'T WORK, ALWAYS RESETS
                    if (blockling.abilityManager.isBought(Abilities.Farming.FASTER_FARMING_FOR_CROPS))
                    {
                        blockling.getStats().farmingIntervalFasterFarmingEnhancedAbilityModifier.setValue(Math.max(0.5f, 1.0f - (cropsHarvested / 66.0f)));
                    }

                    blockling.getStats().farmingXp.incBaseValue(blockling.random.nextInt(4) + 3);
                    blockling.setFinishedAction(false);
                }
                else if (!blockling.isPerformingAction())
                {
                    blockling.startAction((int) blockling.getStats().farmingInterval.getFloat());
                    blockling.setBlockBreaking(targetPos);
                }
            }
            else if (!blockling.hasWorked())
            {
                failedBlocks.add(targetPos);
                resetTarget();
            }
        }
    }

    private void addDropsToInventoryOrWorld(List<ItemStack> drops, BlockPos dropPos)
    {
        for (ItemStack stack : drops)
        {
            ItemStack remainderStack = InventoryUtil.add(blockling, stack);
            if (!remainderStack.isEmpty()) InventoryHelper.spawnItemStack(world, dropPos.getX() + 0.5, dropPos.getY() + 0.5, dropPos.getZ() + 0.5, remainderStack);
        }
    }

    private void moveToTarget()
    {
        if (blockling.getNavigator().getPath() == null || !blockling.hasMoved())
        {
            boolean hasPath = false;

            double distanceSq = blockling.getPosition().distanceSq(targetPos);

            if (distanceSq < blockling.getStats().farmingRangeSq.getFloat())
            {
                hasPath = true;
            }
            else
            {
                Path path = AiUtil.getPathTo(blockling, targetPos, blockling.getStats().farmingRangeSq.getFloat());

                if (path != null)
                {
                    distanceSq = AiUtil.distanceSqFromTarget(path, targetPos);

                    if (distanceSq < blockling.getStats().farmingRangeSq.getFloat())
                    {
                        blockling.getNavigator().setPath(path, 1.0);
                        hasPath = true;
                    }
                }
            }

            if (!hasPath)
            {
                failedBlocks.add(targetPos);
                resetTarget();
            }
        }
    }

    private boolean findCrop()
    {
        int blocklingX = (int)Math.floor(blockling.posX);
        int blocklingY = (int)Math.floor(blockling.posY);
        int blocklingZ = (int)Math.floor(blockling.posZ);

        int startX = blocklingX - searchRadiusX;
        int startY = blocklingY + searchRadiusY;
        int startZ = blocklingZ - searchRadiusX;

        int endX = blocklingX + searchRadiusX + 1;
        int endY = blocklingY - searchRadiusY - 1;
        int endZ = blocklingZ + searchRadiusX + 1;

        for (int y = startY; y > endY; y--)
        {
            for (int x = startX; x < endX; x++)
            {
                for (int z = startZ; z < endZ; z++)
                {
                    BlockPos testPos = new BlockPos(x, y, z);
                    BlockState testState = world.getBlockState(testPos);
                    Block testBlock = testState.getBlock();

                    if (failedBlocks.contains(testPos))
                    {
                        continue;
                    }

                    BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.FARM_NEARBY_ID, AIManager.FARM_NEARBY_CROPS_CROPS_WHITELIST_ID);
                    if ((whitelist == null && BlockUtil.isCrop(testBlock)) || (whitelist != null && whitelist.isInWhitelist(testBlock)))
                    {
                        if (!BlockUtil.isGrown(testState))
                        {
                            continue;
                        }

                        double distanceSq = blockling.getPosition().distanceSq(testPos);

                        if (distanceSq < blockling.getStats().farmingRangeSq.getFloat())
                        {
                            targetPos = testPos;
                            return true;
                        }

                        Path testPath = AiUtil.getPathTo(blockling, testPos, blockling.getStats().farmingRangeSq.getFloat());

                        if (testPath != null)
                        {
                            distanceSq = AiUtil.distanceSqFromTarget(testPath, testPos);

                            if (distanceSq < blockling.getStats().farmingRangeSq.getFloat())
                            {
                                targetPos = testPos;
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
