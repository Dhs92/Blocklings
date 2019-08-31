package com.blocklings.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockHelper
{
    public static List<Block> ores = new ArrayList<>();
    static
    {
        ores.add(Blocks.COAL_ORE);
        ores.add(Blocks.IRON_ORE);
        ores.add(Blocks.GOLD_ORE);
        ores.add(Blocks.LAPIS_ORE);
        ores.add(Blocks.REDSTONE_ORE);
        ores.add(Blocks.LIT_REDSTONE_ORE);
        ores.add(Blocks.DIAMOND_ORE);
        ores.add(Blocks.EMERALD_ORE);
    }

    public static boolean isOre(Block block)
    {
        return block instanceof BlockOre;
    }

    public static List<Block> logs = new ArrayList<>();
    static
    {
        logs.add(Blocks.LOG);
        logs.add(Blocks.LOG2);
    }

    public static boolean isLog(Block block)
    {
        return block instanceof BlockLog;
    }

    public static List<Block> crops = new ArrayList<>();
    static
    {
        crops.add(Blocks.WHEAT);
        crops.add(Blocks.CARROTS);
        crops.add(Blocks.POTATOES);
        crops.add(Blocks.BEETROOTS);
    }

    public static boolean isCrop(Block block)
    {
        return block instanceof BlockCrops;
    }

    public static NonNullList<ItemStack> getDrops(World world, BlockPos blockPos, IBlockState blockState, ItemStack mainStack, ItemStack offStack)
    {
        NonNullList<ItemStack> drops = NonNullList.create();
        Block block = blockState.getBlock();

        int mainSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, mainStack);
        int offSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, offStack);
        boolean silkTouch = mainSilkTouch + offSilkTouch > 0;
        if (silkTouch)
        {
            drops.add(new ItemStack(block, 1));
        }
        else
        {
            int mainFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, mainStack);
            int offFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, offStack);
            int fortune = mainFortune + offFortune;
            block.getDrops(drops, world, blockPos, blockState, fortune);
        }

        return drops;
    }
}
