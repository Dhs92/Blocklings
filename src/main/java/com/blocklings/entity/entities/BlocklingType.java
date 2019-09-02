package com.blocklings.entity.entities;

import com.blocklings.item.BlocklingsItems;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BlocklingType
{
    public static List<BlocklingType> blocklingTypes = new ArrayList<>();
    static
    {
        blocklingTypes.add(new BlocklingType("blockling_grass", new ItemStack[] { new ItemStack(Blocks.GRASS), new ItemStack(Blocks.DIRT) },
            0.0, 0.0, 0.0, 0.0));
        blocklingTypes.add(new BlocklingType("blockling_oak_log", new ItemStack[] { new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.LOG2, 1, 1) },
            2.0, 1.0, 2.0, 1.0));
        blocklingTypes.add(new BlocklingType("blockling_stone", new ItemStack(Blocks.STONE),
            5.0, 1.0, 1.0, -1.0));
        blocklingTypes.add(new BlocklingType("blockling_iron", new ItemStack(Items.IRON_INGOT),
            7.0, 3.0, 2.0, 0.0));
        blocklingTypes.add(new BlocklingType("blockling_quartz", new ItemStack(Items.QUARTZ),
            0.0, 4.0, 3.0, 2.0));
        blocklingTypes.add(new BlocklingType("blockling_gold", new ItemStack(Items.GOLD_INGOT),
            0.0, 3.0, 5.0, 5.0));
        blocklingTypes.add(new BlocklingType("blockling_lapis", new ItemStack(Items.DYE, 1, 4),
            3.0, 3.0, 3.0, 2.0));
        blocklingTypes.add(new BlocklingType("blockling_emerald", new ItemStack(Items.EMERALD),
            8.0, 5.0, 2.0, 2.0));
        blocklingTypes.add(new BlocklingType("blockling_diamond", new ItemStack(Items.DIAMOND),
            10.0, 10.0, 5.0, 3.0));
        blocklingTypes.add(new BlocklingType("blockling_obsidian", new ItemStack(Blocks.OBSIDIAN),
            50.0, 10.0, -5.0, -5.0));
        blocklingTypes.add(new BlocklingType("blockling_pumpkin", new ItemStack(Blocks.PUMPKIN),
            0.0, 3.0, 3.0, 3.0));
        blocklingTypes.add(new BlocklingType("blockling_bedrock", new ItemStack(Blocks.BEDROCK),
            5000.0, 2000.0, 3000.0, 0.001));
    }

    public static BlocklingType getTypeFromTextureName(String textureName)
    {
        for (BlocklingType blocklingType : blocklingTypes)
        {
            if (textureName.equals(blocklingType.textureName))
            {
                return blocklingType;
            }
        }

        return blocklingTypes.get(0);
    }

    public static BlocklingType getTypeFromItemStack(ItemStack itemStack)
    {
        for (BlocklingType blocklingType : blocklingTypes)
        {
            for (ItemStack typeStack : blocklingType.upgradeMaterials)
            {
                if (itemStack.getItem().equals(typeStack.getItem()) && itemStack.getMetadata() == typeStack.getMetadata())
                {
                    return blocklingType;
                }
            }
        }

        return null;
    }


    public String textureName;
    public ResourceLocation entityTexture;
    public ModelResourceLocation itemModel;
    public ItemStack[] upgradeMaterials;
    public double bonusHealth, bonusAttackDamage, bonusAttackSpeed, bonusMovementSpeed;

    public BlocklingType(String textureName, ItemStack itemStack, double bonusHealth, double bonusAttackDamage, double bonusAttackSpeed, double bonusMovementSpeed)
    {
        this.upgradeMaterials = new ItemStack[] { itemStack };
        this.textureName = textureName;
        this.entityTexture = new ResourceLocationBlocklings("textures/entities/blockling/" + textureName + ".png");
        this.itemModel = new ModelResourceLocation(BlocklingsItems.itemBlockling.getRegistryName() + textureName.substring(textureName.indexOf("_")), "inventory");
        this.bonusHealth = bonusHealth;
        this.bonusAttackDamage = bonusAttackDamage;
        this.bonusAttackSpeed = bonusAttackSpeed;
        this.bonusMovementSpeed = bonusMovementSpeed;
    }

    public BlocklingType(String textureName, ItemStack[] upgradeMaterials, double bonusHealth, double bonusAttackDamage, double bonusAttackSpeed, double bonusMovementSpeed)
    {
        this.upgradeMaterials = upgradeMaterials;
        this.textureName = textureName;
        this.entityTexture = new ResourceLocationBlocklings("textures/entities/blockling/" + textureName + ".png");
        this.itemModel = new ModelResourceLocation(BlocklingsItems.itemBlockling.getRegistryName() + textureName.substring(textureName.indexOf("_")), "inventory");
        this.bonusHealth = bonusHealth;
        this.bonusAttackDamage = bonusAttackDamage;
        this.bonusAttackSpeed = bonusAttackSpeed;
        this.bonusMovementSpeed = bonusMovementSpeed;
    }
}
