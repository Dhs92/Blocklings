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
        ItemStack itemstack = blockling.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack itemstack1 = blockling.getHeldItem(EnumHand.OFF_HAND);

        if (itemstack != null) renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, age, limbSwing, speed);
        if (itemstack1 != null) renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, age, limbSwing, speed);
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide, float age, float time, float speed)
    {
        EntityBlockling blockling = (EntityBlockling) entity;

        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float)(flag ? -1 : 1) / 2.28F, 0.65f, 0.0F);
            GlStateManager.rotate(-146.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

            if (blockling.isSitting())
            {
                GlStateManager.translate(0.0f, -0.03f, -0.03F);
                GlStateManager.rotate(8.0F, 1.0F, 0.0F, 0.0F);
            }

            // Animation

            float logSpeed = (float) Math.log(speed + 1);
            float swingHeight = 0.05f + logSpeed / 4.0f;
            float swingSpeed = 1.2f;
            float rot = (flipFlopper(age + time * 30.0f, swingSpeed) * (swingHeight));
            double angle = handSide == EnumHandSide.LEFT ? rot :  -rot;
            GlStateManager.rotate((float) Math.toDegrees(angle), 1.0F, 0.0F, 0.0F);

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