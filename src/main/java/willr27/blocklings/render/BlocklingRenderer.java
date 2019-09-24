package willr27.blocklings.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.model.BlocklingModel;
import willr27.blocklings.render.Layers.BlocklingHeldItemLayer;

import javax.annotation.Nullable;

public class BlocklingRenderer extends MobRenderer<BlocklingEntity, BlocklingModel<BlocklingEntity>>
{
    public BlocklingRenderer(EntityRendererManager renderManager) {

        super(renderManager, new BlocklingModel<>(), 0.4F);
        addLayer(new BlocklingHeldItemLayer(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(BlocklingEntity blockling)
    {
        return blockling.getBlocklingType().entityTexture;
    }

    @Override
    protected void applyRotations(BlocklingEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    protected void renderLivingLabel(BlocklingEntity blockling, String label, double x, double y, double z, int something) // TODO: SOMETHING
    {
        super.renderLivingLabel(blockling, label, x, y, z, something);
    }
}