package willr27.blocklings.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import willr27.blocklings.Blocklings;
import willr27.blocklings.entity.blockling.BlocklingEntity;

public class EntityTypes
{
    public static final EntityType<BlocklingEntity> BLOCKLING = Blocklings.ENTITY_TYPE_BUILDER
    .createEntityType("blockling", BlocklingEntity::new, EntityClassification.CREATURE, 0xffffff, 0xffffff);

    public static void init()
    {

    }
}
