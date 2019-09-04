package willr27.blocklings.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.model.ModelBlockling;
import willr27.blocklings.entity.BlocklingEntity;
import willr27.blocklings.util.BlocklingsResourceLocation;

import javax.annotation.Nullable;

public class BlocklingRenderer extends MobRenderer<BlocklingEntity, ModelBlockling<BlocklingEntity>>
{
    public BlocklingRenderer(EntityRendererManager p_i50969_1_) {

        super(p_i50969_1_, new ModelBlockling<>(), 0.4F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(BlocklingEntity entity)
    {
        return new BlocklingsResourceLocation("textures/entities/blockling/blockling_grass.png");
    }

    @Override
    protected void applyRotations(BlocklingEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}