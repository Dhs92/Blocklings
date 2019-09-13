package willr27.blocklings.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.ability.AbilityGroup;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.GuiUtil;

public class AbilitiesScreen extends Screen
{
    private static final int WINDOW_WIDTH = 158;
    private static final int WINDOW_HEIGHT = 138;

    private AbilitiesGui abilitiesGui;
    private TabbedScreen tabbedScreen;

    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

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

        abilitiesGui = new AbilitiesGui(blockling, abilityGroup, font, WINDOW_WIDTH, WINDOW_HEIGHT, centerX, centerY + 5);
        tabbedScreen = new TabbedScreen(blockling, player, centerX, centerY);

        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        abilitiesGui.draw(mouseX, mouseY);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GuiUtil.bindTexture(GuiUtil.ABILITIES);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);

        tabbedScreen.drawTabs();

        super.render(mouseX, mouseY, partialTicks);
        tabbedScreen.drawTooltip(mouseX, mouseY, this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        abilitiesGui.mouseClicked((int) mouseX, (int) mouseY, state);
        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        abilitiesGui.mouseReleased((int) mouseX, (int) mouseY, state);
        tabbedScreen.mouseReleased((int) mouseX, (int) mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
