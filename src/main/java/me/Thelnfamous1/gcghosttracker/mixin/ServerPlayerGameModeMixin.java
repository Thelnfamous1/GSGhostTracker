package me.Thelnfamous1.gcghosttracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @WrapOperation(method = "useItemOn", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;gameModeForPlayer:Lnet/minecraft/world/level/GameType;", ordinal = 0))
    private GameType handlePerformUseItemOnGameTypeCheck(ServerPlayerGameMode serverPlayerGameMode, Operation<GameType> original, ServerPlayer pPlayer, Level pLevel, ItemStack pStack, InteractionHand pHand, BlockHitResult pHitResult){
        GameType serverGameType = original.call(serverPlayerGameMode);
        if(serverGameType == GameType.SPECTATOR && ((GCGhost)pPlayer).gcghosttracker$isGhostMode()
                && pLevel.getBlockState(pHitResult.getBlockPos()).hasProperty(BlockStateProperties.OPEN)){
            return null;
        } else{
            return serverGameType;
        }
    }
}
