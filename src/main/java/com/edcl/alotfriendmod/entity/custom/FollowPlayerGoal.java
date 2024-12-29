package com.edcl.alotfriendmod.entity.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class FollowPlayerGoal extends Goal {
    private final FriendEntity friend;
    private Player targetPlayer; // 目標玩家
    private final double followSpeed;
    private final double maxFollowRange;
    private final double minFollowRange;

    public FollowPlayerGoal(FriendEntity friend) {
        this.friend = friend;
        this.followSpeed = FriendEntity.MOVEMENT_SPEED;
        this.maxFollowRange = 3f;
        this.minFollowRange = 1f;
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
        boolean targetExist = targetPlayer != null;
        boolean targetAlive = targetPlayer.isAlive();
        boolean targetNotSoFar = targetPlayer.distanceToSqr(friend) <= maxFollowRange * maxFollowRange;   // 當玩家仍然在範圍內時繼續
        boolean targetNotSoClose = targetPlayer.distanceToSqr(friend) >= minFollowRange * minFollowRange;
        return targetExist && targetAlive && targetNotSoClose && targetNotSoFar;
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
