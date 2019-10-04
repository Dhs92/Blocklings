package willr27.blocklings.inventory.Utilities;

import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.utilities.Utility;

public class ChestInventory extends UtilityInventory
{
    public ChestInventory(BlocklingEntity blockling, int utilityIndex)
    {
        super(blockling, 36, Utility.CHEST, utilityIndex);
    }
}
