package me.Thelnfamous1.gcghosttracker.duck;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public interface GCGhost {
    static void handleGhostModeUpdate(ServerPlayer serverPlayer) {
        GCGhost ghost = (GCGhost) serverPlayer;
        if(ghost.gcghosttracker$hasGhostModeChanged()){
            // give spectator abilities to ghost players, or remove it for non-ghost players
            if(ghost.gcghosttracker$isGhostMode()){
                GameType.SPECTATOR.updatePlayerAbilities(serverPlayer.getAbilities());
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            } else{
                serverPlayer.gameMode.getGameModeForPlayer().updatePlayerAbilities(serverPlayer.getAbilities());
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            }
            ghost.gcghosttracker$setGhostModeChangeHandled();
        }
    }

    static boolean isActualSpectator(boolean isSpectator, Player player) {
        if(isSpectator && ((GCGhost) player).gcghosttracker$isGhostMode()){
            return false;
        } else{
            return isSpectator;
        }
    }

    void gcghosttracker$setGhostMode(boolean enable);

    boolean gcghosttracker$isGhostMode();

    boolean gcghosttracker$hasGhostModeChanged();
    
    void gcghosttracker$setGhostModeChangeHandled();
}
