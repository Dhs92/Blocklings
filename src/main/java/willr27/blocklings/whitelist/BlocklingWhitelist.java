package willr27.blocklings.whitelist;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.WhitelistAllMessage;
import willr27.blocklings.network.messages.WhitelistSingleMessage;

import java.util.Map;

public class BlocklingWhitelist extends Whitelist<ResourceLocation>
{
    public final WhitelistType type;
    public final int id;
    public final String name;
    private BlocklingEntity blockling;

    public BlocklingWhitelist(int whitelistId, String name, BlocklingEntity blockling, WhitelistType type)
    {
        this.blockling = blockling;
        this.type = type;
        this.name = name;
        this.id = whitelistId;
    }

    public BlocklingWhitelist(int whitelistId, String name, BlocklingEntity blockling, Map whitelist, WhitelistType type)
    {
        this(whitelistId, name, blockling, type);
        clear();
        putAll(whitelist);
    }

    public void setWhitelist(Whitelist<ResourceLocation> whitelist, boolean sync)
    {
        clear();
        putAll(whitelist);
        if (sync) NetworkHandler.sync(blockling.world, new WhitelistAllMessage(this, blockling.getEntityId()));
    }

    public boolean isInWhitelist(Object entry)
    {
        Boolean result = null;
        if (entry instanceof Block) result = get(((Block)entry).getRegistryName());
        else if (entry instanceof Item) result = get(((Item)entry).getRegistryName());
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
        if (sync) NetworkHandler.sync(blockling.world, new WhitelistSingleMessage(id, entry, value, blockling.getEntityId()));
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
        if (sync) NetworkHandler.sync(blockling.world, new WhitelistAllMessage(this, blockling.getEntityId()));
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
        if (sync) NetworkHandler.sync(blockling.world, new WhitelistAllMessage(this, blockling.getEntityId()));
    }
}
