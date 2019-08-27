package com.blocklings.entity;

import com.blocklings.Blocklings;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.render.renderers.RenderBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHelper
{
    public static void registerEntities()
    {
        int id = 1;
        EntityRegistry.registerModEntity(new ResourceLocationBlocklings("entity_blockling"), EntityBlockling.class, "blockling", id++, Blocklings.instance, 64, 3, true, 7951674, 7319108);
        EntityRegistry.addSpawn(EntityBlockling.class, 10, 1, 2, EnumCreatureType.CREATURE,
                Biomes.PLAINS,
                Biomes.FOREST,
                Biomes.FOREST_HILLS,
                Biomes.REDWOOD_TAIGA,
                Biomes.ROOFED_FOREST,
                Biomes.TAIGA,
                Biomes.TAIGA_HILLS
        );
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityBlockling.class, RenderBlockling.FACTORY);
    }
}
