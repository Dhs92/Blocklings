package willr27.blocklings.gui.screens.utilities;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.screens.TabbedScreen;

public class UtilityScreen extends ContainerScreen<Container>
{
    private TabbedScreen tabbedScreen;

    private final BlocklingEntity blockling;
    private final PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

    private UtilityGui utilityGui;

    public UtilityScreen(Container screenContainer, BlocklingEntity blockling, PlayerEntity player)
    {
        super(screenContainer, null, new StringTextComponent("Inventory"));
        this.blockling = blockling;
        this.player = player;
    }

    @Override
    protected void init()
    {
        xSize = TabbedScreen.CONTENT_WIDTH;
        ySize = TabbedScreen.CONTENT_HEIGHT;

        centerX = width / 2;
        centerY = height / 2 + TabbedScreen.OFFSET_Y;

        left = centerX - TabbedScreen.UI_WIDTH / 2;
        top = centerY - TabbedScreen.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedScreen.CONTENT_WIDTH / 2;
        contentTop = top;

        tabbedScreen = new TabbedScreen(blockling, player, centerX, centerY);

        switch (blockling.getUtilityManager().getUtility(blockling.getGuiInfo().utility))
        {
            case CHEST: utilityGui = new ChestGui(blockling, player); break;
            case CRAFTING_TABLE: utilityGui = new CraftingTableGui(blockling, player); break;
            case FURNACE: utilityGui = new FurnaceGui(blockling, player); break;
        }

        utilityGui.init(font, width, height);

        super.init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        utilityGui.render(mouseX, mouseY, partialTicks);

        tabbedScreen.drawTabs();

        super.render(mouseX, mouseY, partialTicks);
        tabbedScreen.drawTooltip(mouseX, mouseY, this);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        super.mouseClicked(mouseX, mouseY, state);
        return utilityGui.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        return tabbedScreen.mouseReleased((int)mouseX, (int)mouseY, state) || utilityGui.mouseReleased(mouseX, mouseY, state);
    }
}
