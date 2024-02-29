package me.Thelnfamous1.gcghosttracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @WrapOperation(method = "performUseItemOn", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;localPlayerMode:Lnet/minecraft/world/level/GameType;", ordinal = 0))
    private GameType handlePerformUseItemOnGameTypeCheck(MultiPlayerGameMode localPlayerGameMode, Operation<GameType> original, LocalPlayer pPlayer, InteractionHand pHand, BlockHitResult pResul){
        GameType localGameType = original.call(localPlayerGameMode);
        if(localGameType == GameType.SPECTATOR && ((GCGhost)pPlayer).gcghosttracker$isGhostMode()
                && pPlayer.level().getBlockState(pResul.getBlockPos()).hasProperty(BlockStateProperties.OPEN)){
            return null;
        } else{
            return localGameType;
        }
    }
}
