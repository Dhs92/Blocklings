package willr27.blocklings.entity.blockling;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class BlocklingStats
{
    private static final DataParameter<Float> MINING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MINING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);

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
        dataManager.register(MINING_RANGE, 2.3f);
        dataManager.register(MINING_RANGE_SQ, dataManager.get(MINING_RANGE) * dataManager.get(MINING_RANGE));

        dataManager.register(COMBAT_LEVEL, 1);
    }

    public int getCombatLevel() { return dataManager.get(COMBAT_LEVEL); }
    public void setCombatLevel(int value) { dataManager.set(COMBAT_LEVEL, value); }

    public float getMiningRange() { return dataManager.get(MINING_RANGE); }
    public float getMiningRangeSq() { return dataManager.get(MINING_RANGE_SQ); }
    public void setMiningRange(float value) { dataManager.set(MINING_RANGE, value); dataManager.set(MINING_RANGE_SQ, value * value); }
}
