package willr27.blocklings.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil
{
    public static final List<ResourceLocation> ENTITIES = new ArrayList<>();
    static
    {
        for (ResourceLocation entry : Registry.ENTITY_TYPE.keySet())
        {
            if (Registry.ENTITY_TYPE.getValue(entry).get().getClassification() != EntityClassification.WATER_CREATURE)
            {
                ENTITIES.add(entry);
            }
        }
    }
}
