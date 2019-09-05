package willr27.blocklings.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.util.GuiUtil;
import willr27.blocklings.whitelist.BlocklingWhitelist;
import willr27.blocklings.whitelist.WhitelistType;

public class WhitelistScreen extends Screen
{
    private BlocklingEntity blockling;
    private PlayerEntity player;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

    private BlocklingWhitelist whitelist;

    public WhitelistScreen(BlocklingEntity blockling, PlayerEntity player)
    {
        super(new StringTextComponent("Whitelist"));
        this.blockling = blockling;
        this.player = player;
    }

    @Override
    protected void init()
    {
        centerX = width / 2;
        centerY = height / 2 + TabbedScreen.OFFSET_Y;

        left = centerX - TabbedScreen.UI_WIDTH / 2;
        top = centerY - TabbedScreen.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedScreen.CONTENT_WIDTH / 2;
        contentTop = top;

        whitelist = blockling.aiManager.getWhitelist(blockling.getGuiInfo().currentlySelectedGoalId, 0);
        maxPages = (int) Math.ceil(whitelist.size() / (float) ENTRIES_PER_PAGE);

        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.WHITELIST);
        blit(contentLeft, contentTop, 0, 0, TabbedScreen.CONTENT_WIDTH, TabbedScreen.CONTENT_HEIGHT);

        font.drawString(blockling.aiManager.getGoalFromId(blockling.getGuiInfo().currentlySelectedGoalId).name, contentLeft + 8, contentTop + 6, 0xffffff);

        drawButtons(mouseX, mouseY);
        drawScroll(mouseX, mouseY);
        drawEntries(mouseX, mouseY);
        drawTooltips(mouseX, mouseY);

        super.render(mouseX, mouseY, partialTicks);
    }

    private static final int ENTRIES_PER_PAGE = 16;

    private static final int ENTRY_BUTTON_TEXTURE_Y = 166;
    private static final int ENTRY_BUTTON_SIZE = 30;
    private static final int ENTRY_BUTTON_GAP = 4;
    private static final int ENTRY_BUTTON_START_X = 13;
    private static final int ENTRY_BUTTON_START_Y = 21;

    private static final int SCROLL_WIDTH = 12;
    private static final int SCROLL_HEIGHT = 15;
    private static final int SCROLL_TEXTURE_Y = 196;
    private static final int SCROLL_X = 155;
    private static final int SCROLL_START_Y = 17;
    private static final int SCROLL_LENGTH = 141 - SCROLL_HEIGHT;

    private static final int ON_OFF_BUTTON_SIZE = 11;
    private static final int ON_BUTTON_X = 108;
    private static final int ON_BUTTON_Y = 4;
    private static final int ON_BUTTON_TEXTURE_Y = 211;
    private static final int OFF_BUTTON_X = 119;
    private static final int OFF_BUTTON_Y = 4;
    private static final int OFF_BUTTON_TEXTURE_Y = 222;

    private static final int SWAP_BUTTON_WIDTH = 12;
    private static final int SWAP_BUTTON_HEIGHT = 11;
    private static final int SWAP_BUTTON_X = 133;
    private static final int SWAP_BUTTON_Y = 4;
    private static final int SWAP_BUTTON_TEXTURE_Y = 233;

    private int index;
    private int page;
    private int maxPages;
    private int maxForPage;
    private float scroll;
    private boolean mouseDown;
    private boolean scrollPressed;
    private boolean onPressed;
    private boolean offPressed;
    private boolean swapPressed;

    private void drawButtons(int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.WHITELIST);

        int onTextureX = onPressed ? ON_OFF_BUTTON_SIZE : 0;
        blit(getOnX(), getOnY(), onTextureX, ON_BUTTON_TEXTURE_Y, ON_OFF_BUTTON_SIZE, ON_OFF_BUTTON_SIZE);

        int offTextureX = offPressed ? ON_OFF_BUTTON_SIZE : 0;
        blit(getOffX(), getOffY(), offTextureX, OFF_BUTTON_TEXTURE_Y, ON_OFF_BUTTON_SIZE, ON_OFF_BUTTON_SIZE);

        int swapTextureX = swapPressed ? SWAP_BUTTON_WIDTH : 0;
        blit(getSwapX(), getSwapY(), swapTextureX, SWAP_BUTTON_TEXTURE_Y, SWAP_BUTTON_WIDTH, SWAP_BUTTON_HEIGHT);
    }

    private int getOnX()
    {
        return contentLeft + ON_BUTTON_X;
    }
    private int getOnY()
    {
        return contentTop + ON_BUTTON_Y;
    }
    private int getOffX()
    {
        return contentLeft + OFF_BUTTON_X;
    }
    private int getOffY()
    {
        return contentTop + OFF_BUTTON_Y;
    }
    private int getSwapX()
    {
        return contentLeft + SWAP_BUTTON_X;
    }
    private int getSwapY()
    {
        return contentTop + SWAP_BUTTON_Y;
    }

    private void drawTooltips(int mouseX, int mouseY)
    {
        int i = getHoveredEntry(mouseX, mouseY);
        if (i != -1)
        {
            ResourceLocation entry = (ResourceLocation) whitelist.keySet().toArray()[i];

            if (whitelist.type == WhitelistType.BLOCK)
            {
                Block block = Registry.BLOCK.getOrDefault(entry);
                renderTooltip(block.getNameTextComponent().getString(), mouseX, mouseY);
            }
        }
    }

    private void drawScroll(int mouseX, int mouseY)
    {
        if (scrollPressed)
        {
            int dy = mouseY - contentTop - SCROLL_START_Y - SCROLL_HEIGHT / 2;
            dy = Math.min(dy, SCROLL_LENGTH);
            dy = Math.max(dy, 0);
            float percent = (float) dy / (float) SCROLL_LENGTH;
            page = (int)((percent * whitelist.size()) / ENTRIES_PER_PAGE);
            scroll = dy / (float) SCROLL_LENGTH;
        }

        page = Math.max(page, 0);
        page = Math.min(page, maxPages - 1);
        index = page * ENTRIES_PER_PAGE;
        maxForPage = Math.min(ENTRIES_PER_PAGE, whitelist.size() - page * ENTRIES_PER_PAGE);
        //scroll = ((page - 1) * ENTRIES_PER_PAGE + maxForPage) / (float) (whitelist.size() - ENTRIES_PER_PAGE);
        if (!scrollPressed) scroll = page / (float) (maxPages - 1);

        int scrollTextureX = scrollPressed ? SCROLL_WIDTH : 0;
        blit(getScrollX(), getScrollY(), scrollTextureX, SCROLL_TEXTURE_Y, SCROLL_WIDTH, SCROLL_HEIGHT);
    }

    private int getScrollX()
    {
        return contentLeft + SCROLL_X;
    }
    private int getScrollY()
    {
        return contentTop + SCROLL_START_Y + ((int) (SCROLL_LENGTH * scroll));
    }

    private void drawEntries(int mouseX, int mouseY)
    {
        for (int i = 0; i < maxForPage; i++)
        {
            ResourceLocation entry = (ResourceLocation) whitelist.keySet().toArray()[i + index];
            boolean isInWhitelist = whitelist.isInWhitelist(entry);

            int buttonX = getButtonX(i);
            int buttonY = getButtonY(i);

            GlStateManager.color3f(1.0f, 1.0f, 1.0f);
            GuiUtil.bindTexture(GuiUtil.WHITELIST);
            int entryButtonTextureX = isInWhitelist ? 0 : ENTRY_BUTTON_SIZE;
            blit(getButtonX(i), getButtonY(i), entryButtonTextureX, ENTRY_BUTTON_TEXTURE_Y, ENTRY_BUTTON_SIZE, ENTRY_BUTTON_SIZE);

            if (whitelist.type == WhitelistType.BLOCK)
            {
                Block block = Registry.BLOCK.getOrDefault(entry);
                ItemStack stack = new ItemStack(block);
                drawItemStack(stack, getBlockX(i), getBlockY(i), i);
            }

            if (!isInWhitelist)
            {
                GlStateManager.translatef(0.0F, 0.0F, 200.0F);
                fill(buttonX, buttonY, buttonX + ENTRY_BUTTON_SIZE, buttonY + ENTRY_BUTTON_SIZE, 0x55000000);
                GlStateManager.translatef(0.0F, 0.0F, -200.0F);
            }
        }
    }

    private int getHoveredEntry(int mouseX, int mouseY)
    {
        for (int i = 0; i < maxForPage; i++)
        {
            if (GuiUtil.isMouseOver(mouseX, mouseY, getButtonX(i), getButtonY(i), ENTRY_BUTTON_SIZE, ENTRY_BUTTON_SIZE))
            {
                return i + index;
            }
        }
        return -1;
    }

    private int getBlockX(int i)
    {
        return getButtonX(i) + 6;
    }
    private int getBlockY(int i)
    {
        return getButtonY(i)+ 6;
    }

    private int getButtonX(int i)
    {
        return contentLeft + ENTRY_BUTTON_START_X + ((ENTRY_BUTTON_SIZE + ENTRY_BUTTON_GAP) * (i % 4));
    }
    private int getButtonY(int i)
    {
        return top + ENTRY_BUTTON_START_Y + ((ENTRY_BUTTON_SIZE + ENTRY_BUTTON_GAP) * (i / 4));
    }

    private void drawItemStack(ItemStack stack, int x, int y, int i)
    {
        RenderHelper.enableGUIStandardItemLighting();
        if (i % 4 == 1) GlStateManager.translatef(0.2F, 0.0F, 0.0F);
        else if (i % 4 == 2) GlStateManager.translatef(-0.23F, 0.0F, 0.0F);
        float scale = 1.2f;
        GlStateManager.scalef(scale, scale, scale);
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, (int)(x / scale), (int)(y / scale));
        if (i % 4 == 1) GlStateManager.translatef(-0.2F, 0.0F, 0.0F);
        else if (i % 4 == 2) GlStateManager.translatef(0.23F, 0.0F, 0.0F);
        GlStateManager.scalef(1 / scale, 1 / scale, 1 / scale);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getScrollX(), contentTop + SCROLL_START_Y, SCROLL_WIDTH, SCROLL_HEIGHT + SCROLL_LENGTH))
        {
            scrollPressed = true;
            int dy = (int) mouseY - contentTop - SCROLL_START_Y - SCROLL_HEIGHT / 2;
            dy = Math.min(dy, SCROLL_LENGTH);
            dy = Math.max(dy, 0);
            float percent = (float) dy / (float) SCROLL_LENGTH;
            page = (int)((percent * whitelist.size()) / ENTRIES_PER_PAGE);
            scroll = dy / (float) SCROLL_LENGTH;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOnX(), getOnY(), ON_OFF_BUTTON_SIZE, ON_OFF_BUTTON_SIZE))
        {
            onPressed = true;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOffX(), getOffY(), ON_OFF_BUTTON_SIZE, ON_OFF_BUTTON_SIZE))
        {
            offPressed = true;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getSwapX(), getSwapY(), SWAP_BUTTON_WIDTH, SWAP_BUTTON_HEIGHT))
        {
            swapPressed = true;
        }

        mouseDown = true;

        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        int i = getHoveredEntry((int) mouseX, (int) mouseY);
        if (i != -1 && !scrollPressed && !onPressed && !offPressed && !swapPressed)
        {
            ResourceLocation entry = (ResourceLocation) whitelist.keySet().toArray()[i];
            whitelist.toggleEntry(entry);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOnX(), getOnY(), ON_OFF_BUTTON_SIZE, ON_OFF_BUTTON_SIZE))
        {
            whitelist.setAll(true);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOffX(), getOffY(), ON_OFF_BUTTON_SIZE, ON_OFF_BUTTON_SIZE))
        {
            whitelist.setAll(false);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getSwapX(), getSwapY(), SWAP_BUTTON_WIDTH, SWAP_BUTTON_HEIGHT))
        {
            whitelist.toggleAll();
        }
//        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getScrollX(), contentTop + SCROLL_START_Y, SCROLL_WIDTH, SCROLL_HEIGHT + SCROLL_LENGTH))
//        {
//            int dy = (int) mouseY - contentTop - SCROLL_START_Y - SCROLL_HEIGHT / 2;
//            dy = Math.min(dy, SCROLL_LENGTH);
//            dy = Math.max(dy, 0);
//            float percent = (float) dy / (float) SCROLL_LENGTH;
//            page = (int)((percent * whitelist.size()) / ENTRIES_PER_PAGE);
//            scroll = dy / (float) SCROLL_LENGTH;
//        }

        mouseDown = false;
        scrollPressed = false;
        onPressed = false;
        offPressed = false;
        swapPressed = false;

        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (keyCode == 256 && this.shouldCloseOnEsc())
        {
            blockling.openCurrentGui(player);
            return true;
        }

        return super.keyPressed(keyCode, i, j);
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_)
    {
        page -= p_mouseScrolled_5_;

        return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
