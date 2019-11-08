package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import willr27.blocklings.abilities.Abilities;
import willr27.blocklings.block.BlockUtil;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.ai.AiUtil;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.InventoryUtil;
import willr27.blocklings.whitelist.BlocklingWhitelist;

import java.util.EnumSet;

public class BlocklingBonemealCropsGoal extends Goal
{
    private BlocklingEntity blockling;
    private World world;

    private int searchRadiusX = 20;
    private int searchRadiusY = 20;

    private BlockPos targetPos;
    private int timer;
    private int timerMax = 10;

    public BlocklingBonemealCropsGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
        this.world = blockling.world;
    }

    @Override
    public void resetTask()
    {
        resetTarget();
        timer = 0;
    }

    private void resetTarget()
    {
        targetPos = null;
    }

    @Override
    public void startExecuting()
    {

    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.BONEMEAL_CROPS_ID)) return false;

        if (InventoryUtil.find(blockling, Items.BONE_MEAL).index == -1) return false;

        if (!findCrop()) return false;

        return true;
    }

    private boolean findCrop()
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

                    BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.FARM_NEARBY_ID, AIManager.BONEMEAL_CROPS_WHITELIST_ID);
                    if ((whitelist == null && BlockUtil.isCrop(testBlock)) || (whitelist != null && whitelist.isInWhitelist(testBlock)))
                    {
                        if (!BlockUtil.isGrown(testState))
                        {
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
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (!isValidTarget()) return false;

        return super.shouldContinueExecuting();
    }

    private boolean isValidTarget()
    {
        if (targetPos == null) return false;

        BlockState state = world.getBlockState(targetPos);
        Block block = state.getBlock();
        BlocklingWhitelist whitelist = blockling.aiManager.getWhitelist(AIManager.FARM_NEARBY_ID, AIManager.BONEMEAL_CROPS_WHITELIST_ID);
        if ((whitelist == null && !BlockUtil.isCrop(block)) || (whitelist != null && whitelist.isInBlacklist(block)) || BlockUtil.isGrown(state))
        {
            return false;
        }

        return true;
    }

    @Override
    public void tick()
    {
        double distanceSq = blockling.getPosition().distanceSq(targetPos);
        if (distanceSq < blockling.getStats().farmingRangeSq.getFloat())
        {
            if (timer >= timerMax)
            {
                BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, targetPos, null);
                if (!blockling.abilityManager.isBought(Abilities.Farming.BONEMEAL_EFFICIENT) || blockling.random.nextInt(2) == 0) InventoryUtil.take(blockling, Items.BONE_MEAL);
                timer = 0;
            }

            timer++;
        }
    }
}
