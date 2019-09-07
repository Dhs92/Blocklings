package willr27.blocklings.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import willr27.blocklings.Blocklings;

import java.util.ArrayList;
import java.util.List;

public class EntityTypeBuilder
{
    public final List<EntityType> ENTITY_TYPES = new ArrayList<>();
    public final List<Item> SPAWN_EGGS = new ArrayList<>();

    public <T extends Entity> EntityType createEntityType(String name, EntityType.IFactory<T> factory, EntityClassification classification)
    {
        EntityType<T> type = EntityType.Builder.create(factory, classification).setTrackingRange(64).size(0.9f, 0.9f).build(Blocklings.MODID + ":" + name);
        type.setRegistryName(new ResourceLocation(Blocklings.MODID, name));

        ENTITY_TYPES.add(type);

        addSpawnToAllBiomes(type, EntityClassification.CREATURE);
        addSpawnToAllBiomes(type, EntityClassification.AMBIENT);
        addSpawnToAllBiomes(type, EntityClassification.MONSTER);

        return type;
    }

    public <T extends Entity> EntityType createEntityType(String name, EntityType.IFactory<T> factory, EntityClassification classification, int colour1, int colour2)
    {
        EntityType<T> type = EntityType.Builder.create(factory, classification).setTrackingRange(64).size(0.9f, 0.9f).build(Blocklings.MODID + ":" + name);
        type.setRegistryName(new ResourceLocation(Blocklings.MODID, name));

        ENTITY_TYPES.add(type);
        SPAWN_EGGS.add(new SpawnEggItem(type, colour1, colour2, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName(Blocklings.MODID,"item.spawn_egg." + name));

        addSpawnToAllBiomes(type, EntityClassification.CREATURE);
        addSpawnToAllBiomes(type, EntityClassification.AMBIENT);
        addSpawnToAllBiomes(type, EntityClassification.MONSTER);

        return type;
    }

    public static void addSpawnToAllBiomes(EntityType<?> type, EntityClassification classification)
    {
        for (Biome biome : Biome.BIOMES)
        {
            biome.getSpawns(classification).add(new Biome.SpawnListEntry(type, 20, 1, 1));
        }
    }
}
