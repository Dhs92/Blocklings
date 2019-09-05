package willr27.blocklings.whitelist;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.Map;

public class BlocklingWhitelist extends Whitelist<ResourceLocation>
{
    private BlocklingEntity blockling;

    public BlocklingWhitelist(BlocklingEntity blockling)
    {
        this.blockling = blockling;
    }

    public BlocklingWhitelist(BlocklingEntity blockling, Map whitelist)
    {
        this(blockling);
        clear();
        putAll(whitelist);
    }

    public boolean isWhitelisted(Block block)
    {
        Boolean result = get(block.getRegistryName());
        return result != null ? result : false;
    }

    public boolean isBlacklisted(Block block)
    {
        return !isWhitelisted(block);
    }

    @Override
    public void setEntry(ResourceLocation entry, boolean value)
    {
        setEntry(entry, value, true);
    }

    public void setEntry(ResourceLocation entry, boolean value, boolean sync)
    {
        super.setEntry(entry, value);
    }

    @Override
    public void toggleEntry(ResourceLocation entry)
    {
        toggleEntry(entry, true);
    }

    public void toggleEntry(ResourceLocation entry, boolean sync)
    {
        setEntry(entry, !get(entry), sync);
    }

    @Override
    public void setAll(boolean value)
    {
        setAll(value, true);
    }

    public void setAll(boolean value, boolean sync)
    {
        for (ResourceLocation entry : keySet())
        {
            setEntry(entry, value, false);
        }
    }

    @Override
    public void toggleAll()
    {
        toggleAll(true);
    }

    public void toggleAll(boolean sync)
    {
        for (ResourceLocation entry : keySet())
        {
            toggleEntry(entry, false);
        }
    }
}
