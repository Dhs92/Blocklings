package willr27.blocklings.model;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import willr27.blocklings.entity.blockling.BlocklingEntity;

public class BlocklingModel<T extends BlocklingEntity> extends EntityModel<T>
{
    private final RendererModel body;
    private final RendererModel rightLeg;
    private final RendererModel leftLeg;
    private final RendererModel rightArm;
    private final RendererModel leftArm;
    private final RendererModel rightEye;
    private final RendererModel leftEye;

    public static final float bodyBaseX = 0.0872665f;
    private float rightLegBaseX = -0.0872665f;
    private float leftLegBaseX = -0.0872665f;
    public static final float rightArmBaseX = 0.785398f - bodyBaseX;
    public static final float leftArmBaseX =  0.785398f - bodyBaseX;
    private float rightEyeBaseX = 0.0f;
    private float leftEyeBaseX = 0.0f;

    private int[] attackAnimation = { 0, 18, 39, 62, 85, 70, 58, 47, 37, 28, 20, 14, 8, 3, 0 };

    public BlocklingModel()
    {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.body = new RendererModel(this, 16, 0);
        this.body.addBox(-6.0F, -3.0F, -6.0F, 12, 12, 12);
        this.body.setRotationPoint(0.0F, 13.0F, 0.0F);
        this.body.setTextureSize(128, 64);
        this.body.showModel = true;
        setRotation(this.body, 0.0872665F, 0.0F, 0.0F);
        this.rightLeg = new RendererModel(this, 16, 24);
        this.rightLeg.addBox(-1.5F, 1.0F, -3.5F, 5, 6, 6);
        this.rightLeg.setRotationPoint(-4.0F, 4.0F, 0.5F);
        this.rightLeg.setTextureSize(128, 64);
        this.rightLeg.showModel = true;
        setRotation(this.rightLeg, -0.0872665F, 0.0F, 0.0F);
        this.leftLeg = new RendererModel(this, 42, 24);
        this.leftLeg.addBox(-3.5F, 1.0F, -3.5F, 5, 6, 6);
        this.leftLeg.setRotationPoint(4.0F, 4.0F, 0.5F);
        this.leftLeg.setTextureSize(128, 64);
        this.leftLeg.showModel = true;
        setRotation(this.leftLeg, -0.0872665F, 0.0F, 0.0F);
        this.rightArm = new RendererModel(this, 0, 12);
        this.rightArm.addBox(0.0F, 2.0F, -7.0F, 2, 6, 6);
        this.rightArm.setRotationPoint(-8.0F, -3.0F, 0.0F);
        this.rightArm.setTextureSize(128, 64);
        this.rightArm.showModel = true;
        setRotation(this.rightArm, rightArmBaseX, 0.0F, 0.0F);
        this.leftArm = new RendererModel(this, 64, 12);
        this.leftArm.addBox(-2.0F, 2.0F, -7.0F, 2, 6, 6);
        this.leftArm.setRotationPoint(8.0F, -3.0F, 0.0F);
        this.leftArm.setTextureSize(128, 64);
        this.leftArm.showModel = true;
        setRotation(this.leftArm, leftArmBaseX, 0.0F, 0.0F);
        this.rightEye = new RendererModel(this, 22, 8);
        this.rightEye.addBox(-1.0F, -0.2F, 1.5F, 2, 3, 1);
        this.rightEye.setRotationPoint(-2.0F, 3.0F, -8.0F);
        this.rightEye.setTextureSize(128, 64);
        this.rightEye.showModel = true;
        setRotation(this.rightEye, 0.0F, 0.0F, 0.0F);
        this.leftEye = new RendererModel(this, 52, 8);
        this.leftEye.addBox(-1.0F, -0.2F, 1.5F, 2, 3, 1);
        this.leftEye.setRotationPoint(2.0F, 3.0F, -8.0F);
        this.leftEye.setTextureSize(128, 64);
        this.leftEye.showModel = true;
        setRotation(this.leftEye, 0.0F, 0.0F, 0.0F);

        this.body.addChild(this.rightArm);
        this.body.addChild(this.leftArm);
        this.body.addChild(this.rightLeg);
        this.body.addChild(this.leftLeg);
        this.body.addChild(this.rightEye);
        this.body.addChild(this.leftEye);
    }

    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        this.body.render(scale);
    }

    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
    {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
    }

    private float flipFlopper(float age, float coef)
    {
        return ((float) Math.sin(Math.toRadians((coef * age) % 360.0f))) * (float) Math.PI;
    }

    private void setRotation(RendererModel model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}