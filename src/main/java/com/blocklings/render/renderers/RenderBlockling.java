package com.blocklings.render.renderers;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.render.models.ModelBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class RenderBlockling extends RenderLiving<EntityBlockling>
{
    public static final Factory FACTORY = new Factory();

    public RenderBlockling(RenderManager rendermanagerIn)
    {
        super(rendermanagerIn, new ModelBlockling(), 0.3F);
    }

    @Override
    protected void preRenderCallback(EntityBlockling blockling, float partialTicks)
    {
        //GlStateManager.scale(val, val, val);
    }

    @Override
    protected void renderLivingLabel(EntityBlockling blockling, String label, double x, double y, double z, int maxDistance)
    {
        super.renderLivingLabel(blockling, label, x, y, z, maxDistance);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBlockling entity)
    {
        return new ResourceLocationBlocklings("textures/entities/blockling/blockling_0.png");
    }

    public static class Factory implements IRenderFactory<EntityBlockling>
    {
        @Override
        public Render<? super EntityBlockling> createRenderFor(RenderManager manager)
        {
            return new RenderBlockling(manager);
        }

    }
}