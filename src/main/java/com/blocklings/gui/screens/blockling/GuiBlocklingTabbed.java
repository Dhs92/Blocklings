package com.blocklings.gui.screens.blockling;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.GuiHelper;
import com.blocklings.gui.containers.ContainerBlank;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.Tab;
import net.minecraft.client.gui.inventory.GuiContainer;
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
    protected static final int MAIN_TEXTURE_WIDTH = 176;
    protected static final int MAIN_TEXTURE_HEIGHT = 166;

    // The width/height the center screen takes up
    protected static final int SCREEN_WIDTH = 160;
    protected static final int SCREEN_HEIGHT = 150;

    private static final int TAB_WIDTH = 28;
    private static final int TAB_HEIGHT = 28;
    private static final int TAB_HIGHLIGHTED_WIDTH = 32;

    protected int uiStartX, uiStartY;
    protected int centerX, centerY;

    protected int prevMouseX, prevMouseY;
    protected boolean mouseDown = false;
    protected int prevKeyCode;

    protected EntityBlockling blockling;
    protected EntityPlayer player;

    protected boolean drawTabToolTips = true;
    private boolean drawContainer = false;

    protected GuiBlocklingTabbed(EntityBlockling blockling, EntityPlayer player)
    {
        this(new ContainerBlank(), blockling, player);
        drawContainer = false;
    }

    protected GuiBlocklingTabbed(Container container, EntityBlockling blockling, EntityPlayer player)
    {
        super(container);

        this.blockling = blockling;
        this.player = player;
        drawContainer = true;
    }

    @Override
    public void initGui()
    {
        blockling.isInGui = true;

        xSize = UI_WIDTH;
        ySize = UI_HEIGHT;

        centerX = width / 2;
        centerY = height / 2 + GuiHelper.STANDARD_Y_OFFSET;

        uiStartX = centerX - (MAIN_TEXTURE_WIDTH / 2) - TAB_WIDTH;
        uiStartY = centerY - (MAIN_TEXTURE_HEIGHT / 2);

        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
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
                int screenPosX = posX + uiStartX;
                drawTexturedModalRect(screenPosX, posY + uiStartY, texturePosX, posY, TAB_HIGHLIGHTED_WIDTH, TAB_HEIGHT);
            }
            else
            {
                int texturePosX = tab.left ? 0 : 32;
                int screenPosX = tab.left ? posX + uiStartX + 2 : posX + uiStartX - 3;
                drawTexturedModalRect(screenPosX, posY + uiStartY, texturePosX, posY, TAB_WIDTH, TAB_HEIGHT);
            }
        }

        if (drawContainer) super.drawScreen(mouseX, mouseY, partialTicks);

        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (drawTabToolTips && hoveredTab != null)
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

        mouseDown = true;
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

        mouseDown = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        prevKeyCode = keyCode;
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
            int posX = tab.left ? uiStartX + 7 : UI_WIDTH - TAB_WIDTH + uiStartX + 1;
            int posY = tab.pos * 29 + uiStartY + 4;
            if (isMouseOver(mouseX, mouseY, posX, posY, 20, 20))
            {
                return tab;
            }
        }

        return null;
    }
}
