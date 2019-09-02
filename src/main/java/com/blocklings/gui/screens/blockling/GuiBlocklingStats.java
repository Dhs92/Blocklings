package com.blocklings.gui.screens.blockling;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.GuiHelper;
import com.blocklings.util.GuiTextFieldCentered;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiBlocklingStats extends GuiBlocklingTabbed
{
    private static final ResourceLocation STATS = new ResourceLocationBlocklings("textures/gui/inv_stats.png");

    private static final int ICON_SIZE = 11;
    private static final int XP_BAR_WIDTH = 111;

    private GuiTextFieldCentered nameTextField;

    public GuiBlocklingStats(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        nameTextField = new GuiTextFieldCentered(3, fontRenderer, width / 2 - 80, height / 2 - 85, 160, 20)
        {
            public void setFocused(boolean isFocusedIn)
            {
                blockling.setCustomNameTag(nameTextField.getText());
                nameTextField.setText(blockling.getCustomNameTag());
                super.setFocused(isFocusedIn);
            }
        };
        nameTextField.setText(blockling.getCustomNameTag());
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        nameTextField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(STATS);
        drawTexturedModalRect(uiStartX + ((UI_WIDTH - MAIN_TEXTURE_WIDTH) / 2), uiStartY, 0, 0, MAIN_TEXTURE_WIDTH, MAIN_TEXTURE_HEIGHT);

        drawIcons();
        drawXpBars();
        drawStatText();

        GlStateManager.color(1.0f, 1.0f, 1.0f);
        nameTextField.drawTextBox();

        GuiHelper.drawEntityOnScreen(centerX, centerY - 6, 34, centerX - mouseX, centerY - mouseY - 12, blockling);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawIcons()
    {
        drawStatIcon(0, 1);
        drawStatIcon(1, 1);
        drawStatIcon(2, 1);
        drawStatIcon(3, 1);

        drawStatIcon(0, 0);
        drawStatIcon(1, 0);
        drawStatIcon(2, 0);
        drawStatIcon(3, 0);
    }

    private void drawXpBars()
    {
        int gapX = 4;
        int offsetY = 3;
        float xpPercent = (float) blockling.getBlocklingStats().getCombatXp() / (float) blockling.getXpUntilNextLevel(blockling.getBlocklingStats().getCombatLevel());
        int pixelAmount = (int)(xpPercent * XP_BAR_WIDTH);
        drawTexturedModalRect(getStatIconX(0, 0) + ICON_SIZE + gapX, getStatIconY(0, 0) + offsetY, 0, 188, pixelAmount, 5);
        drawTexturedModalRect(getStatIconX(0, 0) + ICON_SIZE + gapX + pixelAmount, getStatIconY(0, 0) + offsetY, pixelAmount, 188 + 5 * 1, XP_BAR_WIDTH - pixelAmount, 5);
        xpPercent = (float) blockling.getBlocklingStats().getMiningXp() / (float) blockling.getXpUntilNextLevel(blockling.getBlocklingStats().getMiningLevel());
        pixelAmount = (int)(xpPercent * XP_BAR_WIDTH);
        drawTexturedModalRect(getStatIconX(1, 0) + ICON_SIZE + gapX, getStatIconY(1, 0) + offsetY, 0, 188 + 5 * 2, pixelAmount, 5);
        drawTexturedModalRect(getStatIconX(1, 0) + ICON_SIZE + gapX + pixelAmount, getStatIconY(1, 0) + offsetY, pixelAmount, 188 + 5 * 3, XP_BAR_WIDTH - pixelAmount, 5);
        xpPercent = (float) blockling.getBlocklingStats().getWoodcuttingXp() / (float) blockling.getXpUntilNextLevel(blockling.getBlocklingStats().getWoodcuttingLevel());
        pixelAmount = (int)(xpPercent * XP_BAR_WIDTH);
        drawTexturedModalRect(getStatIconX(2, 0) + ICON_SIZE + gapX, getStatIconY(2, 0) + offsetY, 0, 188 + 5 * 4, pixelAmount, 5);
        drawTexturedModalRect(getStatIconX(2, 0) + ICON_SIZE + gapX + pixelAmount, getStatIconY(2, 0) + offsetY, pixelAmount, 188 + 5 * 5, XP_BAR_WIDTH - pixelAmount, 5);
        xpPercent = (float) blockling.getBlocklingStats().getFarmingXp() / (float) blockling.getXpUntilNextLevel(blockling.getBlocklingStats().getFarmingLevel());
        pixelAmount = (int)(xpPercent * XP_BAR_WIDTH);
        drawTexturedModalRect(getStatIconX(3, 0) + ICON_SIZE + gapX, getStatIconY(3, 0) + offsetY, 0, 188 + 5 * 6, pixelAmount, 5);
        drawTexturedModalRect(getStatIconX(3, 0) + ICON_SIZE + gapX + pixelAmount, getStatIconY(3, 0) + offsetY, pixelAmount, 188 + 5 * 7, XP_BAR_WIDTH - pixelAmount, 5);
    }

    private void drawStatText()
    {
        int colour = 0xffffff;
        double health = blockling.getHealth();
        double maxHealth = blockling.getMaxHealth();
        double r = 163 - 92 * (health / maxHealth), g = 0 + 171 * (health / maxHealth), b = 0 + 3 * (health / maxHealth);
        colour = (int) r;
        colour = (colour << 8) + (int) g;
        colour = (colour << 8) + (int) b;
        String healthString = Integer.toString((int) health);
        fontRenderer.drawStringWithShadow(healthString, getStatIconX(0, 1) + 3 + ICON_SIZE, getStatIconY(0, 1) + 2, colour);
        colour = 0xfbba20;
        String damageString = Integer.toString((int) blockling.getBlocklingStats().getAttackDamage());
        fontRenderer.drawStringWithShadow(damageString, getStatIconX(2, 1) - 3 - fontRenderer.getStringWidth(damageString), getStatIconY(2, 1) + 2, colour);
        String armourString = Integer.toString((int) blockling.getBlocklingStats().getArmour());
        fontRenderer.drawStringWithShadow(armourString, getStatIconX(1, 1) + 3 + ICON_SIZE, getStatIconY(1, 1) + 2, colour);
        String movementSpeedString = Integer.toString((int) (blockling.getBlocklingStats().getMovementSpeed() * 40));
        fontRenderer.drawStringWithShadow(movementSpeedString, getStatIconX(3, 1) - 3 - fontRenderer.getStringWidth(movementSpeedString), getStatIconY(3, 1) + 2, colour);


        String combatLevelText = Integer.toString(blockling.getBlocklingStats().getCombatLevel());
        colour = 0xff4d4d;
        fontRenderer.drawStringWithShadow(combatLevelText, getStatIconX(0, 0) + ICON_SIZE + 13 + XP_BAR_WIDTH - (fontRenderer.getStringWidth(combatLevelText) / 2), getStatIconY(0, 0) + 2, colour);
        String miningLevelText = Integer.toString(blockling.getBlocklingStats().getMiningLevel());
        colour = 0x7094db;
        fontRenderer.drawStringWithShadow(miningLevelText, getStatIconX(1, 0) + ICON_SIZE + 13 + XP_BAR_WIDTH - (fontRenderer.getStringWidth(miningLevelText) / 2), getStatIconY(1, 0) + 2, colour);
        String woodcuttingLevelText = Integer.toString(blockling.getBlocklingStats().getWoodcuttingLevel());
        colour = 0x57a65b;
        fontRenderer.drawStringWithShadow(woodcuttingLevelText, getStatIconX(2, 0) + ICON_SIZE + 13 + XP_BAR_WIDTH - (fontRenderer.getStringWidth(woodcuttingLevelText) / 2), getStatIconY(2, 0) + 2, colour);
        String farmingLevelText = Integer.toString(blockling.getBlocklingStats().getFarmingLevel());
        colour = 0x9d6d4a;
        fontRenderer.drawStringWithShadow(farmingLevelText, getStatIconX(3, 0) + ICON_SIZE + 13 + XP_BAR_WIDTH - (fontRenderer.getStringWidth(farmingLevelText) / 2), getStatIconY(3, 0) + 2, colour);
    }

    private int getStatIconX(int xTex, int yTex)
    {
        int offsetX = 70;

        if (xTex == 0 && yTex == 0) return centerX - offsetX;
        else if (xTex == 1 && yTex == 0) return centerX - offsetX;
        else if (xTex == 2 && yTex == 0) return centerX - offsetX;
        else if (xTex == 3 && yTex == 0) return centerX - offsetX;
        else if (xTex == 0 && yTex == 1) return centerX - offsetX;
        else if (xTex == 1 && yTex == 1) return centerX - offsetX;
        else if (xTex == 2 && yTex == 1) return centerX + offsetX - ICON_SIZE;
        else if (xTex == 3 && yTex == 1) return centerX + offsetX - ICON_SIZE;

        return -1;
    }

    private int getStatIconY(int xTex, int yTex)
    {
        int offsetY = 43;
        int startY = 90;
        int gapY = 5;

        if (xTex == 0 && yTex == 0) return uiStartY + startY;
        else if (xTex == 1 && yTex == 0) return uiStartY + startY + (gapY + ICON_SIZE);
        else if (xTex == 2 && yTex == 0) return uiStartY + startY + (gapY + ICON_SIZE) * 2;
        else if (xTex == 3 && yTex == 0) return uiStartY + startY + (gapY + ICON_SIZE) * 3;
        else if (xTex == 0 && yTex == 1) return uiStartY + offsetY;
        else if (xTex == 1 && yTex == 1) return uiStartY + offsetY + 22;
        else if (xTex == 2 && yTex == 1) return uiStartY + offsetY;
        else if (xTex == 3 && yTex == 1) return uiStartY + offsetY + 22;

        return -1;
    }

    private void drawStatIcon(int texX, int texY)
    {
        drawTexturedModalRect(getStatIconX(texX, texY), getStatIconY(texX, texY), texX * ICON_SIZE, 166 + texY * ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        nameTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        switch (keyCode)
        {
            case 28:
                nameTextField.setFocused(false);
            default:
                nameTextField.textboxKeyTyped(typedChar, keyCode);
        }
    }
}
