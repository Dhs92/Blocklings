package willr27.blocklings.gui.screens.utilities;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.screens.TabbedScreen;

public class UtilityGui extends AbstractGui
{
    protected final BlocklingEntity blockling;
    protected final PlayerEntity player;
    protected int centerX, centerY;
    protected int left, top;
    protected int contentLeft, contentTop;

    protected FontRenderer font;

    public UtilityGui(BlocklingEntity blockling, PlayerEntity player)
    {
        this.blockling = blockling;
        this.player = player;
    }

    protected void init(FontRenderer font, int width, int height)
    {
        this.font = font;

        centerX = width / 2;
        centerY = height / 2 + TabbedScreen.OFFSET_Y;

        left = centerX - TabbedScreen.UI_WIDTH / 2;
        top = centerY - TabbedScreen.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedScreen.CONTENT_WIDTH / 2;
        contentTop = top;
    }

    public void render(int mouseX, int mouseY, float partialTicks)
    {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        return false;
    }
}
