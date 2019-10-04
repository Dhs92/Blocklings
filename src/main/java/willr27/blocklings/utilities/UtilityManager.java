package willr27.blocklings.utilities;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.AbstractInventory;
import willr27.blocklings.inventory.Utilities.FurnaceInventory;

import java.util.HashMap;
import java.util.Map;

public class UtilityManager
{
    private static final DataParameter<Integer> UTILITY_1 = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> UTILITY_2 = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private BlocklingEntity blockling;

    private Map<Utility, AbstractInventory> inventories1 = new HashMap<>();
    private Map<Utility, AbstractInventory> inventories2 = new HashMap<>();

    public UtilityManager(BlocklingEntity blockling)
    {
        this.blockling = blockling;

        for (Utility utility : Utility.values())
        {
            inventories1.put(utility, utility.inventory.apply(blockling, 1));
            inventories2.put(utility, utility.inventory.apply(blockling, 2));
        }
    }

    public void registerUtilities()
    {
        blockling.getDataManager().register(UTILITY_1, -1);
        blockling.getDataManager().register(UTILITY_2, -1);
    }

    public void update()
    {
        ((FurnaceInventory) getInventory1(Utility.FURNACE)).tick();
        ((FurnaceInventory) getInventory2(Utility.FURNACE)).tick();
    }

    public Utility getUtility(int index)
    {
        return index == 1 ? getUtility1() : getUtility2();
    }
    public Utility getUtility1()
    {
        int ordinal = blockling.getDataManager().get(UTILITY_1);
        return ordinal != - 1 ? Utility.values()[ordinal] : null;
    }
    public Utility getUtility2()
    {
        int ordinal = blockling.getDataManager().get(UTILITY_2);
        return ordinal != - 1 ? Utility.values()[ordinal] : null;
    }

    public void setUtility(Utility utility, int index)
    {
        if (index == 1) setUtility1(utility);
        else setUtility2(utility);
    }
    public void setUtility1(Utility utility)
    {
        blockling.getDataManager().set(UTILITY_1, utility == null ? -1 : utility.ordinal());
    }
    public void setUtility2(Utility utility)
    {
        blockling.getDataManager().set(UTILITY_2, utility == null ? -1 : utility.ordinal());
    }

    public AbstractInventory getInventory(Utility utility, int index)
    {
        return index == 1 ? getInventory1(utility) : getInventory2(utility);
    }
    public AbstractInventory getInventory1(Utility utility)
    {
        return inventories1.get(utility);
    }
    public AbstractInventory getInventory2(Utility utility)
    {
        return inventories2.get(utility);
    }
}
