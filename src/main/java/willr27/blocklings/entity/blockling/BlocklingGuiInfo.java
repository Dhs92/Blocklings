package willr27.blocklings.entity.blockling;

import net.minecraft.network.PacketBuffer;

public class BlocklingGuiInfo
{
    public final int currentlyOpenGuiId;
    public final int mostRecentTabbedGuiId;
    public final int currentlySelectedGoalId;
    public final int abilityGroupId;

    public BlocklingGuiInfo(int currentlyOpenGuiId, int mostRecentTabbedGuiId, int currentlySelectedGoalId, int abilityGroupId)
    {
        this.currentlyOpenGuiId = currentlyOpenGuiId;
        this.mostRecentTabbedGuiId = mostRecentTabbedGuiId;
        this.currentlySelectedGoalId = currentlySelectedGoalId;
        this.abilityGroupId = abilityGroupId;
    }

    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeInt(currentlyOpenGuiId);
        buf.writeInt(mostRecentTabbedGuiId);
        buf.writeInt(currentlySelectedGoalId);
        buf.writeInt(abilityGroupId);
    }

    public static BlocklingGuiInfo readFromBuf(PacketBuffer buf)
    {
        return new BlocklingGuiInfo(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }
}
