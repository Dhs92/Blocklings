package willr27.blocklings.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.util.BlocklingsResourceLocation;

public class GuiUtil
{
    public static final ResourceLocation TABS = new BlocklingsResourceLocation("textures/gui/tabs.png");
    public static final ResourceLocation STATS = new BlocklingsResourceLocation("textures/gui/stats.png");
    public static final ResourceLocation TASKS = new BlocklingsResourceLocation("textures/gui/tasks.png");
    public static final ResourceLocation EQUIPMENT = new BlocklingsResourceLocation("textures/gui/equipment.png");
    public static final ResourceLocation INVENTORY = new BlocklingsResourceLocation("textures/gui/inventory.png");
    public static final ResourceLocation ABILITIES = new BlocklingsResourceLocation("textures/gui/abilities.png");

    public static void bindTexture(ResourceLocation texture)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int left, int top, int width, int height)
    {
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY <= top + height;
    }
}
