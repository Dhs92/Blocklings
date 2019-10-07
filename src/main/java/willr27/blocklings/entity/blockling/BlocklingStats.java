package willr27.blocklings.entity.blockling;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import willr27.blocklings.item.ToolType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlocklingStats
{
    public final List<BlocklingAttribute> attributes = new ArrayList<>();
    public final List<BlocklingAttribute> levels = new ArrayList<>();

    public final BlocklingAttribute combatInterval;
    public final BlocklingAttributeModifier combatIntervalLevelModifier;
    public final BlocklingAttributeModifier combatIntervalToolModifier;
    public final BlocklingAttribute miningInterval;
    public final BlocklingAttributeModifier miningIntervalLevelModifier;
    public final BlocklingAttributeModifier miningIntervalToolModifier;
    public final BlocklingAttributeModifier miningIntervalFasterMiningAbilityModifier;
    public final BlocklingAttributeModifier miningIntervalFasterMiningEnhancedAbilityModifier;
    public final BlocklingAttribute woodcuttingInterval;
    public final BlocklingAttributeModifier woodcuttingIntervalLevelModifier;
    public final BlocklingAttributeModifier woodcuttingIntervalToolModifier;
    public final BlocklingAttribute farmingInterval;
    public final BlocklingAttributeModifier farmingIntervalLevelModifier;
    public final BlocklingAttributeModifier farmingIntervalToolModifier;

    public final BlocklingAttribute combatLevel;
    public final BlocklingAttribute miningLevel;
    public final BlocklingAttribute woodcuttingLevel;
    public final BlocklingAttribute farmingLevel;

    public final BlocklingAttribute combatXp;
    public final BlocklingAttribute miningXp;
    public final BlocklingAttribute woodcuttingXp;
    public final BlocklingAttribute farmingXp;

    public final BlocklingAttribute skillPoints;

    public final BlocklingAttribute miningRange;
    public final BlocklingAttribute miningRangeSq;
    public final BlocklingAttribute woodcuttingRange;
    public final BlocklingAttribute woodcuttingRangeSq;
    public final BlocklingAttribute farmingRange;
    public final BlocklingAttribute farmingRangeSq;

    public final BlocklingAttribute maxHealth;
    public final BlocklingAttributeModifier maxHealthCombatLevelModifier;
    public final BlocklingAttributeModifier maxHealthTypeModifier;
    public final BlocklingAttribute damage;
    public final BlocklingAttributeModifier damageCombatLevelModifier;
    public final BlocklingAttributeModifier damageTypeModifier;
    public final BlocklingAttribute armour;
    public final BlocklingAttributeModifier armourCombatLevelModifier;
    public final BlocklingAttributeModifier armourTypeModifier;
    public final BlocklingAttribute movementSpeed;
    public final BlocklingAttributeModifier movementSpeedTypeModifier;

    private BlocklingEntity blockling;
    private World world;
    private EntityDataManager dataManager;

    public BlocklingStats(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        this.world = blockling.world;
        this.dataManager = blockling.getDataManager();

        combatInterval = createAttribute("combatInterval", "Combat Interval", 10.0f, true);
        combatIntervalLevelModifier = new BlocklingAttributeModifier(combatInterval, "combatIntervalLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        combatIntervalToolModifier = new BlocklingAttributeModifier(combatInterval, "combatIntervalToolModifier", 0.75f, BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL);
        miningInterval = createAttribute("miningInterval", "Mining Interval", 10.0f, true);
        miningIntervalLevelModifier = new BlocklingAttributeModifier(miningInterval, "miningIntervalLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        miningIntervalToolModifier = new BlocklingAttributeModifier(miningInterval, "miningIntervalToolModifier", 0.75f, BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL);
        miningIntervalFasterMiningAbilityModifier = new BlocklingAttributeModifier(miningInterval, "miningIntervalFasterMiningAbilityModifier", 0.9f, BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL);
        miningIntervalFasterMiningEnhancedAbilityModifier = new BlocklingAttributeModifier(miningInterval, "miningIntervalFasterMiningEnhancedAbilityModifier", 0.9f, BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL);
        woodcuttingInterval = createAttribute("woodcuttingInterval", "Woodcutting Interval", 10.0f, true);
        woodcuttingIntervalLevelModifier = new BlocklingAttributeModifier(woodcuttingInterval, "woodcuttingIntervalLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        woodcuttingIntervalToolModifier = new BlocklingAttributeModifier(woodcuttingInterval, "woodcuttingIntervalToolModifier", 0.75f, BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL);
        farmingInterval = createAttribute("farmingInterval", "Farming Interval", 10.0f, true);
        farmingIntervalLevelModifier = new BlocklingAttributeModifier(farmingInterval, "farmingIntervalLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        farmingIntervalToolModifier = new BlocklingAttributeModifier(farmingInterval, "farmingIntervalToolModifier", 0.75f, BlocklingAttributeModifier.Operation.MULTIPLY_TOTAL);

        combatLevel = createAttribute("combatLevel", "Combat Level", new Random().nextInt(99) + 1, true);
        combatLevel.setCallback(() -> { combatIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) combatLevel.getFloat())); updateCombatLevelBonuses(); });
        levels.add(combatLevel);
        miningLevel = createAttribute("miningLevel", "Mining Level", new Random().nextInt(99) + 1, true);
        miningLevel.setCallback(() -> { miningIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) miningLevel.getFloat())); });
        levels.add(miningLevel);
        woodcuttingLevel = createAttribute("woodcuttingLevel", "Woodcutting Level", new Random().nextInt(99) + 1, true);
        woodcuttingLevel.setCallback(() -> { woodcuttingIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) woodcuttingLevel.getFloat())); });
        levels.add(woodcuttingLevel);
        farmingLevel = createAttribute("farmingLevel", "Farming Level", new Random().nextInt(99) + 1, true);
        farmingLevel.setCallback(() -> { farmingIntervalLevelModifier.setValue(calcBreakSpeedFromLevel((int) farmingLevel.getFloat())); });
        levels.add(farmingLevel);

        combatXp = createAttribute("combatXp", "Combat XP", getXpUntilNextLevel(combatLevel.getInt()), true);
        combatXp.setCallback(() -> { checkForLevelUp(); });
        miningXp = createAttribute("miningXp", "Mining XP", getXpUntilNextLevel(miningLevel.getInt()), true);
        miningXp.setCallback(() -> { checkForLevelUp(); });
        woodcuttingXp = createAttribute("woodcuttingXp", "Woodcutting XP", getXpUntilNextLevel(woodcuttingLevel.getInt()), true);
        woodcuttingXp.setCallback(() -> { checkForLevelUp(); });
        farmingXp = createAttribute("farmingXp", "Farming XP", getXpUntilNextLevel(farmingLevel.getInt()), true);
        farmingXp.setCallback(() -> { checkForLevelUp(); });

        skillPoints = createAttribute("skillPoints", "Skill Points", 50.0f, true);

        miningRange = createAttribute("miningRange", "Mining Range", 2.5f, false);
        miningRangeSq = createAttribute("miningRangeSq", "Mining Range Sq", miningRange.getFloat() * miningRange.getFloat(), false);
        miningRange.setCallback(() -> { miningRangeSq.setBaseValue(miningRange.getFloat() * miningRange.getFloat()); });
        woodcuttingRange = createAttribute("woodcuttingRange", "Woodcutting Range", 2.5f, false);
        woodcuttingRangeSq = createAttribute("woodcuttingRangeSq", "Woodcutting Range Sq", woodcuttingRange.getFloat() * woodcuttingRange.getFloat(), false);
        woodcuttingRange.setCallback(() -> { woodcuttingRangeSq.setBaseValue(woodcuttingRange.getFloat() * woodcuttingRange.getFloat()); });
        farmingRange = createAttribute("farmingRange", "Farming Range", 2.5f, false);
        farmingRangeSq = createAttribute("farmingRangeSq", "Farming Range Squared", farmingRange.getFloat() * farmingRange.getFloat(), false);
        farmingRange.setCallback(() -> { farmingRangeSq.setBaseValue(farmingRange.getFloat() * farmingRange.getFloat()); });

        maxHealth = createAttribute("maxHealth", "Max Health", 5.0f, true);
        maxHealth.setCallback(() -> { blockling.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth.getFloat()); updateHealth(); });
        maxHealthCombatLevelModifier = new BlocklingAttributeModifier(maxHealth, "maxHealthCombatLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        maxHealthTypeModifier  = new BlocklingAttributeModifier(maxHealth, "maxHealthTypeModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        damage = createAttribute("damage", "Damage", 1.0f, true);
        damage.setCallback(() -> { blockling.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage.getFloat()); });
        damageCombatLevelModifier = new BlocklingAttributeModifier(damage, "damageCombatLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        damageTypeModifier = new BlocklingAttributeModifier(damage, "damageTypeModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        armour = createAttribute("armour", "Armour", 2.0f, true);
        armour.setCallback(() -> { blockling.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(armour.getFloat()); });
        armourCombatLevelModifier = new BlocklingAttributeModifier(armour, "armourCombatLevelModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        armourTypeModifier = new BlocklingAttributeModifier(armour, "armourTypeModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
        movementSpeed = createAttribute("movementSpeed", "Speed", 0.3f, true);
        maxHealth.setCallback(() -> { blockling.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed.getFloat()); });
        movementSpeedTypeModifier  = new BlocklingAttributeModifier(movementSpeed, "movementSpeedTypeModifier", 0.0f, BlocklingAttributeModifier.Operation.ADDITION);
    }

    private BlocklingAttribute createAttribute(String name, String displayName, float baseValue, boolean displayAsInt)
    {
        BlocklingAttribute attribute = new BlocklingAttribute(blockling, name, displayName, baseValue, displayAsInt);
        attributes.add(attribute);
        return attribute;
    }

    public void initAttributes()
    {
        blockling.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        combatInterval.addModifier(combatIntervalLevelModifier);
        miningInterval.addModifier(miningIntervalLevelModifier);
        woodcuttingInterval.addModifier(woodcuttingIntervalLevelModifier);
        farmingInterval.addModifier(farmingIntervalLevelModifier);

        maxHealth.addModifier(maxHealthCombatLevelModifier);
        damage.addModifier(damageCombatLevelModifier);
        armour.addModifier(armourCombatLevelModifier);

        blockling.setBlocklingType(BlocklingType.OAK_LOG, false); // TODO: MOVE?

        if (!world.isRemote)
        {
            for (BlocklingAttribute attribute : attributes)
            {
                attribute.calculateValue();
            }
        }
        // TODO: NEEDED? ^ V
        updateCombatLevelBonuses();
        updateTypeBonuses();
    }

    public static int getXpUntilNextLevel(int level)
    {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    public void checkForLevelUp()
    {
        int combatLevel = this.combatLevel.getInt();
        int combatXp = this.combatXp.getInt();
        int combatXpReq = getXpUntilNextLevel(combatLevel);
        if (combatXp >= combatXpReq)
        {
            this.combatLevel.setBaseValue(combatLevel + 1);
            this.combatXp.setBaseValue(combatXp - combatXpReq);
        }

        int miningLevel = this.miningLevel.getInt();
        int miningXp = this.miningXp.getInt();
        int miningXpReq = getXpUntilNextLevel(miningLevel);
        if (miningXp >= miningXpReq)
        {
            this.miningLevel.setBaseValue(miningLevel + 1);
            this.miningXp.setBaseValue(miningXp - miningXpReq);
        }

        int woodcuttingLevel = this.woodcuttingLevel.getInt();
        int woodcuttingXp = this.woodcuttingXp.getInt();
        int woodcuttingXpReq = getXpUntilNextLevel(woodcuttingLevel);
        if (woodcuttingXp >= woodcuttingXpReq)
        {
            this.woodcuttingLevel.setBaseValue(woodcuttingLevel + 1);
            this.woodcuttingXp.setBaseValue(woodcuttingXp - woodcuttingXpReq);
        }

        int farmingLevel = this.farmingLevel.getInt();
        int farmingXp = this.farmingXp.getInt();
        int farmingXpReq = getXpUntilNextLevel(farmingLevel);
        if (farmingXp >= farmingXpReq)
        {
            this.farmingLevel.setBaseValue(farmingLevel + 1);
            this.farmingXp.setBaseValue(farmingXp - farmingXpReq);
        }
    }

    public void updateCombatLevelBonuses()
    {
        maxHealthCombatLevelModifier.setValue(calcBonusHealthFromCombatLevel());
        damageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel());
        armourCombatLevelModifier.setValue(calcBonusArmorFromCombatLevel());
    }

    public void updateTypeBonuses()
    {
        BlocklingType type = blockling.getBlocklingType();
        maxHealthTypeModifier.setValue(type.getBonusHealth());
        damageTypeModifier.setValue(type.getBonusDamage());
        armourTypeModifier.setValue(type.getBonusArmour());
        movementSpeedTypeModifier.setValue(type.getBonusSpeed());
    }

    public void updateItemBonuses()
    {
        if (blockling.inventory == null) return;

        ItemStack mainStack = blockling.getHeldItemMainhand();
        ItemStack offStack = blockling.getHeldItemOffhand();
        Item mainItem = mainStack.getItem();
        Item offItem = offStack.getItem();
        ToolType mainType = ToolType.getToolType(mainItem);
        ToolType offType = ToolType.getToolType(offItem);

        // Remove all modifiers first
        combatInterval.removeModifier(combatIntervalToolModifier);
        miningInterval.removeModifier(miningIntervalToolModifier);
        woodcuttingInterval.removeModifier(woodcuttingIntervalToolModifier);
        farmingInterval.removeModifier(farmingIntervalToolModifier);

        // Add appropriate modifier if conditions are met
        boolean matching = mainType == offType;
        if (matching)
        {
            if (mainType == ToolType.WEAPON || offType == ToolType.WEAPON)
            {
                combatInterval.addModifier(combatIntervalToolModifier);
            }
            else if (mainType == ToolType.PICKAXE || offType == ToolType.PICKAXE)
            {
                miningInterval.addModifier(miningIntervalToolModifier);
            }
            else if (mainType == ToolType.AXE || offType == ToolType.AXE)
            {
                woodcuttingInterval.addModifier(woodcuttingIntervalToolModifier);
            }
            else if (mainType == ToolType.HOE || offType == ToolType.HOE)
            {
                farmingInterval.addModifier(farmingIntervalToolModifier);
            }
        }
    }

    private int calcBreakSpeedFromLevel(int level)
    {
        return 50 -((int) (10 * Math.log(level)));
    }

    private float calcBonusHealthFromCombatLevel()
    {
        return (float) (3.0f * Math.log(combatLevel.getFloat()));
    }

    private float calcBonusDamageFromCombatLevel()
    {
        return (float) (2.0f * Math.log(combatLevel.getFloat()));
    }

    private float calcBonusArmorFromCombatLevel()
    {
        return (float) (0.5f * Math.log(combatLevel.getFloat()));
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



    public BlocklingAttribute getAttribute(String name)
    {
        for (BlocklingAttribute attribute : attributes)
        {
            if (attribute.name == name)
            {
                return attribute;
            }
        }

        return null;
    }
}
