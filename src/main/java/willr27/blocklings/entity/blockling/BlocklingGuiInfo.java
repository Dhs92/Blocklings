package willr27.blocklings.entity.blockling;

import net.minecraft.network.PacketBuffer;

public class BlocklingGuiInfo
{
    public final int currentlyOpenGuiId;
    public final int mostRecentTabbedGuiId;
    public final int currentlySelectedGoalId;
    public final int abilityGroupId;
    public final int utility;

    public BlocklingGuiInfo(int currentlyOpenGuiId, int mostRecentTabbedGuiId, int currentlySelectedGoalId, int abilityGroupId, int utility)
    {
        this.currentlyOpenGuiId = currentlyOpenGuiId;
        this.mostRecentTabbedGuiId = mostRecentTabbedGuiId;
        this.currentlySelectedGoalId = currentlySelectedGoalId;
        this.abilityGroupId = abilityGroupId;
        this.utility = utility;
    }

    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeInt(currentlyOpenGuiId);
        buf.writeInt(mostRecentTabbedGuiId);
        buf.writeInt(currentlySelectedGoalId);
        buf.writeInt(abilityGroupId);
        buf.writeInt(utility);
    }

    public static BlocklingGuiInfo readFromBuf(PacketBuffer buf)
    {
        return new BlocklingGuiInfo(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }
}
