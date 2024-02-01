package me.Thelnfamous1.gcghosttracker.mixin;

import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements GCGhost {

    @Unique
    private boolean gcghosttracker$ghostMode;
    @Unique
    private boolean gcghosttracker$ghostModeChanged;

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void gcghosttracker$setGhostMode(boolean enable) {
        this.gcghosttracker$ghostModeChanged = this.gcghosttracker$ghostMode != enable;
        this.gcghosttracker$ghostMode = enable;
    }

    @Override
    public boolean gcghosttracker$isGhostMode() {
        return this.gcghosttracker$ghostMode;
    }

    @Override
    public boolean gcghosttracker$hasGhostModeChanged() {
        return this.gcghosttracker$ghostModeChanged;
    }
}
