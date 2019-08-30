package com.blocklings.gui.screens;

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
        drawTexturedModalRect(left + ((UI_WIDTH - SCREEN_TEXTURE_WIDTH) / 2), top, 0, 0, SCREEN_TEXTURE_WIDTH, SCREEN_TEXTURE_HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }
}
