package com.blocklings.render.layers;

import com.blocklings.entity.entities.EntityBlockling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHeldItem implements LayerRenderer<EntityLivingBase>
{
    protected final RenderLivingBase<?> livingEntityRenderer;

    public LayerHeldItem(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float speed, float time, float age, float netHeadYaw, float headPitch, float scale)
    {
        EntityBlockling blockling = (EntityBlockling) entitylivingbaseIn;
        ItemStack mainStack = blockling.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offStack = blockling.getHeldItem(EnumHand.OFF_HAND);

        if (mainStack != null) renderHeldItem(entitylivingbaseIn, mainStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, age, limbSwing, speed);
        if (offStack != null) renderHeldItem(entitylivingbaseIn, offStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, age, limbSwing, speed);
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide, float age, float time, float speed)
    {
        EntityBlockling blockling = (EntityBlockling) entity;

        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float)(flag ? 1 : -1) / 2.28F, 0.65f, 0.0F);

            GlStateManager.translate(0.0F, -0.14f, -0.31F);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transform, flag);
            GlStateManager.popMatrix();
        }
    }

    private float flipFlopper(float age, float coef)
    {
        return ((float) Math.sin(Math.toRadians((coef * age) % 360.0f))) * (float) Math.PI;
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}