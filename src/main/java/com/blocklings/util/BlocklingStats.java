package com.blocklings.util;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.network.NetworkHelper;
import com.blocklings.network.messages.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class BlocklingStats
{
    private float scale = 1.0f;

    private int skillPoints = 0;

    private int combatLevel = 1, miningLevel = 1, woodcuttingLevel = 1, farmingLevel = 1;
    private int combatXp = 0, miningXp = 0, woodcuttingXp = 0, farmingXp = 0;
    private int combatInterval = 10, miningInterval = 20, woodcuttingInterval = 20, farmingInterval = 20;
    private int combatTimer = 20, miningTimer = 20, woodcuttingTimer = 20, farmingTimer = 20;
    private double miningRange = 2, woodcuttingRange = 2, farmingRange = 2;
    private double miningRangeSq = 4, woodcuttingRangeSq = 4, farmingRangeSq = 4;

    private EntityBlockling blockling;

    private UUID typeBonusHealthUUID;
    private UUID typeBonusAttackDamageUUID;
    private UUID typeBonusMovementSpeedUUID;

    public BlocklingStats(EntityBlockling blockling)
    {
        this.blockling = blockling;

        typeBonusHealthUUID = UUID.randomUUID();
        typeBonusAttackDamageUUID = UUID.randomUUID();
        typeBonusMovementSpeedUUID = UUID.randomUUID();
    }

    public void updateBlocklingTypeStats()
    {
        BlocklingType blocklingType = blockling.getBlocklingType();

        AttributeModifier typeBonusHealth = new AttributeModifier(typeBonusHealthUUID, "type_bonus_health", blocklingType.bonusHealth, 0);
        AttributeModifier typeBonusAttackDamage = new AttributeModifier(typeBonusAttackDamageUUID, "type_bonus_attack_damage", blocklingType.bonusAttackDamage, 0);
        AttributeModifier typeBonusMovementSpeed = new AttributeModifier(typeBonusMovementSpeedUUID, "type_bonus_movements_peed", blocklingType.bonusMovementSpeed / 40.0, 0);

        if (blockling.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
        {
            blockling.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(typeBonusHealth);
            blockling.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(typeBonusAttackDamage);
            blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(typeBonusMovementSpeed);

            blockling.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(typeBonusHealth);
            blockling.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(typeBonusAttackDamage);
            blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(typeBonusMovementSpeed);
        }

        updateHealth();
    }

    public void updateHealth()
    {
        if (blockling.getHealth() > blockling.getMaxHealth())
        {
            blockling.setHealth(blockling.getMaxHealth());
        }
    }

    public double getArmour()
    {
        return blockling.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
    }
    public double getAttackDamage()
    {
        return blockling.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    }
    public double getMovementSpeed()
    {
        return blockling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
    }

    public float getScale()
    {
        return scale;
    }
    public void setScale(float value)
    {
        setScale(value, true);
    }
    public void setScale(float value, boolean sync)
    {
        scale = value;
        if (sync) NetworkHelper.sync(blockling.world, new ScaleMessage(scale, blockling.getEntityId()));
    }

    public int getSkillPoints()
    {
        return skillPoints;
    }
    public void incSkillPoints(int amount)
    {
        setSkillPoints(skillPoints + amount);
    }
    public void setSkillPoints(int value)
    {
        setSkillPoints(value, true);
    }
    public void setSkillPoints(int value, boolean sync)
    {
        skillPoints = value;
        if (sync) NetworkHelper.sync(blockling.world, new SkillPointsMessage(skillPoints, blockling.getEntityId()));
    }

    public int getCombatLevel()
    {
        return combatLevel;
    }
    public void incCombatLevel(int amount)
    {
        setCombatLevel(combatLevel + amount);
    }
    public void setCombatLevel(int value)
    {
        setCombatLevel(value, true);
    }
    public void setCombatLevel(int value, boolean sync)
    {
        combatLevel = value;
        if (sync) NetworkHelper.sync(blockling.world, new CombatLevelMessage(combatLevel, blockling.getEntityId()));
    }

    public int getMiningLevel()
    {
        return miningLevel;
    }
    public void incMiningLevel(int amount)
    {
        setMiningLevel(miningLevel + amount);
    }
    public void setMiningLevel(int value)
    {
        setMiningLevel(value, true);
    }
    public void setMiningLevel(int value, boolean sync)
    {
        miningLevel = value;
        if (sync) NetworkHelper.sync(blockling.world, new MiningLevelMessage(miningLevel, blockling.getEntityId()));
    }

    public int getWoodcuttingLevel()
    {
        return woodcuttingLevel;
    }
    public void incWoodcuttingLevel(int amount)
    {
        setWoodcuttingLevel(woodcuttingLevel + amount);
    }
    public void setWoodcuttingLevel(int value)
    {
        setWoodcuttingLevel(value, true);
    }
    public void setWoodcuttingLevel(int value, boolean sync)
    {
        woodcuttingLevel = value;
        if (sync) NetworkHelper.sync(blockling.world, new WoodcuttingLevelMessage(woodcuttingLevel, blockling.getEntityId()));
    }

    public int getFarmingLevel()
    {
        return farmingLevel;
    }
    public void incFarmingLevel(int amount)
    {
        setFarmingLevel(farmingLevel + amount);
    }
    public void setFarmingLevel(int value)
    {
        setFarmingLevel(value, true);
    }
    public void setFarmingLevel(int value, boolean sync)
    {
        farmingLevel = value;
        if (sync) NetworkHelper.sync(blockling.world, new FarmingLevelMessage(farmingLevel, blockling.getEntityId()));
    }

    public int getCombatXp()
    {
        return combatXp;
    }
    public void incCombatXp(int amount)
    {
        setCombatXp(combatXp + amount);
    }
    public void setCombatXp(int value)
    {
        setCombatXp(value, true);
    }
    public void setCombatXp(int value, boolean sync)
    {
        combatXp = value;
        if (sync) NetworkHelper.sync(blockling.world, new CombatXpMessage(combatXp, blockling.getEntityId()));
    }

    public int getMiningXp()
    {
        return miningXp;
    }
    public void incMiningXp(int amount)
    {
        setMiningXp(miningXp + amount);
    }
    public void setMiningXp(int value)
    {
        setMiningXp(value, true);
    }
    public void setMiningXp(int value, boolean sync)
    {
        miningXp = value;
        if (sync) NetworkHelper.sync(blockling.world, new MiningXpMessage(miningXp, blockling.getEntityId()));
    }

    public int getWoodcuttingXp()
    {
        return woodcuttingXp;
    }
    public void incWoodcuttingXp(int amount)
    {
        setWoodcuttingXp(woodcuttingXp + amount);
    }
    public void setWoodcuttingXp(int value)
    {
        setWoodcuttingXp(value, true);
    }
    public void setWoodcuttingXp(int value, boolean sync)
    {
        woodcuttingXp = value;
        if (sync) NetworkHelper.sync(blockling.world, new WoodcuttingXpMessage(woodcuttingXp, blockling.getEntityId()));
    }

    public int getFarmingXp()
    {
        return farmingXp;
    }
    public void incFarmingXp(int amount)
    {
        setFarmingXp(farmingXp + amount);
    }
    public void setFarmingXp(int value)
    {
        setFarmingXp(value, true);
    }
    public void setFarmingXp(int value, boolean sync)
    {
        farmingXp = value;
        if (sync) NetworkHelper.sync(blockling.world, new FarmingXpMessage(farmingXp, blockling.getEntityId()));
    }

    public int getCombatInterval()
    {
        return combatInterval;
    }
    public void incCombatInterval(int amount)
    {
        setCombatInterval(combatInterval + amount);
    }
    public void setCombatInterval(int value)
    {
        setCombatInterval(value, true);
    }
    public void setCombatInterval(int value, boolean sync)
    {
        combatInterval = value;
        if (sync) NetworkHelper.sync(blockling.world, new CombatIntervalMessage(combatInterval, blockling.getEntityId()));
    }

    public int getMiningInterval()
    {
        return miningInterval;
    }
    public void incMiningInterval(int amount)
    {
        setMiningInterval(miningInterval + amount);
    }
    public void setMiningInterval(int value)
    {
        setMiningInterval(value, true);
    }
    public void setMiningInterval(int value, boolean sync)
    {
        miningInterval = value;
        if (sync) NetworkHelper.sync(blockling.world, new MiningIntervalMessage(miningInterval, blockling.getEntityId()));
    }

    public int getWoodcuttingInterval()
    {
        return woodcuttingInterval;
    }
    public void incWoodcuttingInterval(int amount)
    {
        setWoodcuttingInterval(woodcuttingInterval + amount);
    }
    public void setWoodcuttingInterval(int value)
    {
        setWoodcuttingInterval(value, true);
    }
    public void setWoodcuttingInterval(int value, boolean sync)
    {
        woodcuttingInterval = value;
        if (sync) NetworkHelper.sync(blockling.world, new WoodcuttingIntervalMessage(woodcuttingInterval, blockling.getEntityId()));
    }

    public int getFarmingInterval()
    {
        return farmingInterval;
    }
    public void incFarmingInterval(int amount)
    {
        setFarmingInterval(farmingInterval + amount);
    }
    public void setFarmingInterval(int value)
    {
        setFarmingInterval(value, true);
    }
    public void setFarmingInterval(int value, boolean sync)
    {
        farmingInterval = value;
        if (sync) NetworkHelper.sync(blockling.world, new FarmingIntervalMessage(farmingInterval, blockling.getEntityId()));
    }

    public int getCombatTimer()
    {
        return combatTimer;
    }
    public void incCombatTimer(int amount)
    {
        setCombatTimer(combatTimer + amount);
    }
    public void setCombatTimer(int value)
    {
        setCombatTimer(value, true);
    }
    public void setCombatTimer(int value, boolean sync)
    {
        combatTimer = value;
        if (sync) NetworkHelper.sync(blockling.world, new CombatTimerMessage(combatTimer, blockling.getEntityId()));
    }

    public int getMiningTimer()
    {
        return miningTimer;
    }
    public void incMiningTimer(int amount)
    {
        setMiningTimer(miningTimer + amount);
    }
    public void setMiningTimer(int value)
    {
        setMiningTimer(value, true);
    }
    public void setMiningTimer(int value, boolean sync)
    {
        miningTimer = value;
        if (sync) NetworkHelper.sync(blockling.world, new MiningTimerMessage(miningTimer, blockling.getEntityId()));
    }

    public int getWoodcuttingTimer()
    {
        return woodcuttingTimer;
    }
    public void incWoodcuttingTimer(int amount)
    {
        setWoodcuttingTimer(woodcuttingTimer + amount);
    }
    public void setWoodcuttingTimer(int value)
    {
        setWoodcuttingTimer(value, true);
    }
    public void setWoodcuttingTimer(int value, boolean sync)
    {
        woodcuttingTimer = value;
        if (sync) NetworkHelper.sync(blockling.world, new WoodcuttingTimerMessage(woodcuttingTimer, blockling.getEntityId()));
    }

    public int getFarmingTimer()
    {
        return farmingTimer;
    }
    public void incFarmingTimer(int amount)
    {
        setFarmingTimer(farmingTimer + amount);
    }
    public void setFarmingTimer(int value)
    {
        setFarmingTimer(value, true);
    }
    public void setFarmingTimer(int value, boolean sync)
    {
        farmingTimer = value;
        if (sync) NetworkHelper.sync(blockling.world, new FarmingTimerMessage(farmingTimer, blockling.getEntityId()));
    }

    public double getMiningRange()
    {
        return miningRange;
    }
    public double getMiningRangeSq()
    {
        return miningRangeSq;
    }
    public void setMiningRange(double value)
    {
        setMiningRange(value, true);
    }
    public void setMiningRange(double value, boolean sync)
    {
        miningRange = value;
        miningRangeSq = miningRange * miningRange;
        if (sync) NetworkHelper.sync(blockling.world, new MiningRangeMessage(miningRange, blockling.getEntityId()));
    }

    public double getWoodcuttingRange()
    {
        return woodcuttingRange;
    }
    public double getWoodcuttingRangeSq()
    {
        return woodcuttingRangeSq;
    }
    public void setWoodcuttingRange(double value)
    {
        setWoodcuttingRange(value, true);
    }
    public void setWoodcuttingRange(double value, boolean sync)
    {
        woodcuttingRange = value;
        woodcuttingRangeSq = woodcuttingRange * woodcuttingRange;
        if (sync) NetworkHelper.sync(blockling.world, new WoodcuttingRangeMessage(woodcuttingRange, blockling.getEntityId()));
    }

    public double getFarmingRange()
    {
        return farmingRange;
    }
    public double getFarmingRangeSq()
    {
        return farmingRangeSq;
    }
    public void setFarmingRange(double value)
    {
        setFarmingRange(value, true);
    }
    public void setFarmingRange(double value, boolean sync)
    {
        farmingRange = value;
        farmingRangeSq = farmingRange * farmingRange;
        if (sync) NetworkHelper.sync(blockling.world, new FarmingRangeMessage(farmingRange, blockling.getEntityId()));
    }
}
