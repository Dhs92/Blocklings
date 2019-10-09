package willr27.blocklings.gui.screens.utilities;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.screens.TabbedScreen;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.gui.util.widgets.PorgressWidget;
import willr27.blocklings.inventory.Utilities.FurnaceInventory;

public class FurnaceGui extends UtilityGui
{
    private final FurnaceInventory furnaceInventory;

    private PorgressWidget flames;
    private PorgressWidget arrow;

    public FurnaceGui(BlocklingEntity blockling, PlayerEntity player)
    {
        super(blockling, player);
        this.furnaceInventory = (FurnaceInventory) blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility);
    }

    @Override
    protected void init(FontRenderer font, int width, int height)
    {
        super.init(font, width, height);

        flames = new PorgressWidget(font, centerX - 20, centerY - 40, 14, 14, 176, 0);
        arrow = new PorgressWidget(font, centerX + 40, centerY - 40, 24, 17, 176, 14);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.FURNACE);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);

        flames = new PorgressWidget(font, centerX - 31, centerY - 46, 14, 14, 176, 0);
        arrow = new PorgressWidget(font, centerX - 9, centerY - 49, 24, 17, 176, 14);
        if (furnaceInventory.isBurning()) flames.render(mouseX, mouseY, furnaceInventory.getBurnProgress(), true, true);
        arrow.render(mouseX, mouseY, furnaceInventory.getSmeltProgress(), false, false);
    }
}
