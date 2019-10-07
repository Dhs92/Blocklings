package willr27.blocklings.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.abilities.AbilityGroup;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.gui.util.widgets.Widget;

public class AbilitiesScreen extends Screen
{
    private static final int WINDOW_WIDTH = 158;
    private static final int WINDOW_HEIGHT = 138;

    private static final int MAXIMISE_WIDTH = 300;
    private static final int MAXIMISE_HEIGHT = 190;
    private static final int MAXIMISE_X = 180;
    private static final int MAXIMISE_Y = 142;
    private static final int MAXIMISE_TEXTURE_Y = 206;
    private static final int MAXIMISE_SIZE = 11;

    private AbilitiesGui abilitiesGui;
    private TabbedScreen tabbedScreen;
    private Widget maximiseWidget;

    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;
    private boolean maximised;

    private int firstOpenDelay = 20;

    private AbilityGroup abilityGroup;

    public AbilitiesScreen(BlocklingEntity blockling, PlayerEntity player)
    {
        super(new StringTextComponent("Abilities"));
        this.blockling = blockling;
        this.player = player;

        abilityGroup = blockling.abilityManager.getGroup(blockling.getGuiInfo().abilityGroupId);
    }

    @Override
    protected void init()
    {
        centerX = width / 2;
        centerY = height / 2 + TabbedScreen.OFFSET_Y;

        left = centerX - TabbedScreen.UI_WIDTH / 2;
        top = centerY - TabbedScreen.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedScreen.CONTENT_WIDTH / 2;
        contentTop = top;

        abilitiesGui = new AbilitiesGui(blockling, abilityGroup, font, WINDOW_WIDTH, WINDOW_HEIGHT, centerX, centerY + 5, width, height);
        if (maximised) abilitiesGui.resize(MAXIMISE_WIDTH, MAXIMISE_HEIGHT);
        tabbedScreen = new TabbedScreen(blockling, player, centerX, centerY);
        maximiseWidget = new Widget(font, left + MAXIMISE_X, top + MAXIMISE_Y, MAXIMISE_SIZE, MAXIMISE_SIZE, 0, MAXIMISE_TEXTURE_Y);

        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (firstOpenDelay > 0) firstOpenDelay--;

        abilitiesGui.draw(mouseX, mouseY);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GuiUtil.bindTexture(GuiUtil.ABILITIES);
        String points = blockling.getStats().skillPoints.getDisplayString();
        if (!maximised)
        {
            blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);
            font.drawStringWithShadow(points, left + 184 - font.getStringWidth(points), top + 7, 0xffffff);
            font.drawStringWithShadow("Abilities", left + 36, top + 7, 0xffffff);
        }
        else
        { // TODO: MAKE DYNAMIC
            int left = centerX - MAXIMISE_WIDTH / 2;
            int top = centerY - MAXIMISE_HEIGHT / 2;
            int right = left + MAXIMISE_WIDTH;
            int bottom = top + MAXIMISE_HEIGHT;
            blit(left - 9, top - 13, 0, 0, 120, 108);
            blit(right - 120 + 9, top - 13, 176 - 120, 0, 120, 108);
            blit(left - 9, bottom - 108 + 13, 0, 166 - 108, 120, 108);
            blit(right - 120 + 9, bottom - 108 + 13, 176 - 120, 166 - 108, 120, 108);
            blit(left + 111, top - 13, 30, 0, 78, 30);
            blit(left + 111, bottom - 30 + 13, 30, 166 - 30, 78, 30);
            font.drawStringWithShadow(points, right - 11 - font.getStringWidth(points), top - 6, 0xffffff);
            font.drawStringWithShadow("Abilities", left, top - 6, 0xffffff);
        }

        GuiUtil.bindTexture(GuiUtil.ABILITIES);

        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 10.0f);
        maximiseWidget.textureX = maximiseWidget.isMouseOver(mouseX, mouseY) && !abilitiesGui.isDragging() ? 0 : MAXIMISE_SIZE;
        if (!maximised) maximiseWidget.render(mouseX, mouseY);
        GlStateManager.popMatrix();

        if (!maximised) tabbedScreen.drawTabs();

        super.render(mouseX, mouseY, partialTicks);
        if (!maximised) tabbedScreen.drawTooltip(mouseX, mouseY, this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (firstOpenDelay > 0)
        {
            return true;
        }

        abilitiesGui.mouseClicked((int) mouseX, (int) mouseY, state);
        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (firstOpenDelay > 0)
        {
            return true;
        }

        if (abilitiesGui.mouseReleased((int) mouseX, (int) mouseY, state))
        {
            return true;
        }

        if (!maximised && !abilitiesGui.isDragging() && maximiseWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            abilitiesGui.resize(MAXIMISE_WIDTH, MAXIMISE_HEIGHT);
            maximised = true;
            return true;
        }

        if (!maximised) tabbedScreen.mouseReleased((int) mouseX, (int) mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (abilitiesGui.keyPressed(keyCode, i, j))
        {
            return true;
        }

        if (keyCode == 256 && maximised)
        {
            maximised = false;
            abilitiesGui.resize(WINDOW_WIDTH, WINDOW_HEIGHT);
            return true;
        }

        return super.keyPressed(keyCode, i, j);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
