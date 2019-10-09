package willr27.blocklings.entity.ai.goals;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.Utilities.ChestInventory;

import java.util.EnumSet;

public class BlocklingPlaceTorchesGoal extends Goal
{
    private BlocklingEntity blockling;
    private World world;

    public BlocklingPlaceTorchesGoal(BlocklingEntity blockling)
    {
        setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blockling = blockling;
        this.world = blockling.world;
    }

    @Override
    public boolean shouldExecute()
    {
        if (!blockling.aiManager.isActive(AIManager.PLACE_TORCHES_ID)) return false;

        if (blockling.getThousandTimer() % 40 != 0) return false;

        BlockPos pos = blockling.getPosition();
        if (world.getLight(pos) < 7)
        {
            boolean placedTorch = false;
            int index = blockling.equipmentInventory.find(Items.TORCH);
            if (index != -1)
            {
                blockling.equipmentInventory.decrStackSize(index, 1);
                world.setBlockState(pos, Blocks.TORCH.getDefaultState());
                placedTorch = true;
            }

            if (!placedTorch)
            {
                ChestInventory inv = (ChestInventory) blockling.getUtilityManager().getInventory1();
                if (inv != null)
                {
                    index = inv.find(Items.TORCH);
                    if (index != -1)
                    {
                        inv.decrStackSize(index, 1);
                        world.setBlockState(pos, Blocks.TORCH.getDefaultState());
                        placedTorch = true;
                    }
                }
            }

            if (!placedTorch)
            {
                ChestInventory inv = (ChestInventory) blockling.getUtilityManager().getInventory2();
                if (inv != null)
                {
                    index = inv.find(Items.TORCH);
                    if (index != -1)
                    {
                        inv.decrStackSize(index, 1);
                        world.setBlockState(pos, Blocks.TORCH.getDefaultState());
                    }
                }
            }
        }

        return true;
    }
}
