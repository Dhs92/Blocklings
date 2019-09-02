package com.blocklings.gui.screens.configs;

import com.blocklings.entity.EntityHelper;
import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.GuiHelper;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.whitelist.BlocklingWhitelist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jline.utils.Log;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.lang.reflect.Constructor;

// TODO: SAVE/CANCEL BUTTONS

public class GuiEntityWhitelist extends GuiScreen
{
    private static final ResourceLocation WHITELIST = new ResourceLocationBlocklings("textures/gui/inv_whitelist_entity.png");

    private static final int MAIN_TEXTURE_WIDTH = 176;
    private static final int MAIN_TEXTURE_HEIGHT = 166;

    private static final int ENTRY_WIDTH = 138;
    private static final int ENTRY_HEIGHT = 13;
    private static final int ENTRY_GAP = 1;

    private static final int TOGGLE_X = 134;
    private static final int TOGGLE_Y = 4;
    private static final int TOGGLE_WIDTH = 15;
    private static final int TOGGLE_HEIGHT = 11;

    private static final int ON_X = 109;
    private static final int ON_Y = 4;
    private static final int ON_WIDTH = 11;
    private static final int ON_HEIGHT = 11;

    private static final int OFF_X = 120;
    private static final int OFF_Y = 4;
    private static final int OFF_WIDTH = 11;
    private static final int OFF_HEIGHT = 11;

    private static final int SCROLL_BAR_X = 155;
    private static final int SCROLL_BAR_Y = 17;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 15;
    private int scrollBarOffset = 0;

    private EntityBlockling blockling;
    private EntityPlayer player;
    private World world;

    private int centerX, centerY;
    private int uiStartX, uiStartY;

    private boolean togglePressed = false;
    private boolean scrollPressed = false;
    private boolean onPressed = false;
    private boolean offPressed = false;

    private int page = 0;
    private int index = 0;
    private int max = 0;
    private float scroll = 0.0f;

    public GuiEntityWhitelist(EntityBlockling blockling, EntityPlayer player)
    {
        super();
        this.blockling = blockling;
        this.player = player;
        this.world = blockling.world;
    }

    @Override
    public void initGui()
    {
        blockling.isInGui = true;

        super.initGui();

        centerX = width / 2;
        centerY = height / 2 + GuiHelper.STANDARD_Y_OFFSET;

        uiStartX = centerX - (MAIN_TEXTURE_WIDTH / 2);
        uiStartY = centerY - (MAIN_TEXTURE_HEIGHT / 2);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(WHITELIST);
        drawTexturedModalRect(uiStartX, uiStartY, 0, 0, MAIN_TEXTURE_WIDTH, MAIN_TEXTURE_HEIGHT);

        BlocklingWhitelist whitelist = blockling.getWhitelist(blockling.configInfo.whitelist);
        Object[] entities = whitelist.keySet().toArray();

        if (scrollPressed)
        {
            int boundY = mouseY;
            boundY = Math.max(boundY, uiStartY + SCROLL_BAR_Y);
            boundY = Math.min(boundY, uiStartY + SCROLL_BAR_Y + 129);
            boundY = boundY - (uiStartY + SCROLL_BAR_Y);
            float percent = (float) boundY / 129.0f;
            page = (int)((percent * entities.length) / 10);
        }
        else
        {
            page += Mouse.getDWheel() / -120;
        }

        page = Math.max(page, 0);
        page = Math.min(page, entities.length / 10);
        index = page * 10;
        max = index + 10;
        max = Math.min(max, entities.length);
        scroll = (float) page * 10.0f / (float) entities.length;

        for (int i = index; i < max; i++)
        {
            ResourceLocation entity = (ResourceLocation) entities[i];

            if (i == 7)
            {
                try
                {
                    Class clazz = EntityList.getClass(entity);
                    Constructor cons = clazz.getConstructor(World.class);
                    EntityLivingBase ent = (EntityLivingBase) cons.newInstance(blockling.world);
                    GuiHelper.drawEntityOnScreen(100, 100, 15, mouseX, mouseY, ent);
                }
                catch (Exception e)
                {
                    Log.warn(e.getStackTrace());
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(WHITELIST);
            int texY = whitelist.isWhitelisted(entity) ? 181 : 181 + ENTRY_HEIGHT;
            drawTexturedModalRect(getEntryX(i), getEntryY(i), 0, texY, ENTRY_WIDTH, ENTRY_HEIGHT);
        }

        scrollBarOffset = (int)(129 * scroll);
        if (scrollPressed) drawTexturedModalRect(uiStartX + SCROLL_BAR_X, uiStartY + SCROLL_BAR_Y + scrollBarOffset, SCROLL_BAR_WIDTH, 166, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
        else drawTexturedModalRect(uiStartX + SCROLL_BAR_X, uiStartY + SCROLL_BAR_Y + scrollBarOffset, 0, 166, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);

        if (togglePressed) drawTexturedModalRect(uiStartX + TOGGLE_X, uiStartY + TOGGLE_Y, TOGGLE_WIDTH, 207, TOGGLE_WIDTH, TOGGLE_HEIGHT);
        else drawTexturedModalRect(uiStartX + TOGGLE_X, uiStartY + TOGGLE_Y, 0, 207, TOGGLE_WIDTH, TOGGLE_HEIGHT);

        if (onPressed) drawTexturedModalRect(uiStartX + ON_X, uiStartY + ON_Y, ON_WIDTH, 218, ON_WIDTH, ON_HEIGHT);
        else drawTexturedModalRect(uiStartX + ON_X, uiStartY + ON_Y, 0, 218, ON_WIDTH, ON_HEIGHT);
        if (offPressed) drawTexturedModalRect(uiStartX + OFF_X, uiStartY + OFF_Y, OFF_WIDTH, 218 + ON_HEIGHT, OFF_WIDTH, OFF_HEIGHT);
        else drawTexturedModalRect(uiStartX + OFF_X, uiStartY + OFF_Y, 0, 218 + ON_HEIGHT, OFF_WIDTH, OFF_HEIGHT);

        for (int i = index; i < max; i++)
        {
            ResourceLocation entity = (ResourceLocation) entities[i];
            String name = EntityHelper.getDisplayName(entity);
            fontRenderer.drawString(name, getEntryX(i) + 3, getEntryY(i) + 3, 0xffffffff, false);
        }

        fontRenderer.drawString("Whitelist", uiStartX + 8, uiStartY + 6, 0xffffff, false);
    }

    private int getEntryX(int i)
    {
        return uiStartX + 10;
    }

    private int getEntryY(int i)
    {
        return uiStartY + 18 + (i % 10) * (ENTRY_HEIGHT + ENTRY_GAP);
    }

    private int getHoveredEntity(int mouseX, int mouseY)
    {
        for (int i = index; i < max; i++)
        {
            if (isMouseOver(mouseX, mouseY, getEntryX(i), getEntryY(i), ENTRY_WIDTH, ENTRY_HEIGHT))
            {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (blockling.configInfo.prevGuiId != -1 && (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)))
        {
            blockling.openGui(player);
        }
        else
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (isMouseOver(mouseX, mouseY, uiStartX + TOGGLE_X, uiStartY + TOGGLE_Y, TOGGLE_WIDTH, TOGGLE_HEIGHT))
        {
            togglePressed = true;
        }
        else if (isMouseOver(mouseX, mouseY, uiStartX + SCROLL_BAR_X, uiStartY + SCROLL_BAR_Y + scrollBarOffset, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT))
        {
            scrollPressed = true;
        }
        else if (isMouseOver(mouseX, mouseY, uiStartX + ON_X, uiStartY + ON_Y, ON_WIDTH, ON_HEIGHT))
        {
            onPressed = true;
        }
        else if (isMouseOver(mouseX, mouseY, uiStartX + OFF_X, uiStartY + OFF_Y, OFF_WIDTH, OFF_HEIGHT))
        {
            offPressed = true;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (!scrollPressed)
        {
            int hoveredEntity = getHoveredEntity(mouseX, mouseY);
            if (hoveredEntity != -1)
            {
                BlocklingWhitelist whitelist = blockling.getWhitelist(blockling.configInfo.whitelist);
                Object[] entities = whitelist.keySet().toArray();
                whitelist.toggleEntry((ResourceLocation) entities[hoveredEntity]);
            }
            else if (isMouseOver(mouseX, mouseY, uiStartX + TOGGLE_X, uiStartY + TOGGLE_Y, TOGGLE_WIDTH, TOGGLE_HEIGHT))
            {
                BlocklingWhitelist whitelist = blockling.getWhitelist(blockling.configInfo.whitelist);
                whitelist.toggleAll();
            }
            else if (isMouseOver(mouseX, mouseY, uiStartX + ON_X, uiStartY + ON_Y, ON_WIDTH, ON_HEIGHT))
            {
                BlocklingWhitelist whitelist = blockling.getWhitelist(blockling.configInfo.whitelist);
                whitelist.setAll(true);
            }
            else if (isMouseOver(mouseX, mouseY, uiStartX + OFF_X, uiStartY + OFF_Y, OFF_WIDTH, OFF_HEIGHT))
            {
                BlocklingWhitelist whitelist = blockling.getWhitelist(blockling.configInfo.whitelist);
                whitelist.setAll(false);
            }
        }

        togglePressed = false;
        scrollPressed = false;
        onPressed = false;
        offPressed = false;

        super.mouseReleased(mouseX, mouseY, state);
    }

    private boolean isMouseOver(int mouseX, int mouseY, int left, int top, int width, int height)
    {
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY <= top + height;
    }
}
