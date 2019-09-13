package willr27.blocklings.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import willr27.blocklings.ability.Ability;
import willr27.blocklings.ability.AbilityGroup;
import willr27.blocklings.ability.AbilityState;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.AbilityWidget;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.gui.util.Widget;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class AbilitiesGui extends AbstractGui
{
    private static final int TILE_SIZE = 16;
    private static final int ABILITY_SIZE = 24;

    private static final int LINE_INNER_WIDTH = 2;
    private static final int LINE_BORDER_WIDTH = 4;

    private BlocklingEntity blockling;
    private AbilityGroup abilityGroup;
    private FontRenderer font;
    private int width, height;
    private int centerX, centerY;
    private int left, top, right, bottom;
    private int tilesX, tilesY;
    private int prevMouseX, prevMouseY;
    private int moveX, moveY;
    private int backgroundOffsetX, backgroundOffsetY;
    private boolean moving;
    private Widget windowWidget;

    public AbilitiesGui(BlocklingEntity blockling, AbilityGroup abilityGroup, FontRenderer font, int width, int height, int centerX, int centerY)
    {
        this.blockling = blockling;
        this.abilityGroup = abilityGroup;
        this.font = font;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.left = centerX - width / 2;
        this.top = centerY - height / 2;
        this.right = left + width;
        this.bottom = top + height;
        this.tilesX = width / TILE_SIZE;
        this.tilesY = height / TILE_SIZE;
        this.backgroundOffsetX = blockling.random.nextInt(5000);
        this.backgroundOffsetY = blockling.random.nextInt(5000);

        moveX = width / 2 - 12;
        moveY = height / 2 - 12;
        windowWidget = new Widget(font, left, top, width, height, 0, 0);
    }

    public void draw(int mouseX, int mouseY)
    {
        if (moving)
        {
            moveX += mouseX - prevMouseX;
            moveY += mouseY - prevMouseY;
        }

        drawBackground();
        drawAbilities(mouseX, mouseY);

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    private void drawAbilities(int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.ABILITIES_WIDGETS);

        int x = left + moveX;
        int y = top + moveY;

        for (Ability ability : abilityGroup.getAbilities())
        {
            if (ability.parent == null)
            {
                continue;
            }

            AbilityWidget abilityWidget = new AbilityWidget(font, ability.x + x, ability.y + y, ABILITY_SIZE, ABILITY_SIZE, ability.type.textureX * ABILITY_SIZE, 0);
            AbilityWidget parentWidget = new AbilityWidget(font, ability.parent.x + x, ability.parent.y + y, ABILITY_SIZE, ABILITY_SIZE, ability.parent.type.textureX * ABILITY_SIZE, 0);

            abilityWidget.connect(parentWidget, LINE_BORDER_WIDTH, 0xff000000, left, right, top, bottom);
            abilityWidget.connect(parentWidget, LINE_INNER_WIDTH, abilityGroup.getState(ability).colour, left, right, top, bottom);
        }

        boolean foundHover = false;
        for (Ability ability : abilityGroup.getAbilities())
        {
            AbilityWidget abilityWidget = new AbilityWidget(font, ability.x + x, ability.y + y, ABILITY_SIZE, ABILITY_SIZE, ability.type.textureX * ABILITY_SIZE, 0);

            boolean isHover = false;
            if (!foundHover && windowWidget.isMouseOver(mouseX, mouseY))
            {
                if (abilityWidget.isMouseOver(mouseX, mouseY))
                {
                    drawAbilityHover(ability, abilityWidget);
                    foundHover = true;
                    isHover = true;
                }
            }

            GlStateManager.pushMatrix();
            if (isHover) GlStateManager.translatef(0.0f, 0.0f, 20.0f);

            Color colour = new Color(abilityGroup.getState(ability).colour);
            GlStateManager.color3f(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f);
            if (isHover) abilityWidget.render(mouseX, mouseY);
            else abilityWidget.render(mouseX, mouseY, left, right, top, bottom);

            GlStateManager.color3f(1.0f, 1.0f, 1.0f);
            abilityWidget = new AbilityWidget(font, ability.x + x, ability.y + y, ABILITY_SIZE, ABILITY_SIZE, ability.textureX * ABILITY_SIZE, (ability.textureY + 1) * ABILITY_SIZE);
            if (isHover) abilityWidget.render(mouseX, mouseY);
            else abilityWidget.render(mouseX, mouseY, left, right, top, bottom);

            GlStateManager.popMatrix();
        }
    }

    private static final int HOVER_PADDING = 5;
    private static final int DESCRIPTION_START_OFFSET_Y = 4;
    private static final int HOVER_BOX_WIDTH = 200;
    private static final int HOVER_BOX_HEIGHT = 20;
    private static final int NAME_TEXTURE_Y = 166;
    private static final int DESCRIPTION_TEXTURE_Y = NAME_TEXTURE_Y + HOVER_BOX_HEIGHT;
    private static final int OUTER_WIDTH = 2;

    private void drawAbilityHover(Ability ability, AbilityWidget abilityWidget) // TODO: CLEANUP
    {
        GuiUtil.bindTexture(GuiUtil.ABILITIES);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 20.0f);

        String name = ability.name;
        List<String> description = GuiUtil.breakUpTooltipText(font, ability.description, 100);

        int maxWidth = font.getStringWidth(name) + ABILITY_SIZE + HOVER_PADDING - 1;
        for (String str : description)
        {
            int width = font.getStringWidth(str);
            if (width > maxWidth) maxWidth = width;
        }
        maxWidth += HOVER_PADDING * 2;

        int startX = abilityWidget.x - 4;
        int endX = startX + maxWidth;

        int nameY = abilityWidget.y + 2;
        int descY = nameY + 23;

        new Widget(font, startX, descY - DESCRIPTION_START_OFFSET_Y, maxWidth, DESCRIPTION_START_OFFSET_Y, 0, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH).render(0, 0);
        new Widget(font, endX, descY - DESCRIPTION_START_OFFSET_Y, OUTER_WIDTH, DESCRIPTION_START_OFFSET_Y, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH).render(0, 0);
        int gap = 10;
        int i = 0;
        for (String str : description)
        {
            GuiUtil.bindTexture(GuiUtil.ABILITIES);
            Widget lineWidget = new Widget(font, startX, descY + i * gap, maxWidth, gap, 0, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH);
            lineWidget.render(0, 0);
            new Widget(font, endX, descY + i * gap, OUTER_WIDTH, gap, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + OUTER_WIDTH).render(0, 0);
            lineWidget.renderText(str, -font.getStringWidth(str) - HOVER_PADDING, 0, true, 0xffffffff);
            i++;
        }
        GuiUtil.bindTexture(GuiUtil.ABILITIES);
        new Widget(font, startX, descY + i * gap - 1, maxWidth, OUTER_WIDTH + 1, 0, DESCRIPTION_TEXTURE_Y + (HOVER_BOX_HEIGHT - OUTER_WIDTH - 1)).render(0, 0);
        new Widget(font, endX, descY + i * gap - 1, OUTER_WIDTH, OUTER_WIDTH + 1, HOVER_BOX_WIDTH - OUTER_WIDTH, DESCRIPTION_TEXTURE_Y + (HOVER_BOX_HEIGHT - OUTER_WIDTH - 1)).render(0, 0);

        Widget nameWidget = new Widget(font, startX, nameY, maxWidth, HOVER_BOX_HEIGHT, 0, NAME_TEXTURE_Y);
        Widget nameWidgetEnd = new Widget(font, endX, nameY, OUTER_WIDTH, HOVER_BOX_HEIGHT, HOVER_BOX_WIDTH - OUTER_WIDTH, NAME_TEXTURE_Y);

        nameWidget.render(0, 0);
        nameWidgetEnd.render(0, 0);
        nameWidget.renderText(name, -font.getStringWidth(name) - (ABILITY_SIZE + HOVER_PADDING * 2 - 2), 6, true, 0xffffffff);

        GlStateManager.popMatrix();
        GuiUtil.bindTexture(GuiUtil.ABILITIES_WIDGETS);
    }

    private void drawBackground()
    {
        GuiUtil.bindTexture(GuiUtil.MINING_BACKGROUND);

        int tileTextureX = 0;
        int tileTextureY = 0;

        for (int i = -1; i < tilesX + 1; i++)
        {
            for (int j = -1; j < tilesY + 1; j++)
            {
                int x = left + i * TILE_SIZE + (TILE_SIZE + (moveX % TILE_SIZE)) % TILE_SIZE;
                int y = top + j * TILE_SIZE + (TILE_SIZE + (moveY % TILE_SIZE)) % TILE_SIZE;

                int i1 = i - (int)Math.floor((moveX / (double) TILE_SIZE)) + backgroundOffsetX;
                int j1 = j - (int)Math.floor((moveY / (double) TILE_SIZE)) + backgroundOffsetY;
                int rand = new Random(new Random(i1).nextInt() * new Random(j1).nextInt()).nextInt((256 / TILE_SIZE) * (256 / TILE_SIZE));

                tileTextureX = (rand % TILE_SIZE) * TILE_SIZE;
                tileTextureY = (rand / TILE_SIZE) * TILE_SIZE;

                int dx = 0;
                int dy = 0;
                int sx = 0;
                int sy = 0;
                int tx = 0;
                int ty = 0;

                if (x < left)
                {
                    dx = left - x;
                    sx = dx;
                    tx = dx;
                }
                else if (x + TILE_SIZE >= right)
                {
                    tx = x + TILE_SIZE - right;
                }

                if (y < top)
                {
                    dy = top - y;
                    sy = dy;
                    ty = dy;
                }
                else if (y + TILE_SIZE >= bottom)
                {
                    ty = y + TILE_SIZE - bottom;
                }

                if (Math.abs(tx) <= TILE_SIZE && Math.abs(ty) <= TILE_SIZE)
                {
                    blit(x + dx, y + dy, tileTextureX + sx, tileTextureY + sy, TILE_SIZE - tx, TILE_SIZE - ty);
                }
            }
        }

//        fill(left, top - 1, right, top - 2, 0xffffffff);
//        fill(left - 2, top, left - 1, bottom, 0xffffffff);
//        fill(right + 2, top, right + 1, bottom, 0xffffffff);
//        fill(left, bottom + 1, right, bottom + 2, 0xffffffff);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (windowWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            moving = true;
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        int x = left + moveX;
        int y = top + moveY;

        if (windowWidget.isMouseOver((int) mouseX, (int) mouseY))
        {
            for (Ability ability : abilityGroup.getAbilities())
            {
                AbilityWidget abilityWidget = new AbilityWidget(font, ability.x + x, ability.y + y, ABILITY_SIZE, ABILITY_SIZE, ability.type.textureX * ABILITY_SIZE, 0);

                if (abilityWidget.isMouseOver((int) mouseX, (int) mouseY))
                {
                    abilityGroup.setState(ability, AbilityState.UNLOCKED);
                }
            }
        }

        moving = false;

        return false;
    }
}
