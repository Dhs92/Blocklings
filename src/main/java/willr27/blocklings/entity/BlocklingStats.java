package willr27.blocklings.entity;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class BlocklingStats
{
    private static final DataParameter<Integer> COMBAT_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private BlocklingEntity blockling;
    private EntityDataManager dataManager;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.dataManager = blockling.getDataManager();
    }

    public void registerData()
    {
        dataManager.register(COMBAT_LEVEL, 1);
    }

    public int getCombatLevel() { return dataManager.get(COMBAT_LEVEL); }
    public void setCombatLevel(int value) { dataManager.set(COMBAT_LEVEL, value); }
}
