package willr27.blocklings.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DropUtil
{
    public static List<ItemStack> getDrops(Entity entity, BlockPos blockPos, ItemStack stack)
    {
        return getDrops(entity, blockPos, stack, stack);
    }

    public static List<ItemStack> getDrops(Entity entity, BlockPos blockPos, ItemStack mainStack, ItemStack offStack)
    {
        List<ItemStack> drops = new ArrayList<>();
        BlockState blockState = entity.world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        ItemStack stack = new Random().nextInt(2) == 0 ? mainStack : offStack;
        drops.addAll(block.getDrops(blockState, (ServerWorld) entity.world, blockPos, null, entity, stack));
        drops = drops.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());

        return drops;
    }
}
