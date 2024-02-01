package me.Thelnfamous1.gcghosttracker.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin{

    @Shadow public abstract void setCamera(@Nullable Entity pEntityToSpectate);

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void handleIsSpectator(CallbackInfoReturnable<Boolean> cir){
        if(this.gcghosttracker$isGhostMode()){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void handleAttack(Entity pTargetEntity, CallbackInfo ci){
        if(this.gcghosttracker$isGhostMode()){
            ci.cancel();
            this.setCamera(pTargetEntity);
        }
    }
}
