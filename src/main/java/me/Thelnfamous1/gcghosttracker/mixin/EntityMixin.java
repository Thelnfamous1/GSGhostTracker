package me.Thelnfamous1.gcghosttracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @WrapOperation(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;noPhysics:Z", opcode = Opcodes.GETFIELD))
    private boolean handlePush(Entity entity, Operation<Boolean> noPhysics){
        if(entity.isSpectator() && entity instanceof GCGhost ghost && ghost.gcghosttracker$isGhostMode()){
            return false;
        }
        return noPhysics.call(entity);
    }
}
