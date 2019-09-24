package willr27.blocklings.gui.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.util.BlocklingsResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiUtil
{
    public static final ResourceLocation TABS = new BlocklingsResourceLocation("textures/gui/tabs.png");
    public static final ResourceLocation STATS = new BlocklingsResourceLocation("textures/gui/stats.png");
    public static final ResourceLocation TASKS = new BlocklingsResourceLocation("textures/gui/tasks.png");
    public static final ResourceLocation EQUIPMENT = new BlocklingsResourceLocation("textures/gui/equipment.png");
    public static final ResourceLocation INVENTORY = new BlocklingsResourceLocation("textures/gui/inventory.png");
    public static final ResourceLocation ABILITIES = new BlocklingsResourceLocation("textures/gui/abilities.png");
    public static final ResourceLocation ABILITIES_WIDGETS = new BlocklingsResourceLocation("textures/gui/abilities_widgets.png");
    public static final ResourceLocation MINING_BACKGROUND = new BlocklingsResourceLocation("textures/gui/abilities_backgrounds/mining.png");
    public static final ResourceLocation WHITELIST = new BlocklingsResourceLocation("textures/gui/whitelist.png");

    public static void bindTexture(ResourceLocation texture)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
    }

    public static List<String> splitText(FontRenderer font, String text, int maxWidth)
    {
        List<String> outText = new ArrayList<>();

        String tempString = text;
        while (font.getStringWidth(tempString) > maxWidth)
        {
            String trim = font.trimStringToWidth(tempString, maxWidth);
            if (tempString.substring(trim.length(), trim.length() + 1) != " ")
            {
                int i = trim.lastIndexOf(" ");
                if (i != -1) trim = trim.substring(0, i);
            }
            outText.add(trim);
            tempString = tempString.substring(trim.length() + 1);
        }
        outText.add(tempString);

        return outText;
    }

    public static boolean isKeyDown(int key)
    {
        return InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), key);
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int left, int top, int width, int height)
    {
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY <= top + height;
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity ent)
    {
        String name = ent.getCustomName().getString();
        ent.setCustomName(null);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)posX, (float)posY, 50.0F);
        float scale2 = 1.0f / Math.max(ent.getWidth(), ent.getHeight());
        GlStateManager.scalef((float)(-scale * scale2), (float)scale * scale2, (float)scale * scale2);
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translatef(0.0F, 0.0F, 0.0F);
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        entityrenderermanager.setPlayerViewY(180.0F);
        entityrenderermanager.setRenderShadow(false);
        entityrenderermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        entityrenderermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        ent.setCustomName(new StringTextComponent(name));
    }
}
