package willr27.blocklings.entity.blockling;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import java.util.UUID;

public class BlocklingStats
{
    private static final DataParameter<Float> MINING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MINING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WOODCUTTING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WOODCUTTING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FARMING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FARMING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);

    private static final DataParameter<Integer> COMBAT_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MINING_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOODCUTTING_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FARMING_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private static final DataParameter<Integer> COMBAT_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MINING_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOODCUTTING_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FARMING_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private BlocklingEntity blockling;
    private EntityDataManager dataManager;

    private UUID levelBonusHealthUUID;
    private UUID levelBonusAttackDamageUUID;
    private UUID levelBonusArmourUUID;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.dataManager = blockling.getDataManager();

        levelBonusHealthUUID = UUID.randomUUID();
        levelBonusAttackDamageUUID = UUID.randomUUID();
        levelBonusArmourUUID = UUID.randomUUID();
    }

    public void registerData()
    {
        dataManager.register(MINING_RANGE, 2.3f);
        dataManager.register(MINING_RANGE_SQ, dataManager.get(MINING_RANGE) * dataManager.get(MINING_RANGE));
        dataManager.register(WOODCUTTING_RANGE, 2.3f);
        dataManager.register(WOODCUTTING_RANGE_SQ, dataManager.get(WOODCUTTING_RANGE) * dataManager.get(WOODCUTTING_RANGE));
        dataManager.register(FARMING_RANGE, 2.3f);
        dataManager.register(FARMING_RANGE_SQ, dataManager.get(FARMING_RANGE) * dataManager.get(FARMING_RANGE));

        dataManager.register(COMBAT_LEVEL, 1);
    }

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    public void checkForLevelUp()
    {
        int combatLevel = getCombatLevel();
        int combatXp = getCombatXp();
        int combatXpReq = getXpUntilNextLevel(combatLevel);
        if (combatXp >= combatXpReq)
        {
            setCombatLevel(combatLevel + 1);
            setCombatXp(combatXp - combatXpReq);
        }

        int miningLevel = getMiningLevel();
        int miningXp = getMiningXp();
        int miningXpReq = getXpUntilNextLevel(miningLevel);
        if (miningXp >= miningXpReq)
        {
            setMiningLevel(miningLevel + 1);
            setMiningXp(miningXp - miningXpReq);
        }

        int woodcuttingLevel = getWoodcuttingLevel();
        int woodcuttingXp = getWoodcuttingXp();
        int woodcuttingXpReq = getXpUntilNextLevel(woodcuttingLevel);
        if (woodcuttingXp >= woodcuttingXpReq)
        {
            setWoodcuttingLevel(woodcuttingLevel + 1);
            setWoodcuttingXp(woodcuttingXp - woodcuttingXpReq);
        }

        int farmingLevel = getFarmingLevel();
        int farmingXp = getFarmingXp();
        int farmingXpReq = getXpUntilNextLevel(farmingLevel);
        if (farmingXp >= farmingXpReq)
        {
            setFarmingLevel(farmingLevel + 1);
            setFarmingXp(farmingXp - farmingXpReq);
        }
    }

    public void updateLevelStats()
    {
        AttributeModifier levelBonusHealth = new AttributeModifier(levelBonusHealthUUID, "level_bonus_health", calcBonusHealthFromLevel(), AttributeModifier.Operation.ADDITION);
        AttributeModifier levelBonusAttackDamage = new AttributeModifier(levelBonusAttackDamageUUID, "level_bonus_attack_damage", calcBonusDamageFromLevel(), AttributeModifier.Operation.ADDITION);
        AttributeModifier levelBonusArmor = new AttributeModifier(levelBonusArmourUUID, "level_bonus_armour", calcBonusArmorFromLevel(), AttributeModifier.Operation.ADDITION);

        if (blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
        {
            blockling.getAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(levelBonusHealth);
            blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(levelBonusAttackDamage);
            blockling.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(levelBonusArmor);

            blockling.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(levelBonusHealth);
            blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(levelBonusAttackDamage);
            blockling.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(levelBonusArmor);
        }

        updateHealth();
    }

    private double calcBonusHealthFromLevel()
    {
        return 3.0 * Math.log(getCombatLevel());
    }

    private double calcBonusDamageFromLevel()
    {
        return 2.0 * Math.log(getCombatLevel());
    }

    private double calcBonusArmorFromLevel()
    {
        return 4.0 * Math.log(getCombatLevel());
    }


    public void updateHealth()
    {
        if (blockling.getHealth() > blockling.getMaxHealth())
        {
            blockling.setHealth(blockling.getMaxHealth());
        }
    }


    public float getMiningRange() { return dataManager.get(MINING_RANGE); }
    public float getMiningRangeSq() { return dataManager.get(MINING_RANGE_SQ); }
    public void setMiningRange(float value) { dataManager.set(MINING_RANGE, value); dataManager.set(MINING_RANGE_SQ, value * value); }

    public float getWoodcuttingRange() { return dataManager.get(WOODCUTTING_RANGE); }
    public float getWoodcuttingRangeSq() { return dataManager.get(WOODCUTTING_RANGE_SQ); }
    public void setWoodcuttingRange(float value) { dataManager.set(WOODCUTTING_RANGE, value); dataManager.set(WOODCUTTING_RANGE_SQ, value * value); }

    public float getFarmingRange() { return dataManager.get(FARMING_RANGE); }
    public float getFarmingRangeSq() { return dataManager.get(FARMING_RANGE_SQ); }
    public void setFarmingRange(float value) { dataManager.set(FARMING_RANGE, value); dataManager.set(FARMING_RANGE_SQ, value * value); }

    
    public int getCombatLevel() { return dataManager.get(COMBAT_LEVEL); }
    public void incCombatLevel(int value) { setCombatLevel(getCombatLevel() + value); }
    public void setCombatLevel(int value) { dataManager.set(COMBAT_LEVEL, value); updateLevelStats(); }

    public int getMiningLevel() { return dataManager.get(MINING_LEVEL); }
    public void incMiningLevel(int value) { setMiningLevel(getMiningLevel() + value); }
    public void setMiningLevel(int value) { dataManager.set(MINING_LEVEL, value); updateLevelStats(); }

    public int getWoodcuttingLevel() { return dataManager.get(WOODCUTTING_LEVEL); }
    public void incWoodcuttingLevel(int value) { setWoodcuttingLevel(getWoodcuttingLevel() + value); }
    public void setWoodcuttingLevel(int value) { dataManager.set(WOODCUTTING_LEVEL, value); updateLevelStats(); }

    public int getFarmingLevel() { return dataManager.get(FARMING_LEVEL); }
    public void incFarmingLevel(int value) { setFarmingLevel(getFarmingLevel() + value); }
    public void setFarmingLevel(int value) { dataManager.set(FARMING_LEVEL, value); updateLevelStats(); }


    public int getCombatXp() { return dataManager.get(COMBAT_XP); }
    public void incCombatXp(int value) { setCombatXp(getCombatXp() + value); }
    public void setCombatXp(int value) { dataManager.set(COMBAT_XP, value); checkForLevelUp(); }

    public int getMiningXp() { return dataManager.get(MINING_XP); }
    public void incMiningXp(int value) { setMiningXp(getMiningXp() + value); }
    public void setMiningXp(int value) { dataManager.set(MINING_XP, value); checkForLevelUp(); }

    public int getWoodcuttingXp() { return dataManager.get(WOODCUTTING_XP); }
    public void incWoodcuttingXp(int value) { setWoodcuttingXp(getWoodcuttingXp() + value); }
    public void setWoodcuttingXp(int value) { dataManager.set(WOODCUTTING_XP, value); checkForLevelUp(); }

    public int getFarmingXp() { return dataManager.get(FARMING_XP); }
    public void incFarmingXp(int value) { setFarmingXp(getFarmingXp() + value); }
    public void setFarmingXp(int value) { dataManager.set(FARMING_XP, value); checkForLevelUp(); }
}