package willr27.blocklings.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ToolUtil
{
    public static final List<Item> WEAPONS = new ArrayList<>();
    static
    {
        WEAPONS.add(Items.WOODEN_SWORD);
        WEAPONS.add(Items.STONE_SWORD);
        WEAPONS.add(Items.IRON_SWORD);
        WEAPONS.add(Items.GOLDEN_SWORD);
        WEAPONS.add(Items.DIAMOND_SWORD);
    }
    public static boolean isWeapon(Item item)
    {
        return WEAPONS.contains(item);
    }

    public static final List<Item> PICKAXES = new ArrayList<>();
    static
    {
        PICKAXES.add(Items.WOODEN_PICKAXE);
        PICKAXES.add(Items.STONE_PICKAXE);
        PICKAXES.add(Items.IRON_PICKAXE);
        PICKAXES.add(Items.GOLDEN_PICKAXE);
        PICKAXES.add(Items.DIAMOND_PICKAXE);
    }
    public static boolean isPickaxe(Item item)
    {
        return PICKAXES.contains(item);
    }

    public static final List<Item> AXES = new ArrayList<>();
    static
    {
        AXES.add(Items.WOODEN_AXE);
        AXES.add(Items.STONE_AXE);
        AXES.add(Items.IRON_AXE);
        AXES.add(Items.GOLDEN_AXE);
        AXES.add(Items.DIAMOND_AXE);
    }
    public static boolean isAxe(Item item)
    {
        return AXES.contains(item);
    }

    public static final List<Item> HOES = new ArrayList<>();
    static
    {
        HOES.add(Items.WOODEN_HOE);
        HOES.add(Items.STONE_HOE);
        HOES.add(Items.IRON_HOE);
        HOES.add(Items.GOLDEN_HOE);
        HOES.add(Items.DIAMOND_HOE);
    }
    public static boolean isHoe(Item item)
    {
        return HOES.contains(item);
    }

    public static final List<Item> TOOLS = new ArrayList<>();
    static
    {
        TOOLS.addAll(WEAPONS);
        TOOLS.addAll(PICKAXES);
        TOOLS.addAll(AXES);
        TOOLS.addAll(HOES);
    }
    public static boolean isTool(Item item)
    {
        return TOOLS.contains(item);
    }
}
