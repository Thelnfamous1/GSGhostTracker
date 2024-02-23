package me.Thelnfamous1.gcghosttracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<
        T extends net.minecraft.world.entity.LivingEntity,
        M extends net.minecraft.client.model.EntityModel<T>>
{

    /*
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSpectator()Z"))
    private boolean handleRenderLayers(T pEntity, Operation<Boolean> isSpectator){
        if(pEntity instanceof Player player){
            return GCGhost.isActualSpectator(isSpectator.call(player), player);
        }
        return isSpectator.call(pEntity);
    }
     */
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void handleRenderToBuffer(M pModel, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha, Operation<Void> renderToBuffer, T pEntity){
        if(pEntity.isSpectator() && pEntity instanceof GCGhost ghost && ghost.gcghosttracker$isGhostMode()){
            renderToBuffer.call(pModel, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, 0.25F);
        } else{
            renderToBuffer.call(pModel, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handleIsInvisibleTo(T pEntity, Player player, Operation<Boolean> isInvisibleTo){
        if(pEntity.isSpectator() && pEntity instanceof GCGhost ghost && ghost.gcghosttracker$isGhostMode()){
            return false;
        } else{
            return isInvisibleTo.call(pEntity, player);
        }
    }
}
