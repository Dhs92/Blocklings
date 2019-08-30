package com.blocklings.gui.screens;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.State;
import com.blocklings.util.Task;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiBlocklingTasks extends GuiBlocklingTabbed
{
    private static final ResourceLocation TASKS = new ResourceLocationBlocklings("textures/gui/inv_tasks.png");
    private static final ResourceLocation TASKS_WIDGETS = new ResourceLocationBlocklings("textures/gui/inv_tasks_widgets.png");

    public GuiBlocklingTasks(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(TASKS);
        drawTexturedModalRect(left + ((UI_WIDTH - SCREEN_TEXTURE_WIDTH) / 2), top, 0, 0, SCREEN_TEXTURE_WIDTH, SCREEN_TEXTURE_HEIGHT);

        mc.getTextureManager().bindTexture(TASKS_WIDGETS);

        for (State state : State.values())
        {
            if (state == getHoveredState(mouseX, mouseY)) GlStateManager.color(0.8f, 0.8f, 1.0f, 1.0f);
            int buttonTexX = state == blockling.getState() ? State.BUTTON_SIZE : 0;
            drawTexturedModalRect(getButtonLeft(state), getButtonTop(state), buttonTexX, 0, State.BUTTON_SIZE, State.BUTTON_SIZE);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            drawTexturedModalRect(getIconLeft(state), getIconTop(state), state.xTex, state.yTex, State.ICON_SIZE, State.ICON_SIZE);
        }

        for (Task task : Task.values())
        {
            if (task == getHoveredTask(mouseX, mouseY)) GlStateManager.color(0.8f, 0.8f, 1.0f, 1.0f);
            int buttonTexX = blockling.isTaskActive(task) ? Task.BUTTON_SIZE * 2 : 0;
            drawTexturedModalRect(getButtonLeft(task), getButtonTop(task), buttonTexX, 0, Task.BUTTON_SIZE, Task.BUTTON_SIZE);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            drawTexturedModalRect(getIconLeft(task), getIconTop(task), task.xTex, task.yTex, Task.ICON_SIZE, Task.ICON_SIZE);
        }

        fontRenderer.drawStringWithShadow("State", left + 36, top + 7, 0xffffff);
        fontRenderer.drawStringWithShadow("Tasks", left + 36, top + 52, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);

        State hoveredState = getHoveredState(mouseX, mouseY);
        if (hoveredState != null)
        {
            drawHoveringText(hoveredState.name, mouseX, mouseY);
        }

        Task hoveredTask = getHoveredTask(mouseX, mouseY);
        if (hoveredTask != null)
        {
            drawHoveringText(hoveredTask.name, mouseX, mouseY);
        }
    }

    private int getButtonLeft(State state)
    {
        return left + 41 + (State.BUTTON_SIZE + 6) * (state.ordinal() % 6);
    }
    private int getButtonTop(State state)
    {
        return top + 23 + (State.BUTTON_SIZE + 6) * (state.ordinal() / 6);
    }
    private int getIconLeft(State state)
    {
        return getButtonLeft(state) + (State.BUTTON_SIZE - State.ICON_SIZE) / 2;
    }
    private int getIconTop(State state)
    {
        return getButtonTop(state) + (State.BUTTON_SIZE - State.ICON_SIZE) / 2;
    }

    private int getButtonLeft(Task task)
    {
        return left + 41 + (Task.BUTTON_SIZE + 6) * (task.ordinal() % 6);
    }
    private int getButtonTop(Task task)
    {
        return top + 68 + (Task.BUTTON_SIZE + 6) * (task.ordinal() / 6);
    }
    private int getIconLeft(Task task)
    {
        return getButtonLeft(task) + (State.BUTTON_SIZE - State.ICON_SIZE) / 2;
    }
    private int getIconTop(Task task)
    {
        return getButtonTop(task) + (State.BUTTON_SIZE - State.ICON_SIZE) / 2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        State hoveredState = getHoveredState(mouseX, mouseY);
        if (hoveredState != null)
        {
            blockling.setState(hoveredState);
        }

        Task hoveredTask = getHoveredTask(mouseX, mouseY);
        if (hoveredTask != null)
        {
            blockling.toggleTask(hoveredTask);
        }
    }

    private State getHoveredState(int mouseX, int mouseY)
    {
        for (State state : State.values())
        {
            if (isMouseOver(mouseX, mouseY, getButtonLeft(state), getButtonTop(state), State.BUTTON_SIZE, State.BUTTON_SIZE))
            {
                return state;
            }
        }

        return null;
    }

    private Task getHoveredTask(int mouseX, int mouseY)
    {
        for (Task task : Task.values())
        {
            if (isMouseOver(mouseX, mouseY, getButtonLeft(task), getButtonTop(task), Task.BUTTON_SIZE, Task.BUTTON_SIZE))
            {
                return task;
            }
        }

        return null;
    }
}
