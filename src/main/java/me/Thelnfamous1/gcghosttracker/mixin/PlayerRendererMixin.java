package me.Thelnfamous1.gcghosttracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @WrapOperation(method = "setModelProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isSpectator()Z"))
    private boolean handleSetModelProperties(AbstractClientPlayer player, Operation<Boolean> isSpectator){
        boolean spectator = isSpectator.call(player);
        if(spectator && ((GCGhost) player).gcghosttracker$isGhostMode()){
            return false;
        } else{
            return spectator;
        }
    }

}
