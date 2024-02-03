package me.Thelnfamous1.gcghosttracker.mixin;

import me.Thelnfamous1.gcghosttracker.GCGTNetwork;
import me.Thelnfamous1.gcghosttracker.GhostSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin{
    @Shadow public abstract boolean setGameMode(GameType pGameMode);

    @Shadow public abstract boolean isSpectator();

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void gcghosttracker$onGhostModeChanged() {
        this.setGameMode(this.gcghosttracker$isGhostMode() ? GameType.SPECTATOR : this.getServer().getDefaultGameType());
        GCGTNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new GhostSyncPacket((Player) (Object)this, this.gcghosttracker$isGhostMode()));
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void handleAttack(Entity pTargetEntity, CallbackInfo ci){
        if(this.isSpectator() && this.gcghosttracker$isGhostMode()){
            ci.cancel();
        }
    }
}
