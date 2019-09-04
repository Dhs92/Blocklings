package willr27.blocklings.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public class AiUtil
{
    public static Path getPathTo(MobEntity entity, BlockPos blockPos)
    {
        return entity.getNavigator().getPathToPos(blockPos, 1);
    }

    public static double distanceSqFromTarget(Path path, BlockPos blockPos)
    {
        return blockPos.distanceSq(getFinalBlockPos(path));
    }

    public static BlockPos getFinalBlockPos(Path path)
    {
        PathPoint finalPoint = path.getFinalPathPoint();
        return new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
    }
}
