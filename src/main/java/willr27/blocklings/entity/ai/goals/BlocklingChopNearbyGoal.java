package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.InventoryHelper;
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

public class BlocklingChopNearbyGoal extends Goal
{
    public static final ToolType TOOL_TYPE = ToolType.AXE;

    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlocklingEntity blockling;
    private World world;

    private Set<BlockPos> failedBlocks = new LinkedHashSet<>();
    private Set<BlockPos> tree = new LinkedHashSet<>();
    private BlockPos treeStartPos;
    private BlockPos movePos;

    private float logsChopped = 0;

    public BlocklingChopNearbyGoal(BlocklingEntity blockling)
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
        tree.clear();
        treeStartPos = null;
        resetTarget();
        logsChopped = 0;

        if (blockling.abilityManager.isBought(Abilities.Woodcutting.FASTER_CHOPPING_FOR_LOGS))
        {
            blockling.getStats().woodcuttingIntervalFasterChoppingEnhancedAbilityModifier.setValue(1.0f);
        }
    }

    private void resetTarget()
    {
        // TODO: LAST BLOCK, KEEP TRACK
        if (blockling.getBlockBreaking() != null) world.sendBlockBreakProgress(blockling.getEntityId(), blockling.getBlockBreaking(), -1);
        movePos = null;
    }

    @Override
    public boolean shouldExecute()
    {
        if (blockling.getThousandTimer() % 80 == 0) failedBlocks.clear();
        if (blockling.getThousandTimer() % 20 != 0) return false;
        if (!blockling.aiManager.isActive(AIManager.CHOP_NEARBY_ID)) return false;
        if (!blockling.hasToolType(TOOL_TYPE)) return false;

        if (!findTreeStart()) return false;
        if (!findTree()) return false;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (tree.isEmpty()) return false;
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
        if (movePos != null)
        {
            double distanceSq = blockling.getPosition().distanceSq(movePos);

            if (distanceSq < blockling.getStats().woodcuttingRangeSq.getFloat())
            {
                if (blockling.hasFinishedAction())
                {
                    BlockPos logPos = (BlockPos) tree.toArray()[tree.size() - 1];
                    Block log = world.getBlockState(logPos).getBlock();
                    world.sendBlockBreakProgress(blockling.getEntityId(), logPos, -1);

                    ItemStack mainStack = blockling.isHoldingToolType(TOOL_TYPE, Hand.MAIN_HAND) ? blockling.getHeldItemMainhand() : ItemStack.EMPTY;
                    ItemStack offStack = blockling.isHoldingToolType(TOOL_TYPE, Hand.OFF_HAND) ? blockling.getHeldItemOffhand() : ItemStack.EMPTY;
                    List<ItemStack> drops = DropUtil.getDrops(blockling, logPos, mainStack, offStack);
                    addDropsToInventoryOrWorld(drops, logPos);

                    int itemDamage = blockling.abilityManager.isBought(Abilities.Woodcutting.FASTER_CHOPPING_FOR_DURABILITY) ? 2 : 1;
                    mainStack.attemptDamageItem(itemDamage, new Random(), null);
                    offStack.attemptDamageItem(itemDamage, new Random(), null);

                    world.destroyBlock(logPos, false);
                    tree.remove(logPos);

                    if (tree.isEmpty() && blockling.abilityManager.isBought(Abilities.Woodcutting.REPLANTER))
                    {
                        Block belowBlock = world.getBlockState(logPos.down()).getBlock();
                        if (belowBlock == Blocks.DIRT || belowBlock == Blocks.GRASS_BLOCK) // TODO: BlockUtil::isDirt
                        {
                            Block sapling = BlockUtil.getSapling(log);
                            if (sapling != null)
                            {
                                BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_SAPLINGS_WHITELIST_ID);
                                if (whitelist == null || whitelist.isInWhitelist(sapling))
                                {
                                    if (InventoryUtil.take(blockling, sapling.asItem()))
                                    {
                                        world.setBlockState(logPos, sapling.getDefaultState());
                                    }
                                }
                            }
                        }
                    }

                    if (blockling.abilityManager.isBought(Abilities.Woodcutting.LEAF_BREAKER))
                    {
                        int radius = blockling.abilityManager.isBought(Abilities.Woodcutting.MORE_LEAVES_BROKEN) ? 3 : 2;

                        int startX = logPos.getX() - (radius - 1);
                        int startY = logPos.getY() - (radius - 1);
                        int startZ = logPos.getZ() - (radius - 1);

                        int endX = logPos.getX() + radius;
                        int endY = logPos.getY() + radius;
                        int endZ = logPos.getZ() + radius;

                        for (int x = startX; x <= endX; x++)
                        {
                            for (int y = startY; y <= endY; y++)
                            {
                                for (int z = startZ; z <= endZ; z++)
                                {
                                    BlockPos surroundingPos = new BlockPos(x, y, z);
                                    BlockState surroundingState = world.getBlockState(surroundingPos);
                                    Block surroundingBlock = surroundingState.getBlock();

                                    if (BlockUtil.isLeaf(surroundingBlock))
                                    {
                                        if (blockling.abilityManager.isBought(Abilities.Woodcutting.LEAF_DROP_GATHERER))
                                        {
                                            drops = DropUtil.getDrops(blockling, surroundingPos, mainStack, offStack);
                                            addDropsToInventoryOrWorld(drops, surroundingPos);
                                        }

                                        world.destroyBlock(surroundingPos, false);
                                    }
                                }
                            }
                        }
                    }

                    logsChopped++;
                    if (blockling.abilityManager.isBought(Abilities.Woodcutting.FASTER_CHOPPING_FOR_LOGS))
                    {
                        blockling.getStats().woodcuttingIntervalFasterChoppingEnhancedAbilityModifier.setValue(Math.max(0.5f, 1.0f - (logsChopped / 66.0f)));
                    }

                    blockling.getStats().woodcuttingXp.incBaseValue(blockling.random.nextInt(4) + 3);
                    blockling.setFinishedAction(false);
                }
                else if (!blockling.isPerformingAction() && !tree.isEmpty())
                {
                    BlockPos logPos = (BlockPos) tree.toArray()[tree.size() - 1];
                    blockling.startAction((int) blockling.getStats().woodcuttingInterval.getFloat());
                    blockling.setBlockBreaking(logPos);
                }
                else if (blockling.getBlockBreaking() != null)
                {
                    float percent = blockling.getActionTimer() / (float) blockling.getActionInvterval();
                    world.sendBlockBreakProgress(blockling.getEntityId(), blockling.getBlockBreaking(), (int)(percent * 8));
                }
            }
            else if (!blockling.hasWorked())
            {
                failedBlocks.add(movePos);
                tree.remove(movePos);
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

    private void updateToValidTarget()
    {
        if (movePos != null)
        {
            BlockState targetState = world.getBlockState(movePos);
            Block targetBlock = targetState.getBlock();

            BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID);
            if (!BlockUtil.isLog(targetBlock) || (whitelist != null && whitelist.isInBlacklist(targetBlock)))
            {
                tree.remove(movePos);
                resetTarget();
            }
        }
    }

    private void moveToTarget()
    {
        if (movePos == null || blockling.getNavigator().getPath() == null)
        {
            boolean foundTarget = false;

            for (BlockPos blockPos : tree)
            {
                if (AiUtil.canSeeBlock(blockling, blockPos))
                {
                    double distanceSq = blockling.getPosition().distanceSq(blockPos);

                    if (distanceSq < blockling.getStats().woodcuttingRangeSq.getFloat())
                    {
                        movePos = blockPos;
                        foundTarget = true;
                        break;
                    }

                    Path path = AiUtil.getPathTo(blockling, blockPos, blockling.getStats().woodcuttingRangeSq.getFloat());

                    if (path != null)
                    {
                        distanceSq = AiUtil.distanceSqFromTarget(path, blockPos);

                        if (distanceSq < blockling.getStats().woodcuttingRangeSq.getFloat())
                        {
                            movePos = blockPos;
                            blockling.getNavigator().setPath(path, 1.0);
                            foundTarget = true;
                            break;
                        }
                    }
                }
            }

            if (!foundTarget)
            {
                failedBlocks.addAll(tree);
                tree.clear();
            }
        }
    }

    private boolean findTreeStart()
    {
        int blocklingX = (int)Math.floor(blockling.posX);
        int blocklingY = (int)Math.floor(blockling.posY);
        int blocklingZ = (int)Math.floor(blockling.posZ);

        int startX = blocklingX - searchRadiusX;
        int startY = blocklingY - searchRadiusY;
        int startZ = blocklingZ - searchRadiusX;

        int endX = blocklingX + searchRadiusX + 1;
        int endY = blocklingY + searchRadiusY + 1;
        int endZ = blocklingZ + searchRadiusX + 1;

        for (int y = startY; y < endY; y++)
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

                    BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID);
                    if ((whitelist == null && BlockUtil.isLog(testBlock)) || (whitelist != null && whitelist.isInWhitelist(testBlock)))
                    {
                        if (AiUtil.canSeeBlock(blockling, testPos))
                        {
                            double distanceSq = blockling.getPosition().distanceSq(testPos);

                            if (distanceSq < blockling.getStats().woodcuttingRangeSq.getFloat())
                            {
                                treeStartPos = testPos;
                                return true;
                            }

                            Path testPath = AiUtil.getPathTo(blockling, testPos, blockling.getStats().woodcuttingRangeSq.getFloat());

                            if (testPath != null)
                            {
                                distanceSq = AiUtil.distanceSqFromTarget(testPath, testPos);

                                if (distanceSq < blockling.getStats().woodcuttingRangeSq.getFloat())
                                {
                                    treeStartPos = testPos;
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

    private boolean findTree()
    {
        Set<BlockPos> positionsToTest = new HashSet<>();
        positionsToTest.add(treeStartPos);

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

                        if (tree.contains(surroundingPos))
                        {
                            continue;
                        }

                        BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.CHOP_NEARBY_ID, AIManager.CHOP_NEARBY_LOGS_LOGS_WHITELIST_ID);
                        if ((whitelist == null && BlockUtil.isLog(surroundingBlock)) || (whitelist != null && whitelist.isInWhitelist(surroundingBlock)))
                        {
                            positionsToTest.add(surroundingPos);
                        }
                    }
                }
            }

            tree.add(testPos);
            positionsToTest.remove(testPos);
        }

        return !tree.isEmpty();
    }
}
