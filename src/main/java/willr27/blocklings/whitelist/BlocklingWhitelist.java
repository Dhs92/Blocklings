package willr27.blocklings.whitelist;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.entity.blockling.BlocklingEntity;

import java.util.Map;

public class BlocklingWhitelist extends Whitelist<ResourceLocation>
{
    public final WhitelistType type;
    private BlocklingEntity blockling;

    public BlocklingWhitelist(BlocklingEntity blockling, WhitelistType type)
    {
        this.blockling = blockling;
        this.type = type;
    }

    public BlocklingWhitelist(BlocklingEntity blockling, Map whitelist, WhitelistType type)
    {
        this(blockling, type);
        clear();
        putAll(whitelist);
    }

    public boolean isInWhitelist(Object entry)
    {
        Boolean result = null;
        if (entry instanceof Block) result = get(((Block)entry).getRegistryName());
        else result = get(entry);

        return result != null ? result : false;
    }

    public boolean isInBlacklist(Object entry)
    {
        return !isInWhitelist(entry);
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
