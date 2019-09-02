package com.blocklings.render.renderers;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.proxy.ClientProxy;
import com.blocklings.render.layers.LayerHeldItem;
import com.blocklings.render.models.ModelBlockling;
import com.blocklings.render.models.ModelVillagerBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class RenderBlockling extends RenderLiving<EntityBlockling>
{
    public static final Factory FACTORY = new Factory();

    public RenderBlockling(RenderManager rendermanagerIn)
    {
        super(rendermanagerIn, new ModelBlockling(), 0.3F);
        addLayer(new LayerHeldItem(this));
    }

    @Override
    public void doRender(EntityBlockling entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        //renderMelon(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderMelon(EntityBlockling entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + entity.height / 2, z);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        //GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glCallList(ClientProxy.sphereIdOutside);

        GL11.glCallList(ClientProxy.sphereIdInside);
        GL11.glPopMatrix();
    }

    @Override
    protected void preRenderCallback(EntityBlockling blockling, float partialTicks)
    {
        float val = blockling.getBlocklingStats().getScale();
        GlStateManager.scale(val, val, val);
    }

    @Override
    protected void renderLivingLabel(EntityBlockling blockling, String label, double x, double y, double z, int maxDistance)
    {
        if (!blockling.isInGui) super.renderLivingLabel(blockling, label, x, y, z, maxDistance);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBlockling blockling)
    {
        return new ResourceLocationBlocklings("textures/entities/blockling/" + blockling.getBlocklingType().textureName + ".png");
//        return new ResourceLocationBlocklings("textures/entities/blockling/blockling_villager.png");
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