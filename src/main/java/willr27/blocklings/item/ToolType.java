package willr27.blocklings.item;

import net.minecraft.item.Item;

public enum ToolType
{
    ANY,
    WEAPON,
    PICKAXE,
    AXE,
    HOE;

    public boolean is(Item item)
    {
        return isTooltype(this, item);
    }

    public static boolean isTooltype(ToolType type, Item item)
    {
        switch (type)
        {
            case ANY: return ToolUtil.isTool(item);
            case WEAPON: return ToolUtil.isWeapon(item);
            case PICKAXE: return ToolUtil.isPickaxe(item);
            case AXE: return ToolUtil.isAxe(item);
            case HOE: return ToolUtil.isHoe(item);
        }

        return false;
    }
}
