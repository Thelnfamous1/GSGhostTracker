package me.Thelnfamous1.gcghosttracker.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends PlayerMixin{
    protected AbstractClientPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void handleIsSpectator(CallbackInfoReturnable<Boolean> cir){
        if(this.gcghosttracker$isGhostMode()){
            cir.setReturnValue(true);
        }
    }
}
