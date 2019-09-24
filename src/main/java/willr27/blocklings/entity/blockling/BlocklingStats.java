package willr27.blocklings.entity.blockling;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import willr27.blocklings.item.ToolType;

import java.util.Random;
import java.util.UUID;

public class BlocklingStats
{
    public static final int COMBAT_LEVEL_ID = 0;
    public static final int MINING_LEVEL_ID = 1;
    public static final int WOODCUTTING_LEVEL_ID = 2;
    public static final int FARMING_LEVEL_ID = 3;

    private static final DataParameter<Float> MINING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MINING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WOODCUTTING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WOODCUTTING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FARMING_RANGE = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FARMING_RANGE_SQ = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.FLOAT);

    private static final DataParameter<Integer> COMBAT_INTERVAL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MINING_INTERVAL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOODCUTTING_INTERVAL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FARMING_INTERVAL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private static final DataParameter<Integer> COMBAT_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MINING_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOODCUTTING_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FARMING_LEVEL = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private static final DataParameter<Integer> COMBAT_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MINING_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOODCUTTING_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FARMING_XP = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private static final DataParameter<Integer> SKILL_POINTS = EntityDataManager.createKey(BlocklingEntity.class, DataSerializers.VARINT);

    private BlocklingEntity blockling;
    private EntityDataManager dataManager;

    private UUID levelBonusHealthUUID;
    private UUID levelBonusAttackDamageUUID;
    private UUID levelBonusArmourUUID;

    private UUID typeBonusHealthUUID;
    private UUID typeBonusAttackDamageUUID;
    private UUID typeBonusArmourUUID;
    private UUID typeBonusMovementSpeedUUID;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.dataManager = blockling.getDataManager();

        levelBonusHealthUUID = UUID.randomUUID();
        levelBonusAttackDamageUUID = UUID.randomUUID();
        levelBonusArmourUUID = UUID.randomUUID();

        typeBonusHealthUUID = UUID.randomUUID();
        typeBonusAttackDamageUUID = UUID.randomUUID();
        typeBonusArmourUUID = UUID.randomUUID();
        typeBonusMovementSpeedUUID = UUID.randomUUID();
    }

    public void registerData()
    {
        blockling.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0);
        blockling.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
        blockling.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0);
        blockling.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);

        dataManager.register(MINING_RANGE, 2.3f);
        dataManager.register(MINING_RANGE_SQ, dataManager.get(MINING_RANGE) * dataManager.get(MINING_RANGE));
        dataManager.register(WOODCUTTING_RANGE, 2.3f);
        dataManager.register(WOODCUTTING_RANGE_SQ, dataManager.get(WOODCUTTING_RANGE) * dataManager.get(WOODCUTTING_RANGE));
        dataManager.register(FARMING_RANGE, 2.3f);
        dataManager.register(FARMING_RANGE_SQ, dataManager.get(FARMING_RANGE) * dataManager.get(FARMING_RANGE));

        dataManager.register(COMBAT_INTERVAL, 50);
        dataManager.register(MINING_INTERVAL, 50);
        dataManager.register(WOODCUTTING_INTERVAL, 50);
        dataManager.register(FARMING_INTERVAL, 50);

        dataManager.register(COMBAT_LEVEL, new Random().nextInt(80) + 1);
        dataManager.register(MINING_LEVEL, new Random().nextInt(80) + 1);
        dataManager.register(WOODCUTTING_LEVEL, new Random().nextInt(80) + 1);
        dataManager.register(FARMING_LEVEL, new Random().nextInt(80) + 1);
        dataManager.register(COMBAT_XP, new Random().nextInt(getXpUntilNextLevel(getCombatLevel())));
        dataManager.register(MINING_XP, new Random().nextInt(getXpUntilNextLevel(getMiningLevel())));
        dataManager.register(WOODCUTTING_XP, new Random().nextInt(getXpUntilNextLevel(getWoodcuttingLevel())));
        dataManager.register(FARMING_XP, new Random().nextInt(getXpUntilNextLevel(getFarmingLevel())));

        dataManager.register(SKILL_POINTS, 50);

        blockling.setBlocklingType(BlocklingType.DIAMOND, false); // TODO: MOVE?

        checkForLevelUp();
        updateStateBonuses();
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

    public void updateTypeBonuses()
    {
        BlocklingType type = blockling.getBlocklingType();
        AttributeModifier typeBonusHealth = new AttributeModifier(typeBonusHealthUUID, "type_bonus_health", type.getBonusHealth(), AttributeModifier.Operation.ADDITION);
        AttributeModifier typeBonusAttackDamage = new AttributeModifier(typeBonusAttackDamageUUID, "type_bonus_attack_damage", type.getBonusDamage(), AttributeModifier.Operation.ADDITION);
        AttributeModifier typeBonusArmor = new AttributeModifier(typeBonusArmourUUID, "type_bonus_armour", type.getBonusArmour(), AttributeModifier.Operation.ADDITION);
        AttributeModifier typeBonusMovementSpeed = new AttributeModifier(typeBonusMovementSpeedUUID, "type_bonus_movement_speed", type.getBonusSpeed() / 40.0, AttributeModifier.Operation.ADDITION);


        if (blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
        {
            blockling.getAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(typeBonusHealth);
            blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(typeBonusAttackDamage);
            blockling.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(typeBonusArmor);
            blockling.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(typeBonusMovementSpeed);

            blockling.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(typeBonusHealth);
            blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(typeBonusAttackDamage);
            blockling.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(typeBonusArmor);
            blockling.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(typeBonusMovementSpeed);
        }

        updateHealth();
    }

    public void updateStateBonuses()
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

        updateIntervalBonuses();
        updateHealth();
    }

    public void updateIntervalBonuses()
    {
        if (blockling.inventory == null) return;

        ItemStack mainStack = blockling.getHeldItemMainhand();
        ItemStack offStack = blockling.getHeldItemOffhand();
        Item mainItem = mainStack.getItem();
        Item offItem = offStack.getItem();
        ToolType mainType = ToolType.getToolType(mainItem);
        ToolType offType = ToolType.getToolType(offItem);

        boolean matching = mainType == offType;
        if (matching)
        {
            if (mainType == ToolType.WEAPON || offType == ToolType.WEAPON)
            {
                setCombatInterval((int)(calcBreakSpeedFromLevel(getCombatLevel()) / 1.5));
                setMiningInterval(calcBreakSpeedFromLevel(getMiningLevel()));
                setWoodcuttingInterval(calcBreakSpeedFromLevel(getWoodcuttingLevel()));
                setFarmingInterval(calcBreakSpeedFromLevel(getFarmingLevel()));
            }
            else if (mainType == ToolType.PICKAXE || offType == ToolType.PICKAXE)
            {
                setMiningInterval((int)(calcBreakSpeedFromLevel(getMiningLevel()) / 1.5));
                setCombatInterval(calcBreakSpeedFromLevel(getCombatLevel()));
                setWoodcuttingInterval(calcBreakSpeedFromLevel(getWoodcuttingLevel()));
                setFarmingInterval(calcBreakSpeedFromLevel(getFarmingLevel()));
            }
            else if (mainType == ToolType.AXE || offType == ToolType.AXE)
            {
                setWoodcuttingInterval((int)(calcBreakSpeedFromLevel(getWoodcuttingLevel()) / 1.5));
                setCombatInterval(calcBreakSpeedFromLevel(getCombatLevel()));
                setMiningInterval(calcBreakSpeedFromLevel(getMiningLevel()));
                setFarmingInterval(calcBreakSpeedFromLevel(getFarmingLevel()));
            }
            else if (mainType == ToolType.HOE || offType == ToolType.HOE)
            {
                setFarmingInterval((int)(calcBreakSpeedFromLevel(getFarmingLevel()) / 1.5));
                setCombatInterval(calcBreakSpeedFromLevel(getCombatLevel()));
                setMiningInterval(calcBreakSpeedFromLevel(getMiningLevel()));
                setWoodcuttingInterval(calcBreakSpeedFromLevel(getWoodcuttingLevel()));
            }
        }
        else
        {
            setCombatInterval(calcBreakSpeedFromLevel(getCombatLevel()));
            setMiningInterval(calcBreakSpeedFromLevel(getMiningLevel()));
            setWoodcuttingInterval(calcBreakSpeedFromLevel(getWoodcuttingLevel()));
            setFarmingInterval(calcBreakSpeedFromLevel(getFarmingLevel()));
        }
    }

    private int calcBreakSpeedFromLevel(int level)
    {
        return 50 -((int) (10 * Math.log(level)));
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


    public double getAttackDamage() { return blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue(); }
    public double getArmour() { return blockling.getAttribute(SharedMonsterAttributes.ARMOR).getValue(); }
    public double getMovementSpeed() { return blockling.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue(); }


    public float getMiningRange() { return dataManager.get(MINING_RANGE); }
    public float getMiningRangeSq() { return dataManager.get(MINING_RANGE_SQ); }
    public void setMiningRange(float value) { dataManager.set(MINING_RANGE, value); dataManager.set(MINING_RANGE_SQ, value * value); }

    public float getWoodcuttingRange() { return dataManager.get(WOODCUTTING_RANGE); }
    public float getWoodcuttingRangeSq() { return dataManager.get(WOODCUTTING_RANGE_SQ); }
    public void setWoodcuttingRange(float value) { dataManager.set(WOODCUTTING_RANGE, value); dataManager.set(WOODCUTTING_RANGE_SQ, value * value); }

    public float getFarmingRange() { return dataManager.get(FARMING_RANGE); }
    public float getFarmingRangeSq() { return dataManager.get(FARMING_RANGE_SQ); }
    public void setFarmingRange(float value) { dataManager.set(FARMING_RANGE, value); dataManager.set(FARMING_RANGE_SQ, value * value); }


    public int getCombatInterval() { return dataManager.get(COMBAT_INTERVAL); }
    public void setCombatInterval(int value) { dataManager.set(COMBAT_INTERVAL, value); }

    public int getMiningInterval() { return dataManager.get(MINING_INTERVAL); }
    public void setMiningInterval(int value) { dataManager.set(MINING_INTERVAL, value); }

    public int getWoodcuttingInterval() { return dataManager.get(WOODCUTTING_INTERVAL); }
    public void setWoodcuttingInterval(int value) { dataManager.set(WOODCUTTING_INTERVAL, value); }

    public int getFarmingInterval() { return dataManager.get(FARMING_INTERVAL); }
    public void setFarmingInterval(int value) { dataManager.set(FARMING_INTERVAL, value); }



    public String getLevelName(int levelId)
    {
        switch (levelId)
        {
            case COMBAT_LEVEL_ID: return "Combat";
            case MINING_LEVEL_ID: return "Mining";
            case WOODCUTTING_LEVEL_ID: return "Woodcutting";
            case FARMING_LEVEL_ID: return "Farming";
        }

        return null;
    }

    public int getLevel(int levelId)
    {
        switch (levelId)
        {
            case COMBAT_LEVEL_ID: return getCombatLevel();
            case MINING_LEVEL_ID: return getMiningLevel();
            case WOODCUTTING_LEVEL_ID: return getWoodcuttingLevel();
            case FARMING_LEVEL_ID: return getFarmingLevel();
        }

        return -1;
    }

    public void setLevel(int levelId, int value)
    {
        switch (levelId)
        {
            case COMBAT_LEVEL_ID: setCombatLevel(value); break;
            case MINING_LEVEL_ID: setMiningLevel(value); break;
            case WOODCUTTING_LEVEL_ID: setWoodcuttingLevel(value); break;
            case FARMING_LEVEL_ID: setFarmingLevel(value); break;
        }
    }
    
    public int getCombatLevel() { return dataManager.get(COMBAT_LEVEL); }
    public void incCombatLevel(int value) { setCombatLevel(getCombatLevel() + value); }
    public void setCombatLevel(int value) { dataManager.set(COMBAT_LEVEL, value); updateStateBonuses(); }

    public int getMiningLevel() { return dataManager.get(MINING_LEVEL); }
    public void incMiningLevel(int value) { setMiningLevel(getMiningLevel() + value); }
    public void setMiningLevel(int value) { dataManager.set(MINING_LEVEL, value); updateStateBonuses(); }

    public int getWoodcuttingLevel() { return dataManager.get(WOODCUTTING_LEVEL); }
    public void incWoodcuttingLevel(int value) { setWoodcuttingLevel(getWoodcuttingLevel() + value); }
    public void setWoodcuttingLevel(int value) { dataManager.set(WOODCUTTING_LEVEL, value); updateStateBonuses(); }

    public int getFarmingLevel() { return dataManager.get(FARMING_LEVEL); }
    public void incFarmingLevel(int value) { setFarmingLevel(getFarmingLevel() + value); }
    public void setFarmingLevel(int value) { dataManager.set(FARMING_LEVEL, value); updateStateBonuses(); }


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

    public int getSkillPoints() { return dataManager.get(SKILL_POINTS); }
    public void incSkillPoints(int value) { setSkillPoints(getSkillPoints() + value); }
    public void setSkillPoints(int value) { dataManager.set(SKILL_POINTS, value); }
}
