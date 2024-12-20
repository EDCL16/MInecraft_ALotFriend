package com.edcl.alotfriendmod.entity.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FollowPlayerGoal extends Goal {
    private final FriendEntity friend;
    private Player targetPlayer; // 目標玩家
    private final double followSpeed;
    private final double followRange;

    public FollowPlayerGoal(FriendEntity friend, double followSpeed, double followRange) {
        this.friend = friend;
        this.followSpeed = followSpeed;
        this.followRange = followRange;
    }

    @Override
    public boolean canUse() {

        MinecraftServer server = friend.getServer();  // 假設 'friend' 是一個 Entity
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        if (!players.isEmpty()) {
            // 找到最近的玩家
            targetPlayer = players.get(0);
            return true;
        }

        targetPlayer = null;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        // 當玩家仍然在範圍內時繼續
        return targetPlayer != null && targetPlayer.isAlive() &&
                targetPlayer.distanceToSqr(friend) <= followRange * followRange;
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            // 向目標玩家移動
            friend.getNavigation().moveTo(targetPlayer, followSpeed);
        }
    }

    @Override
    public void stop() {
        // 停止移動
        targetPlayer = null;
        friend.getNavigation().stop();
    }
}
