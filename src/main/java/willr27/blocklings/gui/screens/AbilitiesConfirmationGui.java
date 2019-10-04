package willr27.blocklings.gui.screens;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import willr27.blocklings.abilities.Ability;
import willr27.blocklings.abilities.AbilityGroup;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.List;

public class AbilitiesConfirmationGui extends AbstractGui
{
    private FontRenderer font;
    private List<String> message;
    private int windowWidth;
    private int windowHeight;
    private int areaWidth;
    private int areaHeight;
    public boolean closed;
    public boolean yes;
    private Button yesButton;
    private Button noButton; // TODO: TIDY CLASS

    public AbilitiesConfirmationGui()
    {
        closed = true;
    }

    public AbilitiesConfirmationGui(FontRenderer font, BlocklingEntity blockling, Ability ability, AbilityGroup group, List<String> message, int windowWidth, int windowHeight, int areaWidth, int areHeight)
    {
        this.font = font;
        this.message = message;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.areaWidth = areaWidth;
        this.areaHeight = areHeight;
        this.closed = false;

        int width = 50;
        int height = 20;
        int yesX = windowWidth / 2 - 30 - width / 2;
        int noX = windowWidth / 2 + 30 - width / 2;
        int y = windowHeight / 2 + 10;
        yesButton = new Button(yesX, y, width, height, "Yes", press -> { yes = true; closed = true; blockling.abilityManager.tryBuyAbility(group, ability); });
        noButton = new Button(noX, y, width, height, "No", press -> { yes = false; closed = true; });
    }

    public void draw(int mouseX, int mouseY)
    {
        if (!closed)
        {
            fill(windowWidth / 2 - areaWidth / 2, windowHeight / 2 - areaHeight / 2 - 5, windowWidth / 2 + areaWidth / 2, windowHeight / 2 + areaHeight / 2 - 5, 0xbb000000);

            int i = 0;
            for (String str : message)
            {
                drawCenteredString(font, str, windowWidth / 2, windowHeight / 2 + i * 11 - (message.size() * 11) - 5, 0xffffff);
                i++;
            }

            yesButton.render(mouseX, mouseY, 0);
            noButton.render(mouseX, mouseY, 0);
        }
    }

    public boolean keyPressed(int keyCode, int i, int j)
    {
        if (!closed && keyCode == 256)
        {
            if (noButton != null)
            {
                noButton.onPress();
                return true;
            }
        }

        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
//        if (yesButton.isMouseOver(mouseX, mouseY))
//        {
//            yesButton.mouseClicked(mouseX, mouseY, state);
//        }
//        else if (noButton.isMouseOver(mouseX, mouseY))
//        {
//            noButton.mouseClicked(mouseX, mouseY, state);
//        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (yesButton.isMouseOver(mouseX, mouseY))
        {
            yesButton.mouseReleased(mouseX, mouseY, state);
            yesButton.onPress();
            return true;
        }
        else if (noButton.isMouseOver(mouseX, mouseY))
        {
            noButton.mouseReleased(mouseX, mouseY, state);
            noButton.onPress();
            return true;
        }

        return false;
    }
}
