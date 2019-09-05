package willr27.blocklings.entity.blockling;

import net.minecraft.network.PacketBuffer;

public class BlocklingGuiInfo
{
    public final int currentlyOpenGuiId;
    public final int mostRecentTabbedGuiId;
    public final int currentlySelectedGoalId;

    public BlocklingGuiInfo(int currentlyOpenGuiId, int mostRecentTabbedGuiId, int currentlySelectedGoalId)
    {
        this.currentlyOpenGuiId = currentlyOpenGuiId;
        this.mostRecentTabbedGuiId = mostRecentTabbedGuiId;
        this.currentlySelectedGoalId = currentlySelectedGoalId;
    }

    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeInt(currentlyOpenGuiId);
        buf.writeInt(mostRecentTabbedGuiId);
        buf.writeInt(currentlySelectedGoalId);
    }

    public static BlocklingGuiInfo readFromBuf(PacketBuffer buf)
    {
        return new BlocklingGuiInfo(buf.readInt(), buf.readInt(), buf.readInt());
    }
}
