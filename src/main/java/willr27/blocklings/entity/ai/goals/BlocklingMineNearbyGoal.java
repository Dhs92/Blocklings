package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import willr27.blocklings.abilities.Abilities;
import willr27.blocklings.abilities.AbilityGroup;
import willr27.blocklings.abilities.AbilityState;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.ai.AiUtil;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.item.DropUtil;
import willr27.blocklings.item.ToolType;

import java.util.*;

public class BlocklingMineNearbyGoal extends Goal
{
    public static final ToolType TOOL_TYPE = ToolType.PICKAXE;

    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlocklingEntity blockling;
    private World world;

    private Set<BlockPos> failedBlocks = new LinkedHashSet<>();
    private Set<BlockPos> vein = new LinkedHashSet<>();
    private BlockPos veinStartPos;
    private BlockPos targetPos;

    private float oresMined = 0;

    public BlocklingMineNearbyGoal(BlocklingEntity blockling)
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
        vein.clear();
        veinStartPos = null;
        resetTarget();
        blockling.stopAction();
        oresMined = 0;

        if (blockling.abilityManager.getGroup(AbilityGroup.MINING).getState(Abilities.Mining.FASTER_MINING_FOR_ORES) == AbilityState.BOUGHT)
        {
            blockling.getStats().miningIntervalFasterMiningEnhancedAbilityModifier.setValue(1.0f);
        }
    }

    private void resetTarget()
    {
        if (targetPos != null) world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, -1);
        targetPos = null;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getThousandTimer() % 80 == 0) failedBlocks.clear();
        if (blockling.getThousandTimer() % 20 != 0) return false;
        if (!blockling.aiManager.isActive(AIManager.MINE_NEARBY_ID)) return false;
        if (!blockling.hasToolType(TOOL_TYPE)) return false;

        if (!findVeinStart()) return false;
        if (!findVein()) return false;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (vein.isEmpty()) return false;
        if (!blockling.hasToolType(TOOL_TYPE)) return false;

        return true;
    }

    @Override
    public void tick()
    {
        blockling.switchToToolType(TOOL_TYPE);
        updateToValidTarget();
        moveToTarget();
        tryMineTarget();
    }

    private void tryMineTarget()
    {
        if (targetPos != null)
        {
            double distanceSq = blockling.getPosition().distanceSq(targetPos);

            if (distanceSq < blockling.getStats().miningRangeSq.getFloat())
            {
                if (blockling.hasFinishedAction())
                {
                    ItemStack mainStack = blockling.isHoldingToolType(TOOL_TYPE, Hand.MAIN_HAND) ? blockling.getHeldItemMainhand() : ItemStack.EMPTY;
                    ItemStack offStack = blockling.isHoldingToolType(TOOL_TYPE, Hand.OFF_HAND) ? blockling.getHeldItemOffhand() : ItemStack.EMPTY;
                    List<ItemStack> drops = DropUtil.getDrops(blockling, targetPos, mainStack, offStack);
                    addDropsToInventoryOrWorld(drops, targetPos);

                    int itemDamage = blockling.abilityManager.isBought(AbilityGroup.MINING, Abilities.Mining.FASTER_MINING_FOR_DURABILITY) ? 2 : 1;
                    mainStack.attemptDamageItem(itemDamage, new Random(), null);
                    offStack.attemptDamageItem(itemDamage, new Random(), null);

                    world.destroyBlock(targetPos, false);
                    vein.remove(targetPos);
                    resetTarget();

                    oresMined++;
                    if (blockling.abilityManager.isBought(AbilityGroup.MINING, Abilities.Mining.FASTER_MINING_FOR_ORES))
                    {
                        blockling.getStats().miningIntervalFasterMiningEnhancedAbilityModifier.setValue(Math.max(0.5f, 1.0f - (oresMined / 25.0f)));
                    }

                    blockling.getStats().miningXp.incBaseValue(blockling.random.nextInt(4) + 3);
                    blockling.setFinishedAction(false);
                }
                else if (!blockling.isPerformingAction())
                {
                    blockling.startAction((int) blockling.getStats().miningInterval.getFloat());
                    blockling.setBlockBreaking(targetPos);
                }
                else if (targetPos != null)
                {
                    float percent = blockling.getActionTimer() / (float) blockling.getActionInvterval();
                    world.sendBlockBreakProgress(blockling.getEntityId(), targetPos, (int)(percent * 8));
                }
            }
            else if (!blockling.hasWorked())
            {
                failedBlocks.add(targetPos);
                vein.remove(targetPos);
                resetTarget();
            }
        }
    }

    private void addDropsToInventoryOrWorld(List<ItemStack> drops, BlockPos dropPos)
    {
        for (ItemStack stack : drops)
        {
            ItemStack remainderStack = blockling.equipmentInventory.addItem(stack);
            if (!remainderStack.isEmpty()) InventoryHelper.spawnItemStack(world, dropPos.getX() + 0.5, dropPos.getY() + 0.5, dropPos.getZ() + 0.5, remainderStack);
        }
    }

    private void updateToValidTarget()
    {
        if (targetPos != null)
        {
            BlockState targetState = world.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if (blockling.aiManager.getWhitelist(AIManager.MINE_NEARBY_ID, AIManager.MINE_NEARBY_ORES_WHITELIST_ID).isInBlacklist(targetBlock))
            {
                vein.remove(targetPos);
                resetTarget();
            }
        }
    }

    private void moveToTarget()
    {
        if (targetPos == null || blockling.getNavigator().getPath() == null || !blockling.hasMoved())
        {
            boolean foundTarget = false;

            for (BlockPos blockPos : vein)
            {
                if (AiUtil.canSeeBlock(blockling, blockPos))
                {
                    double distanceSq = blockling.getPosition().distanceSq(blockPos);

                    if (distanceSq < blockling.getStats().miningRangeSq.getFloat())
                    {
                        targetPos = blockPos;
                        foundTarget = true;
                        break;
                    }

                    Path path = AiUtil.getPathTo(blockling, blockPos, blockling.getStats().miningRangeSq.getFloat());

                    if (path != null)
                    {
                        distanceSq = AiUtil.distanceSqFromTarget(path, blockPos);

                        if (distanceSq < blockling.getStats().miningRangeSq.getFloat())
                        {
                            targetPos = blockPos;
                            blockling.getNavigator().setPath(path, 1.0);
                            foundTarget = true;
                            break;
                        }
                    }
                }
            }

            if (!foundTarget)
            {
                failedBlocks.addAll(vein);
                vein.clear();
            }
        }
    }

    private boolean findVeinStart()
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

                    if (blockling.aiManager.getWhitelist(AIManager.MINE_NEARBY_ID, AIManager.MINE_NEARBY_ORES_WHITELIST_ID).isInWhitelist(testBlock))
                    {
                        if (AiUtil.canSeeBlock(blockling, testPos))
                        {
                            double distanceSq = blockling.getPosition().distanceSq(testPos);

                            if (distanceSq < blockling.getStats().miningRangeSq.getFloat())
                            {
                                veinStartPos = testPos;
                                return true;
                            }

                            Path testPath = AiUtil.getPathTo(blockling, testPos, blockling.getStats().miningRangeSq.getFloat());

                            if (testPath != null)
                            {
                                distanceSq = AiUtil.distanceSqFromTarget(testPath, testPos);

                                if (distanceSq < blockling.getStats().miningRangeSq.getFloat())
                                {
                                    veinStartPos = testPos;
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean findVein()
    {
        Set<BlockPos> positionsToTest = new HashSet<>();
        positionsToTest.add(veinStartPos);

        while (!positionsToTest.isEmpty())
        {
            BlockPos testPos = (BlockPos)positionsToTest.toArray()[0];

            int startX = testPos.getX() - 1;
            int startY = testPos.getY() - 1;
            int startZ = testPos.getZ() - 1;

            int endX = startX + 2;
            int endY = startY + 2;
            int endZ = startZ + 2;

            for (int x = startX; x <= endX; x++)
            {
                for (int y = startY; y <= endY; y++)
                {
                    for (int z = startZ; z <= endZ; z++)
                    {
                        BlockPos surroundingPos = new BlockPos(x, y, z);
                        BlockState surroundingState = world.getBlockState(surroundingPos);
                        Block surroundingBlock = surroundingState.getBlock();

                        if (surroundingPos == testPos)
                        {
                            continue;
                        }

                        if (vein.contains(surroundingPos))
                        {
                            continue;
                        }

                        if (blockling.aiManager.getWhitelist(AIManager.MINE_NEARBY_ID, AIManager.MINE_NEARBY_ORES_WHITELIST_ID).isInWhitelist(surroundingBlock))
                        {
                            positionsToTest.add(surroundingPos);
                        }
                    }
                }
            }

            vein.add(testPos);
            positionsToTest.remove(testPos);
        }

        return !vein.isEmpty();
    }
}
