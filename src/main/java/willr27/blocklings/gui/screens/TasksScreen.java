package willr27.blocklings.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import willr27.blocklings.entity.ai.goals.GoalInfo;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.GuiHandler;
import willr27.blocklings.gui.util.GuiUtil;

import java.util.ArrayList;
import java.util.List;

public class TasksScreen extends Screen
{
    private TabbedScreen tabbedScreen;

    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

    private boolean mouseDown;
    private int selectedStartX, selectedStartY;
    private GoalInfo potentialGoal, selectedGoal;

    public TasksScreen(BlocklingEntity blockling, PlayerEntity player)
    {
        super(new StringTextComponent("Equipment"));
        this.blockling = blockling;
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

        tabbedScreen = new TabbedScreen(blockling, player, centerX, centerY);

        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.TASKS);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);
        drawTasks(mouseX, mouseY);

        if (mouseDown)
        {
            if (potentialGoal != null)
            {
                boolean hasMovedMouseEnough = 4 < Math.max(Math.abs(selectedStartX - mouseX), Math.abs(selectedStartY - mouseY));
                if (hasMovedMouseEnough)
                {
                    selectedGoal = potentialGoal;
                    potentialGoal = null;
                }
            }
        }
        else
        {
            potentialGoal = null;
            selectedGoal = null;
        }

        tabbedScreen.drawTabs();

        super.render(mouseX, mouseY, partialTicks);
        tabbedScreen.drawTooltip(mouseX, mouseY, this);
        drawTooltips(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        mouseDown = true;

        GoalInfo hoveredGoal = getHoveredGoal((int) mouseX, (int) mouseY);
        if (hoveredGoal != null)
        {
            selectedStartX = (int) mouseX;
            selectedStartY = (int) mouseY;
            potentialGoal = hoveredGoal;
        }

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        GoalInfo hoveredGoal = getHoveredGoal((int) mouseX, (int) mouseY);
        if (selectedGoal == null)
        {
            if (hoveredGoal != null)
            {
                if (GuiUtil.isKeyDown(340) && hoveredGoal.hasWhitelist())
                {
                    blockling.openGui(player, GuiHandler.WHITELIST_ID, hoveredGoal.goalId, -1, -1);
                }
                else
                {
                    hoveredGoal.toggleActive();
                }
            }
        }
        else
        {
            if (hoveredGoal != null && hoveredGoal != selectedGoal)
            {
                selectedGoal.setPriority(hoveredGoal.getPriority());
            }
        }

        mouseDown = false;
        tabbedScreen.mouseReleased((int)mouseX, (int)mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    private static final int BUTTONS_PER_ROW = 6;
    private static final int BUTTON_SIZE = 20;
    private static final int BUTTON_TEXTURE_Y = 166;
    private static final int BUTTON_GAP = 5;
    private static final int BUTTON_OFFSET_X = 15;
    private static final int BUTTON_OFFSET_Y = 22;

    private static final int BUTTON_ACTIVE_TEXTURE_X = 20;

    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXTURE_Y = 186;

    private void drawTasks(int mouseX, int mouseY)
    {
        GoalInfo hoveredGoal = getHoveredGoal(mouseX, mouseY);

        int i = 0;
        for (GoalInfo goal : blockling.aiManager.getGoals())
        {
            if (goal.isUnlocked())
            {
                boolean isSelectedGoal = goal == selectedGoal;

                if (goal == hoveredGoal && !isSelectedGoal) GlStateManager.color3f(0.5f, 0.7f, 0.9f);
                else GlStateManager.color3f(1.0f, 1.0f, 1.0f);

                int buttonTextureX = goal.isActive() ? BUTTON_ACTIVE_TEXTURE_X : 0;
                int iconTextureX = goal.iconX * ICON_SIZE;
                int iconTextureY = goal.iconY * ICON_SIZE + ICON_TEXTURE_Y;

                int buttonX;
                int buttonY;
                int iconX;
                int iconY;

                if (isSelectedGoal)
                {
                    buttonX = getButtonMouseX(mouseX);
                    buttonY = getButtonMouseY(mouseY);
                    iconX = getIconMouseX(mouseX);
                    iconY = getIconMouseY(mouseY);
                }
                else
                {
                    buttonX = getButtonX(i);
                    buttonY = getButtonY(i);
                    iconX = getIconX(i);
                    iconY = getIconY(i);
                }

                if (isSelectedGoal) GlStateManager.translated(0.0, 0.0, 10);

                blit(buttonX, buttonY, buttonTextureX, BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);
                blit(iconX, iconY, iconTextureX, iconTextureY, ICON_SIZE, ICON_SIZE);

                if (isSelectedGoal) GlStateManager.translated(0.0, 0.0, -10);

                i++;
            }
        }

        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
    }

    private void drawTooltips(int mouseX, int mouseY)
    {
        GoalInfo hoveredGoal = getHoveredGoal(mouseX, mouseY);
        if (hoveredGoal != null && hoveredGoal != selectedGoal)
        {
            List<String> text = new ArrayList<>();
            text.add(TextFormatting.GOLD + hoveredGoal.name);
            if (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340))
            {
                text.add("");
                text.addAll(GuiUtil.splitText(font, hoveredGoal.description, 150));
                if (hoveredGoal.hasWhitelist())
                {
                    text.add("");
                    text.add(TextFormatting.WHITE + "" + TextFormatting.UNDERLINE + "Shift+click for whitelist(s)");
                }
            }
            else
            {
                text.add(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "" + "Hold shift...");
            }
            renderTooltip(text, mouseX, mouseY);
        }
    }

    private GoalInfo getHoveredGoal(int mouseX, int mouseY)
    {
        // TODO: INEFFICIENCIES HERE
        int i = 0;
        for (GoalInfo goal : blockling.aiManager.getGoals())
        {
            if (goal.isUnlocked())
            {
                if (GuiUtil.isMouseOver(mouseX, mouseY, getButtonX(i), getButtonY(i), BUTTON_SIZE, BUTTON_SIZE))
                {
                    return goal;
                }
                i++;
            }
        }
        return null;
    }

    private int getIconMouseX(int mouseX)
    {
        return getButtonMouseX(mouseX) + (BUTTON_SIZE - ICON_SIZE) / 2;
    }
    private int getIconMouseY(int mouseY)
    {
        return getButtonMouseY(mouseY) + (BUTTON_SIZE - ICON_SIZE) / 2;
    }
    private int getButtonMouseX(int mouseX)
    {
        return mouseX - BUTTON_SIZE / 2;
    }
    private int getButtonMouseY(int mouseY)
    {
        return mouseY - BUTTON_SIZE / 2;
    }

    private int getIconX(int i)
    {
        return getButtonX(i) + (BUTTON_SIZE - ICON_SIZE) / 2;
    }
    private int getIconY(int i)
    {
        return getButtonY(i) + (BUTTON_SIZE - ICON_SIZE) / 2;
    }
    private int getButtonX(int i)
    {
        return contentLeft + BUTTON_OFFSET_X + ((i % BUTTONS_PER_ROW) * (BUTTON_SIZE + BUTTON_GAP));
    }
    private int getButtonY(int i)
    {
        return top + BUTTON_OFFSET_Y + ((i / BUTTONS_PER_ROW) * (BUTTON_SIZE + BUTTON_GAP));
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
