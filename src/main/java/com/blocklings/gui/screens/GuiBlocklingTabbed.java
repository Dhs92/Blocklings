package com.blocklings.gui.screens;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.containers.ContainerBlank;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.Tab;
import com.sun.jna.platform.unix.X11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

abstract public class GuiBlocklingTabbed extends GuiContainer
{
    protected static final ResourceLocation WIDGETS = new ResourceLocationBlocklings("textures/gui/inv_widgets.png");

    protected static final int Y_OFFSET = -10;

    // The width/height of the area textures take up
    protected static final int UI_WIDTH = 232;
    protected static final int UI_HEIGHT = 166;

    // The width/height the center screen's texture takes up
    protected static final int SCREEN_TEXTURE_WIDTH = 176;
    protected static final int SCREEN_TEXTURE_HEIGHT = 166;

    // The width/height the center screen takes up
    protected static final int SCREEN_WIDTH = 160;
    protected static final int SCREEN_HEIGHT = 150;

    private static final int TAB_WIDTH = 28;
    private static final int TAB_HEIGHT = 28;
    private static final int TAB_HIGHLIGHTED_WIDTH = 32;

    protected int left, top;
    protected int centerX, centerY;

    protected EntityBlockling blockling;
    protected EntityPlayer player;

    protected GuiBlocklingTabbed(EntityBlockling blockling, EntityPlayer player)
    {
        this(new ContainerBlank(), blockling, player);
    }

    protected GuiBlocklingTabbed(Container container, EntityBlockling blockling, EntityPlayer player)
    {
        super(container);

        this.blockling = blockling;
        this.player = player;
    }

    @Override
    public void initGui()
    {
        blockling.isInGui = true;

        xSize = UI_WIDTH;
        ySize = UI_HEIGHT;

        super.initGui();

        left = (width - UI_WIDTH) / 2;
        top = (height - UI_HEIGHT) / 2 + Y_OFFSET;

        centerX = width / 2;
        centerY = height / 2 + Y_OFFSET;
    }

    @Override
    public void updateScreen()
    {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(WIDGETS);
        for (Tab tab : Tab.values())
        {
            int posX = tab.left ? 0 : UI_WIDTH - TAB_WIDTH;
            int posY = tab.pos * 29;
            if (tab == blockling.getCurrentGuiTab())
            {
                posX = tab.left ? posX : posX - 4;
                int texturePosX = tab.left ? 64 : 96;
                int screenPosX = posX + left;
                drawTexturedModalRect(screenPosX, posY + top, texturePosX, posY, TAB_HIGHLIGHTED_WIDTH, TAB_HEIGHT);
            }
            else
            {
                int texturePosX = tab.left ? 0 : 32;
                int screenPosX = tab.left ? posX + left + 2 : posX + left - 3;
                drawTexturedModalRect(screenPosX, posY + top, texturePosX, posY, TAB_WIDTH, TAB_HEIGHT);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab != null)
        {
            drawHoveringText(hoveredTab.displayName, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab != null && hoveredTab != blockling.getCurrentGuiTab())
        {
            blockling.setCurrentGuiTab(hoveredTab);
            blockling.openGui(player);
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        blockling.isInGui = false;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    protected boolean isMouseOver(int mouseX, int mouseY, int left, int top, int width, int height)
    {
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY <= top + height;
    }

    private Tab getHoveredTab(int mouseX, int mouseY)
    {
        for (Tab tab : Tab.values())
        {
            int posX = tab.left ? left + 7 : UI_WIDTH - TAB_WIDTH + left + 1;
            int posY = tab.pos * 29 + top + 4;
            if (isMouseOver(mouseX, mouseY, posX, posY, 20, 20))
            {
                return tab;
            }
        }

        return null;
    }

    protected static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityBlockling ent)
    {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        float scale2 = ent.getBlocklingStats().getScale();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale) / scale2, (float)scale / scale2, (float)scale / scale2);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
