package willr27.blocklings.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import willr27.blocklings.config.BlocklingsConfig;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil
{
    public static final List<ResourceLocation> ENTITIES = new ArrayList<>();
    private static boolean called = false;
    public static void init(World world)
    {
        if (!called)
        {
            called = true;
            for (ResourceLocation entry : Registry.ENTITY_TYPE.keySet())
            {
                Entity entity = Registry.ENTITY_TYPE.getValue(entry).get().create(world);
                if ((entity instanceof LivingEntity && !(entity instanceof WaterMobEntity)))
                {
                    ENTITIES.add(entry);
                }
            }
            BlocklingsConfig.initEntities();
        }
    }
}
