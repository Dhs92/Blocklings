package willr27.blocklings.render.Layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.model.BlocklingModel;

public class BlocklingHeldItemLayer extends LayerRenderer<BlocklingEntity, BlocklingModel<BlocklingEntity>>
{
    public BlocklingHeldItemLayer(IEntityRenderer<BlocklingEntity, BlocklingModel<BlocklingEntity>> p_i50938_1_) {
        super(p_i50938_1_);
    }

    public void render(BlocklingEntity blockling, float limbSwing, float speed, float time, float age, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack mainStack = blockling.getHeldItemMainhand();
        ItemStack offStack = blockling.getHeldItemOffhand();

        if (!mainStack.isEmpty()) renderHeldItem(blockling, mainStack, Hand.MAIN_HAND);
        if (!offStack.isEmpty()) renderHeldItem(blockling, offStack, Hand.OFF_HAND);
    }

    private void renderHeldItem(BlocklingEntity blockling, ItemStack stack, Hand hand)
    {
        ItemCameraTransforms.TransformType transformType = hand == Hand.MAIN_HAND ? ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;

        GlStateManager.pushMatrix();
        boolean flag = hand == Hand.MAIN_HAND;
        GlStateManager.translatef((float)(flag ? -1 : 1) / 2.28F, 0.65f, 0.0F);
        GlStateManager.rotatef(-146.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);

        Minecraft.getInstance().getItemRenderer().renderItem(stack, blockling, transformType, false);
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
