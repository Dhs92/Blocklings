package willr27.blocklings.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.AbilityWidget;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.gui.util.Widget;

import java.util.Random;

public class AbilitiesGui extends AbstractGui
{
    private static final int TILE_SIZE = 16;

    private BlocklingEntity blockling;
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

    public AbilitiesGui(BlocklingEntity blockling, FontRenderer font, int width, int height, int centerX, int centerY)
    {
        this.blockling = blockling;
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

        int w = 24;
        int h = 24;
        int x = left + moveX;
        int y = top + moveY;

        AbilityWidget ability = new AbilityWidget(font, x + 100, y + 100, w, h, 0, 0);
        AbilityWidget ability2 = new AbilityWidget(font, x + 190, y + 150, w, h, 24, 0);

        ability2.connect(ability, 14, 0xff000000, left, right, top, bottom);
        ability2.connect(ability, 8, 0xffFFD800, left, right, top, bottom);

        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        ability.render(mouseX, mouseY, left, right, top, bottom);
        ability2.render(mouseX, mouseY, left, right, top, bottom);
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

        fill(left, top - 1, right, top - 2, 0xffffffff);
        fill(left - 2, top, left - 1, bottom, 0xffffffff);
        fill(right + 2, top, right + 1, bottom, 0xffffffff);
        fill(left, bottom + 1, right, bottom + 2, 0xffffffff);
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
        moving = false;

        return false;
    }
}
