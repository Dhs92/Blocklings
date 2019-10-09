package willr27.blocklings.gui.screens.utilities;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.screens.TabbedScreen;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.inventory.Utilities.CraftingTableInventory;

public class CraftingTableGui extends UtilityGui
{
    private final CraftingTableInventory craftingTableInventory;

    public CraftingTableGui(BlocklingEntity blockling, PlayerEntity player)
    {
        super(blockling, player);
        this.craftingTableInventory = (CraftingTableInventory) blockling.getUtilityManager().getInventory(blockling.getGuiInfo().utility);
    }

    @Override
    protected void init(FontRenderer font, int width, int height)
    {
        super.init(font, width, height);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.CRAFTING_TABLE);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);
    }
}
