package com.blocklings.gui.screens;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBlocklingStats extends GuiBlocklingTabbed
{
    private static final ResourceLocation STATS = new ResourceLocationBlocklings("textures/gui/inv_stats.png");

    public GuiBlocklingStats(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(STATS);
        drawTexturedModalRect(left + ((UI_WIDTH - SCREEN_TEXTURE_WIDTH) / 2), top, 0, 0, SCREEN_TEXTURE_WIDTH, SCREEN_TEXTURE_HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }
}
