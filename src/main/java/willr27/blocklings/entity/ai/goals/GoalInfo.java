package willr27.blocklings.entity.ai.goals;

import net.minecraft.entity.ai.goal.Goal;
import willr27.blocklings.entity.ai.AIManager;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.GoalActiveMessage;
import willr27.blocklings.network.messages.GoalPriorityMessage;
import willr27.blocklings.whitelist.BlocklingWhitelist;

import java.util.HashMap;
import java.util.Map;

public class GoalInfo
{
    private final AIManager aiManager;
    public final int goalId;
    public final Goal goal;
    public final String name;
    public final int iconX;
    public final int iconY;
    private int priority;
    private boolean unlocked;
    private boolean active;
    private Map<Integer, BlocklingWhitelist> whitelists = new HashMap<>();

    public GoalInfo(AIManager aiManager, int goalId, String name, Goal goal, int priority, int iconX, int iconY)
    {
        this.aiManager = aiManager;
        this.goalId = goalId;
        this.name = name;
        this.goal = goal;
        this.priority = priority;
        this.unlocked = true;
        this.active = false;
        this.iconX = iconX;
        this.iconY = iconY;
    }

    public int getPriority() { return priority; }
    public void setPriority(int value) { setPriority(value, true, true); }
    public void setPriority(int value, boolean sync) { setPriority(value, true, sync); }
    public void setPriority(int value, boolean updateManager, boolean sync) { int oldPriority = priority; if (updateManager) aiManager.updateGoalsOrder(this, oldPriority, value); priority = value; aiManager.reorderGoals(); aiManager.reapplyGoals(); if (sync) NetworkHandler.sync(aiManager.blockling.world, new GoalPriorityMessage(goalId, priority, aiManager.blockling.getEntityId())); }

    public boolean isUnlocked() { return unlocked; }
    public void toggleUnlocked() { setUnlocked(!unlocked); }
    public void setUnlocked(boolean value) { unlocked = value; }

    public boolean isActive() { return active; }
    public void toggleActive() { setActive(!active, true); }
    public void setActive(boolean value, boolean sync) { active = value; aiManager.reapplyGoals(); if (sync) NetworkHandler.sync(aiManager.blockling.world, new GoalActiveMessage(goalId, active, aiManager.blockling.getEntityId())); }

    public Map<Integer, BlocklingWhitelist> getWhitelists() { return whitelists; }
    public void addWhitelist(int id, BlocklingWhitelist whitelist) { whitelists.put(id, whitelist); }

    public boolean hasWhitelist() { return !whitelists.isEmpty(); }
    public BlocklingWhitelist getWhitelist(int id) { return whitelists.get(id); }
}
