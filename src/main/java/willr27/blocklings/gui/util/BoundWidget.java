package willr27.blocklings.gui.util;

import net.minecraft.client.gui.FontRenderer;

public class BoundWidget extends Widget
{
    public BoundWidget(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
    {
        super(font, x, y, width, height, textureX, textureY);
    }

    public void render(int mouseX, int mouseY, int leftBound, int rightBound, int topBound, int bottomBound)
    {
        int dx = 0;
        int dy = 0;
        int sx = 0;
        int sy = 0;
        int tx = 0;
        int ty = 0;

        if (x < leftBound)
        {
            dx = leftBound - x;
            sx = dx;
            tx = dx;
        }
        else if (x + width >= rightBound)
        {
            tx = x + width - rightBound;
        }

        if (y < topBound)
        {
            dy = topBound - y;
            sy = dy;
            ty = dy;
        }
        else if (y + height >= bottomBound)
        {
            ty = y + height - bottomBound;
        }

        if (Math.abs(tx) <= width && Math.abs(ty) <= height)
        {
            blit(x + dx, y + dy, textureX + sx, textureY + sy, width - tx, height - ty);
        }
    }
}
