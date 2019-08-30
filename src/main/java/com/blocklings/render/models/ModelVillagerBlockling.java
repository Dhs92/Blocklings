package com.blocklings.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelVillagerBlockling extends ModelBase
{
    public ModelRenderer body;
    public ModelRenderer bodyChild;
    public ModelRenderer bodyChild_1;
    public ModelRenderer bodyChild_2;
    public ModelRenderer bodyChild_3;
    public ModelRenderer shape8;

    public ModelVillagerBlockling() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.bodyChild_1 = new ModelRenderer(this, 64, 12);
        this.bodyChild_1.setRotationPoint(8.0F, -3.0F, 0.0F);
        this.bodyChild_1.addBox(-2.0F, 2.0F, -7.0F, 2, 6, 6, 0.0F);
        this.setRotateAngle(bodyChild_1, 0.6981315016746521F, 0.0F, 0.0F);
        this.body = new ModelRenderer(this, 16, 0);
        this.body.setRotationPoint(0.0F, 13.0F, 0.0F);
        this.body.addBox(-6.0F, -3.0F, -6.0F, 12, 12, 12, 0.0F);
        this.setRotateAngle(body, 0.08726649731397627F, 0.0F, 0.0F);
        this.bodyChild_3 = new ModelRenderer(this, 42, 24);
        this.bodyChild_3.setRotationPoint(4.0F, 4.0F, 0.5F);
        this.bodyChild_3.addBox(-3.5F, 1.0F, -3.5F, 5, 6, 6, 0.0F);
        this.setRotateAngle(bodyChild_3, -0.08726649731397627F, 0.0F, 0.0F);
        this.bodyChild_2 = new ModelRenderer(this, 16, 24);
        this.bodyChild_2.setRotationPoint(-4.0F, 4.0F, 0.5F);
        this.bodyChild_2.addBox(-1.5F, 1.0F, -3.5F, 5, 6, 6, 0.0F);
        this.setRotateAngle(bodyChild_2, -0.08726649731397627F, 0.0F, 0.0F);
        this.bodyChild = new ModelRenderer(this, 0, 12);
        this.bodyChild.setRotationPoint(-8.0F, -3.0F, 0.0F);
        this.bodyChild.addBox(0.0F, 2.0F, -7.0F, 2, 6, 6, 0.0F);
        this.setRotateAngle(bodyChild, 0.6981315016746521F, 0.0F, 0.0F);
        this.shape8 = new ModelRenderer(this, 0, 0);
        this.shape8.setRotationPoint(0.0F, 5.0F, -7.0F);
        this.shape8.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        this.body.addChild(this.bodyChild_1);
        this.body.addChild(this.bodyChild_3);
        this.body.addChild(this.bodyChild_2);
        this.body.addChild(this.bodyChild);
        this.body.addChild(this.shape8);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.body.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
