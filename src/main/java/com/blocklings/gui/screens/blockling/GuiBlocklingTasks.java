package com.blocklings.gui.screens.blockling;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.gui.GuiHandler;
import com.blocklings.util.ResourceLocationBlocklings;
import com.blocklings.util.State;
import com.blocklings.util.Tab;
import com.blocklings.util.Task;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiBlocklingTasks extends GuiBlocklingTabbed
{
    private static final ResourceLocation TASKS = new ResourceLocationBlocklings("textures/gui/inv_tasks.png");
    private static final ResourceLocation TASKS_WIDGETS = new ResourceLocationBlocklings("textures/gui/inv_tasks_widgets.png");

    private Task potentialSelectedTask;
    private Task selectedTask;
    private int startMouseX, startMouseY;

    public GuiBlocklingTasks(EntityBlockling blockling, EntityPlayer player)
    {
        super(blockling, player);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!mouseDown)
        {
            selectedTask = null;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (mouseDown && potentialSelectedTask != null && (Math.abs(mouseX - startMouseX) > 3 || Math.abs(mouseY - startMouseY) > 3))
        {
            selectedTask = potentialSelectedTask;
            potentialSelectedTask = null;
        }

        drawTabToolTips = selectedTask == null;

        mc.getTextureManager().bindTexture(TASKS);
        drawTexturedModalRect(uiStartX + ((UI_WIDTH - MAIN_TEXTURE_WIDTH) / 2), uiStartY, 0, 0, MAIN_TEXTURE_WIDTH, MAIN_TEXTURE_HEIGHT);

        mc.getTextureManager().bindTexture(TASKS_WIDGETS);

        for (State state : State.values())
        {
            if (state == getHoveredState(mouseX, mouseY)) GlStateManager.color(0.8f, 0.8f, 1.0f, 1.0f);
            int buttonTexX = state == blockling.getState() ? State.BUTTON_SIZE : 0;
            drawTexturedModalRect(getButtonLeft(state), getButtonTop(state), buttonTexX, 0, State.BUTTON_SIZE, State.BUTTON_SIZE);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            drawTexturedModalRect(getIconLeft(state), getIconTop(state), state.xTex, state.yTex, State.ICON_SIZE, State.ICON_SIZE);
        }

        for (Task task : blockling.getTasksInPriorityOrder())
        {
            if (task != selectedTask)
            {
                if (task == getHoveredTask(mouseX, mouseY)) GlStateManager.color(0.8f, 0.8f, 1.0f, 1.0f);
                int buttonTexX = blockling.isTaskActive(task) ? Task.BUTTON_SIZE * 2 : 0;
                drawTexturedModalRect(getButtonLeft(task), getButtonTop(task), buttonTexX, 0, Task.BUTTON_SIZE, Task.BUTTON_SIZE);
                GlStateManager.color(1.0f, 1.0f, 1.0f);
                drawTexturedModalRect(getIconLeft(task), getIconTop(task), task.xTex, task.yTex, Task.ICON_SIZE, Task.ICON_SIZE);
            }
        }

        fontRenderer.drawString("State", uiStartX + 36, uiStartY + 6, 0xffffff, false);
        fontRenderer.drawString("Tasks", uiStartX + 36, uiStartY + 51, 0xffffff, false);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (selectedTask != null)
        {
            mc.getTextureManager().bindTexture(TASKS_WIDGETS);
            int buttonTexX = blockling.isTaskActive(selectedTask) ? Task.BUTTON_SIZE * 2 : 0;
            drawTexturedModalRect(mouseX - Task.BUTTON_SIZE / 2, mouseY - Task.BUTTON_SIZE / 2, buttonTexX, 0, Task.BUTTON_SIZE, Task.BUTTON_SIZE);
            drawTexturedModalRect(mouseX - Task.ICON_SIZE / 2, mouseY - Task.ICON_SIZE / 2, selectedTask.xTex, selectedTask.yTex, Task.ICON_SIZE, Task.ICON_SIZE);
        }

        State hoveredState = getHoveredState(mouseX, mouseY);
        if (hoveredState != null)
        {
            drawHoveringText(hoveredState.name, mouseX, mouseY);
        }

        Task hoveredTask = getHoveredTask(mouseX, mouseY);
        if (hoveredTask != null && hoveredTask != selectedTask)
        {
            drawHoveringText(hoveredTask.name, mouseX, mouseY);
        }

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    private int getButtonLeft(State state)
    {
        return uiStartX + 41 + (State.BUTTON_SIZE + 6) * (state.ordinal() % 6);
    }
    private int getButtonTop(State state)
    {
        return uiStartY + 21 + (State.BUTTON_SIZE + 6) * (state.ordinal() / 6);
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
        return uiStartX + 41 + (Task.BUTTON_SIZE + 6) * (blockling.getTaskPriority(task) % 6);
    }
    private int getButtonTop(Task task)
    {
        return uiStartY + 66 + (Task.BUTTON_SIZE + 6) * (blockling.getTaskPriority(task) / 6);
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
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseDown)
        {
            potentialSelectedTask = getHoveredTask(mouseX, mouseY);
            startMouseX = mouseX;
            startMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        State hoveredState = getHoveredState(mouseX, mouseY);
        if (hoveredState != null)
        {
            blockling.setState(hoveredState, false);
        }

        Task hoveredTask = getHoveredTask(mouseX, mouseY);
        if (hoveredTask != null)
        {
            if (Keyboard.isKeyDown(29))
            {
                blockling.openConfigGui(player, GuiHandler.ENTITY_WHITELIST_ID, Tab.TASKS.ordinal(), hoveredTask.whitelistId);
            }
            else if (selectedTask == null)
            {
                blockling.toggleTask(hoveredTask, false);
            }
            else if (hoveredTask != selectedTask)
            {
                blockling.setTaskPriority(selectedTask, blockling.getTaskPriority(hoveredTask));
            }
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        // Only change states on the server when we leave the GUI
        blockling.setState(blockling.getState(), true);
        for (Task task : Task.values())
        {
            blockling.setTask(task, blockling.isTaskActive(task), true);
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
