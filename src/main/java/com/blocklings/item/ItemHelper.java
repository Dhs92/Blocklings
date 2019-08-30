package com.blocklings.item;

import com.blocklings.util.BlocklingType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemHelper
{
    public static final List<Item> WEAPONS = new ArrayList<>();
    public static final List<Item> PICKAXES = new ArrayList<>();
    public static final List<Item> AXES = new ArrayList<>();
    public static final List<Item> HOES = new ArrayList<>();
    static
    {
        WEAPONS.add(Items.WOODEN_SWORD);
        WEAPONS.add(Items.STONE_SWORD);
        WEAPONS.add(Items.GOLDEN_SWORD);
        WEAPONS.add(Items.IRON_SWORD);
        WEAPONS.add(Items.DIAMOND_SWORD);

        PICKAXES.add(Items.WOODEN_PICKAXE);
        PICKAXES.add(Items.STONE_PICKAXE);
        PICKAXES.add(Items.GOLDEN_PICKAXE);
        PICKAXES.add(Items.IRON_PICKAXE);
        PICKAXES.add(Items.DIAMOND_PICKAXE);

        AXES.add(Items.WOODEN_AXE);
        AXES.add(Items.STONE_AXE);
        AXES.add(Items.GOLDEN_AXE);
        AXES.add(Items.IRON_AXE);
        AXES.add(Items.DIAMOND_AXE);

        HOES.add(Items.WOODEN_HOE);
        HOES.add(Items.STONE_HOE);
        HOES.add(Items.GOLDEN_HOE);
        HOES.add(Items.IRON_HOE);
        HOES.add(Items.DIAMOND_HOE);
    }

    public static final List<Item> EQUIPABLE_ITEMS = new ArrayList<>();
    static
    {
        EQUIPABLE_ITEMS.addAll(WEAPONS);
        EQUIPABLE_ITEMS.addAll(PICKAXES);
        EQUIPABLE_ITEMS.addAll(AXES);
        EQUIPABLE_ITEMS.addAll(HOES);
    }

    public static boolean isWeapon(Item item)
    {
        return WEAPONS.contains(item);
    }

    public static boolean isPickaxe(Item item)
    {
        return PICKAXES.contains(item);
    }

    public static boolean isAxe(Item item)
    {
        return AXES.contains(item);
    }

    public static boolean isHoe(Item item)
    {
        return HOES.contains(item);
    }

    public static boolean isEquipable(Item item)
    {
        return EQUIPABLE_ITEMS.contains(item);
    }


    private static List<Item> flowers = new ArrayList<>();
    static
    {
        flowers.add(Item.getItemFromBlock(Blocks.RED_FLOWER));
        flowers.add(Item.getItemFromBlock(Blocks.YELLOW_FLOWER));
    }

    public static boolean isFlower(Item item)
    {
        return flowers.contains(item);
    }


    private static List<ItemStack> upgradeMaterials = new ArrayList<ItemStack>();
    static
    {
        for (BlocklingType type : BlocklingType.blocklingTypes)
        {
            upgradeMaterials.addAll(Arrays.asList(type.upgradeMaterials));
        }
    }

    public static boolean isUpgradeMaterial(ItemStack itemStack)
    {
        for (ItemStack material : upgradeMaterials)
        {
            if (material.getItem() == itemStack.getItem() && material.getMetadata() == itemStack.getMetadata())
            {
                return true;
            }
        }

        return false;
    }
}
