package com.blocklings.gui.screens.blockling;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.containers.ContainerEquipmentBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBlocklingEquipment extends GuiBlocklingTabbed
{
    private static final ResourceLocation EQUIPMENT = new ResourceLocationBlocklings("textures/gui/inv_equipment.png");

    public GuiBlocklingEquipment(EntityBlockling blockling, EntityPlayer player)
    {
        super(new ContainerEquipmentBlockling(blockling, player.inventory, blockling.getInv()), blockling, player);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(EQUIPMENT);
        drawTexturedModalRect(uiStartX + ((UI_WIDTH - MAIN_TEXTURE_WIDTH) / 2), uiStartY, 0, 0, MAIN_TEXTURE_WIDTH, MAIN_TEXTURE_HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }
}
