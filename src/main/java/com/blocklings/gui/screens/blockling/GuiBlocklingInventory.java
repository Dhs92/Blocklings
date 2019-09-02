package com.blocklings.gui.screens.blockling;

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
        drawTexturedModalRect(uiStartX + ((UI_WIDTH - MAIN_TEXTURE_WIDTH) / 2), uiStartY, 0, 0, MAIN_TEXTURE_WIDTH, MAIN_TEXTURE_HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }
}
