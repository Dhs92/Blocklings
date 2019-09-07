package willr27.blocklings.gui.util;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class Icon extends AbstractGui
{
    private FontRenderer font;
    public int x, y;
    public int width, height;
    public int textureX, textureY;

    public Icon(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
    {
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public void render(int mouseX, int mouseY)
    {
        blit(x, y, textureX, textureY, width, height);
    }

    public void renderText(String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -font.getStringWidth(text) - dx : width + dx;
        drawString(font, text, x + bonusX, y + dy, colour);
    }

    public void renderCenteredText(String text, int dx, int dy, boolean right, int colour)
    {
        int bonusX = right ? -dx : width + dx;
        drawCenteredString(font, text, x + bonusX, y + dy, colour);
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return GuiUtil.isMouseOver(mouseX, mouseY, x, y, width, height);
    }
}
