package willr27.blocklings.gui.screens;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.gui.util.Tab;

public class TabbedScreen extends AbstractGui
{
    public static final int OFFSET_Y = -10;

    public static final int UI_WIDTH = 232;
    public static final int UI_HEIGHT = 166;

    public static final int CONTENT_WIDTH = 176;
    public static final int CONTENT_HEIGHT = 166;

    public static final int TAB_GAP = 6;
    public static final int TAB_HEIGHT = 28;

    public static final int LEFT_TAB_OFF_WIDTH = 26;
    public static final int RIGHT_TAB_OFF_WIDTH = 28;
    public static final int LEFT_TAB_ON_WIDTH = 33;
    public static final int RIGHT_TAB_ON_WIDTH = 34;

    public static final int TAB_OFF_OFFSET_X = 3;

    public static final int LEFT_TAB_OFF_TEXTURE_X = 0;
    public static final int RIGHT_TAB_OFF_TEXTURE_X = 26;
    public static final int LEFT_TAB_ON_TEXTURE_X = 54;
    public static final int RIGHT_TAB_ON_TEXTURE_X = 87;

    public static final int ICON_TEXTURE_Y = 140;
    public static final int ICON_SIZE = 22;
    public static final int ICON_OFFSET_X = 3;
    public static final int ICON_OFFSET_Y = 3;

    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top, right, bottom;

    public TabbedScreen(BlocklingEntity blockling, PlayerEntity player, int centerX, int centerY)
    {
        this.blockling = blockling;
        this.player = player;
        this.centerX = centerX;
        this.centerY = centerY;
        this.left = centerX - UI_WIDTH / 2;
        this.top = centerY - UI_HEIGHT / 2;
        this.right = left + UI_WIDTH;
        this.bottom = top + UI_HEIGHT;
    }

    void drawTabs()
    {
        GuiUtil.bindTexture(GuiUtil.TABS);

        int i = 0;
        for (Tab tab : Tab.leftTabs)
        {
            int tabTexLocationY = TAB_HEIGHT * i;
            int iconTexLocationX = ICON_SIZE * tab.textureX;
            int iconTexLocationY = ICON_SIZE * tab.textureY + ICON_TEXTURE_Y;
            if (isActiveTab(tab))
            {
                blit(getLeftTabOnX(i), getLeftTabOnY(i), LEFT_TAB_ON_TEXTURE_X, tabTexLocationY, LEFT_TAB_ON_WIDTH, TAB_HEIGHT);
                blit(getLeftIconOnX(i), getLeftIconOnY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
            }
            else
            {
                blit(getLeftTabOffX(i), getLeftTabOffY(i), LEFT_TAB_OFF_TEXTURE_X, tabTexLocationY, LEFT_TAB_OFF_WIDTH, TAB_HEIGHT);
                blit(getLeftIconOffX(i), getLeftIconOffY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
            }
            i++;
        }

        i = 0;
        for (Tab tab : Tab.rightTabs)
        {
            int tabTexLocationY = TAB_HEIGHT * i;
            int iconTexLocationX = ICON_SIZE * tab.textureX;
            int iconTexLocationY = ICON_SIZE * tab.textureY + ICON_TEXTURE_Y;
            if (isActiveTab(tab))
            {
                blit(getRightTabOnX(i), getRightTabOnY(i), RIGHT_TAB_ON_TEXTURE_X, tabTexLocationY, RIGHT_TAB_ON_WIDTH, TAB_HEIGHT);
                blit(getRightIconOnX(i), getRightIconOnY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
            }
            else
            {
                blit(getRightTabOffX(i), getRightTabOffY(i), RIGHT_TAB_OFF_TEXTURE_X, tabTexLocationY, RIGHT_TAB_OFF_WIDTH, TAB_HEIGHT);
                blit(getRightIconOffX(i), getRightIconOffY(i), iconTexLocationX, iconTexLocationY, ICON_SIZE, ICON_SIZE);
            }
            i++;
        }
    }

    public void drawTooltip(int mouseX, int mouseY, Screen screen)
    {
        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab != null)
        {
            screen.renderTooltip(hoveredTab.name, mouseX, mouseY);
        }
    }

    public boolean mouseReleased(int mouseX, int mouseY, int state)
    {
        Tab hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab != null)
        {
            blockling.openGui(player, hoveredTab.guiId);
            return true;
        }

        return false;
    }

    private Tab getHoveredTab(int mouseX, int mouseY)
    {
        int i = 0;
        for (Tab tab : Tab.leftTabs)
        {
            if ((isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getLeftIconOffX(i), getLeftIconOffY(i), ICON_SIZE, ICON_SIZE))
             || !isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getLeftIconOnX(i), getLeftIconOnY(i), ICON_SIZE, ICON_SIZE))
            {
                return tab;
            }
            i++;
        }
        i = 0;
        for (Tab tab : Tab.rightTabs)
        {
            if ((isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getRightIconOffX(i), getRightIconOffY(i), ICON_SIZE, ICON_SIZE))
             || !isActiveTab(tab) && GuiUtil.isMouseOver(mouseX, mouseY, getRightIconOnX(i), getRightIconOnY(i), ICON_SIZE, ICON_SIZE))
            {
                return tab;
            }
            i++;
        }

        return null;
    }

    private Tab getActiveTab()
    {
        for (Tab tab : Tab.values())
        {
            if (tab.guiId == blockling.getGuiInfo().mostRecentTabbedGuiId)
            {
                return tab;
            }
        }
        return null;
    }

    private boolean isActiveTab(Tab tab)
    {
        return tab == getActiveTab();
    }

    private int getLeftIconOffX(int i)
    {
        return getLeftTabOffX(i) + ICON_OFFSET_X;
    }
    private int getLeftIconOffY(int i)
    {
        return getLeftTabOffY(i) + ICON_OFFSET_Y;
    }

    private int getRightIconOffX(int i)
    {
        return getRightTabOffX(i) + RIGHT_TAB_OFF_WIDTH - ICON_SIZE - ICON_OFFSET_X;
    }
    private int getRightIconOffY(int i)
    {
        return getRightTabOffY(i) + ICON_OFFSET_Y;
    }

    private int getLeftTabOffX(int i)
    {
        return left + TAB_OFF_OFFSET_X;
    }
    private int getLeftTabOffY(int i)
    {
        return top + 1 + ((TAB_HEIGHT + TAB_GAP) * i);
    }

    private int getRightTabOffX(int i)
    {
        return right - RIGHT_TAB_OFF_WIDTH - TAB_OFF_OFFSET_X;
    }
    private int getRightTabOffY(int i)
    {
        return top + 1 + ((TAB_HEIGHT + TAB_GAP) * i);
    }



    private int getLeftIconOnX(int i)
    {
        return getLeftTabOnX(i) + ICON_OFFSET_X + TAB_OFF_OFFSET_X;
    }
    private int getLeftIconOnY(int i)
    {
        return getLeftTabOnY(i) + ICON_OFFSET_Y;
    }

    private int getRightIconOnX(int i)
    {
        return getRightTabOnX(i) + RIGHT_TAB_ON_WIDTH - ICON_SIZE - ICON_OFFSET_X - TAB_OFF_OFFSET_X - 1;
    }
    private int getRightIconOnY(int i)
    {
        return getRightTabOnY(i) + ICON_OFFSET_Y;
    }

    private int getLeftTabOnX(int i)
    {
        return left - 1;
    }
    private int getLeftTabOnY(int i)
    {
        return top + 1 + ((TAB_HEIGHT + TAB_GAP) * i);
    }

    private int getRightTabOnX(int i)
    {
        return right + 2 - RIGHT_TAB_ON_WIDTH;
    }
    private int getRightTabOnY(int i)
    {
        return top + 1 + ((TAB_HEIGHT + TAB_GAP) * i);
    }
}
