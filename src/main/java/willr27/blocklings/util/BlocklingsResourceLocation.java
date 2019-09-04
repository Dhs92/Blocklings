package willr27.blocklings.util;

import net.minecraft.util.ResourceLocation;
import willr27.blocklings.Blocklings;

public class BlocklingsResourceLocation extends ResourceLocation
{
    public BlocklingsResourceLocation(String path)
    {
        super(Blocklings.MODID, path);
    }
}
