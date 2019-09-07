package willr27.blocklings.gui.screens;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.entity.blockling.BlocklingStats;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.gui.util.Icon;

public class StatsScreen extends Screen
{
    private TabbedScreen tabbedScreen;

    private BlocklingEntity blockling;
    private BlocklingStats stats;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;
    private int contentRight, contentBottom;

    private static final int ICON_SIZE = 11;
    private static final int XP_BAR_WIDTH = 111;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int STAT_ICON_TEXTURE_Y = 166;
    private static final int LEVEL_ICON_TEXTURE_Y = 177;

    private static final int HEALTH_ICON_TEXTURE_X = 0;
    private static final int ARMOUR_ICON_TEXTURE_X = ICON_SIZE;
    private static final int DAMAGE_ICON_TEXTURE_X = ICON_SIZE * 2;
    private static final int SPEED_ICON_TEXTURE_X = ICON_SIZE * 3;
    private static final int LEFT_ICON_X = 20;
    private static final int TOP_ICON_Y = 40;
    private static final int BOTTOM_ICON_Y = 68;

    private static final int COMBAT_ICON_TEXTURE_X = 0;
    private static final int MINING_ICON_TEXTURE_X = ICON_SIZE;
    private static final int WOODCUTTING_ICON_TEXTURE_X = ICON_SIZE * 2;
    private static final int FARMING_ICON_TEXTURE_X = ICON_SIZE * 3;
    private static final int LEVEL_XP_GAP = 16;
    private static final int LEVEL_ICON_X = 15;
    private static final int COMBAT_ICON_Y = 90;
    private static final int MINING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_ICON_Y = COMBAT_ICON_Y + LEVEL_XP_GAP * 3;

    private static final int COMBAT_XP_BAR_TEXTURE_Y = 188;
    private static final int MINING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 2;
    private static final int WOODCUTTING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 4;
    private static final int FARMING_XP_BAR_TEXTURE_Y = COMBAT_XP_BAR_TEXTURE_Y + XP_BAR_HEIGHT * 6;
    private static final int XP_BAR_X = 31;
    private static final int COMBAT_XP_BAR_Y = 93;
    private static final int MINING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP;
    private static final int WOODCUTTING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 2;
    private static final int FARMING_XP_BAR_Y = COMBAT_XP_BAR_Y + LEVEL_XP_GAP * 3;

    private Icon healthIcon;
    private Icon armourIcon;
    private Icon damageIcon;
    private Icon speedIcon;

    private Icon combatIcon;
    private Icon miningIcon;
    private Icon woodcuttingIcon;
    private Icon farmingIcon;

    private XpBar combatXpBar;
    private XpBar miningXpBar;
    private XpBar woodcuttingXpBar;
    private XpBar farmingXpBar;

    public StatsScreen(BlocklingEntity blockling, PlayerEntity player)
    {
        super(new StringTextComponent("Stats"));
        this.blockling = blockling;
        this.stats = blockling.getStats();
        this.player = player;
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
        contentRight = contentLeft + TabbedScreen.CONTENT_WIDTH;
        contentBottom = contentTop + TabbedScreen.CONTENT_HEIGHT;

        tabbedScreen = new TabbedScreen(blockling, player, centerX, centerY);

        healthIcon = new Icon(font, contentLeft + LEFT_ICON_X, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, HEALTH_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);
        armourIcon = new Icon(font, contentLeft + LEFT_ICON_X, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, ARMOUR_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);
        damageIcon = new Icon(font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + TOP_ICON_Y, ICON_SIZE, ICON_SIZE, DAMAGE_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);
        speedIcon = new Icon(font, contentRight - LEFT_ICON_X - ICON_SIZE, contentTop + BOTTOM_ICON_Y, ICON_SIZE, ICON_SIZE, SPEED_ICON_TEXTURE_X, STAT_ICON_TEXTURE_Y);

        combatIcon = new Icon(font, contentLeft + LEVEL_ICON_X, contentTop + COMBAT_ICON_Y, ICON_SIZE, ICON_SIZE, COMBAT_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);
        miningIcon = new Icon(font, contentLeft + LEVEL_ICON_X, contentTop + MINING_ICON_Y, ICON_SIZE, ICON_SIZE, MINING_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);
        woodcuttingIcon = new Icon(font, contentLeft + LEVEL_ICON_X, contentTop + WOODCUTTING_ICON_Y, ICON_SIZE, ICON_SIZE, WOODCUTTING_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);
        farmingIcon = new Icon(font, contentLeft + LEVEL_ICON_X, contentTop + FARMING_ICON_Y, ICON_SIZE, ICON_SIZE, FARMING_ICON_TEXTURE_X, LEVEL_ICON_TEXTURE_Y);

        combatXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + COMBAT_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, COMBAT_XP_BAR_TEXTURE_Y);
        miningXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + MINING_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, MINING_XP_BAR_TEXTURE_Y);
        woodcuttingXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + WOODCUTTING_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, WOODCUTTING_XP_BAR_TEXTURE_Y);
        farmingXpBar = new XpBar(font, contentLeft + XP_BAR_X, contentTop + FARMING_XP_BAR_Y, XP_BAR_WIDTH, XP_BAR_HEIGHT, 0, FARMING_XP_BAR_TEXTURE_Y);

        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);

        drawStatIcons(mouseX, mouseY);
        drawXpBars(mouseX, mouseY);
        GuiUtil.drawEntityOnScreen(centerX, centerY, 40, centerX - mouseX, centerY - mouseY, blockling);
        tabbedScreen.drawTabs();

        super.render(mouseX, mouseY, partialTicks);
        tabbedScreen.drawTooltip(mouseX, mouseY, this);
    }

    private void drawStatIcons(int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);

        healthIcon.render(mouseX, mouseY);
        armourIcon.render(mouseX, mouseY);
        damageIcon.render(mouseX, mouseY);
        speedIcon.render(mouseX, mouseY);

        combatIcon.render(mouseX, mouseY);
        miningIcon.render(mouseX, mouseY);
        woodcuttingIcon.render(mouseX, mouseY);
        farmingIcon.render(mouseX, mouseY);

        healthIcon.renderText(Integer.toString((int)blockling.getHealth()), 4, 1, false, 0xffe100);
        armourIcon.renderText(Integer.toString((int)stats.getAttackDamage()), 4, 1, false, 0xffe100);
        damageIcon.renderText(Integer.toString((int)stats.getArmour()), 4, 1, true, 0xffe100);
        speedIcon.renderText(Integer.toString((int)(stats.getMovementSpeed() * 40.0)), 4, 1, true, 0xffe100);
    }

    private void drawXpBars(int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.STATS);

        combatXpBar.render(mouseX, mouseY, stats.getCombatXp(), stats.getCombatLevel());
        miningXpBar.render(mouseX, mouseY, stats.getMiningXp(), stats.getMiningLevel());
        woodcuttingXpBar.render(mouseX, mouseY, stats.getWoodcuttingXp(), stats.getWoodcuttingLevel());
        farmingXpBar.render(mouseX, mouseY, stats.getFarmingXp(), stats.getFarmingLevel());

        combatXpBar.renderText(Integer.toString(stats.getCombatLevel()), 6, -1, false, 0xff4d4d);
        miningXpBar.renderText(Integer.toString(stats.getMiningLevel()), 6, -1, false, 0x7094db);
        woodcuttingXpBar.renderText(Integer.toString(stats.getWoodcuttingLevel()), 6, -1, false, 0x57a65b);
        farmingXpBar.renderText(Integer.toString(stats.getFarmingLevel()), 6, -1, false, 0x9d6d4a);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        tabbedScreen.mouseReleased((int)mouseX, (int)mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    private class XpBar extends Icon
    {
        public XpBar(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
        {
            super(font, x, y, width, height, textureX, textureY);
        }

        public void render(int mouseX, int mouseY, int xp, int level)
        {
            double percentage = xp / (double) BlocklingStats.getXpUntilNextLevel(level);
            int middle = (int)(width * percentage);

            blit(x, y, textureX, textureY + height, width, height);
            blit(x, y, textureX, textureY, middle, height);
        }
    }
}
