package willr27.blocklings.gui.screens;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.entity.BlocklingEntity;
import willr27.blocklings.gui.container.containers.InventoryContainer;
import willr27.blocklings.gui.util.GuiUtil;

public class InventoryScreen extends ContainerScreen<InventoryContainer>
{
    private TabbedScreen tabbedScreen;

    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

    public InventoryScreen(InventoryContainer screenContainer, BlocklingEntity blockling, PlayerEntity player)
    {
        super(screenContainer, null, new StringTextComponent("Inventory"));
        this.blockling = blockling;
        this.player = player;
    }

    @Override
    protected void init()
    {
        xSize = TabbedScreen.UI_WIDTH;
        ySize = TabbedScreen.UI_HEIGHT;

        centerX = width / 2;
        centerY = height / 2 + TabbedScreen.OFFSET_Y;

        left = centerX - TabbedScreen.UI_WIDTH / 2;
        top = centerY - TabbedScreen.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedScreen.CONTENT_WIDTH / 2;
        contentTop = top;

        tabbedScreen = new TabbedScreen(blockling, player, centerX, centerY);

        super.init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.INVENTORY);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);

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
        tabbedScreen.mouseReleased((int)mouseX, (int)mouseY, state);

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        return super.mouseReleased(mouseX, mouseY, state);
    }
}
