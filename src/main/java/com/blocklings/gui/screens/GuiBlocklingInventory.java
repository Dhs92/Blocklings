package com.blocklings.gui.screens;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.containers.ContainerInventoryBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBlocklingInventory extends GuiBlocklingTabbed
{
    private static final ResourceLocation INVENTORY = new ResourceLocationBlocklings("textures/gui/inv_inventory.png");

    public GuiBlocklingInventory(EntityBlockling blockling, EntityPlayer player)
    {
        super(new ContainerInventoryBlockling(blockling, player.inventory, blockling.getInv()), blockling, player);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(INVENTORY);
        drawTexturedModalRect(left + ((UI_WIDTH - SCREEN_TEXTURE_WIDTH) / 2), top, 0, 0, SCREEN_TEXTURE_WIDTH, SCREEN_TEXTURE_HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }
}
