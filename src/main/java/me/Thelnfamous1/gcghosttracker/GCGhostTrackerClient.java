package me.Thelnfamous1.gcghosttracker;

import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public class GCGhostTrackerClient {

    public static void registerClientEvents(){
        MinecraftForge.EVENT_BUS.addListener((RenderNameTagEvent event) -> {
            if(event.getEntity().isSpectator() && event.getEntity() instanceof GCGhost ghost && ghost.gcghosttracker$isGhostMode()){
                event.setResult(Event.Result.DENY);
            }
        });
    }

    public static void registerItemProperties(){
        GCGhostTracker.LOGGER.info("Registering item properties!");
        ItemProperties.register(
                GCGhostTracker.PLAYER_TRACKER_COMPASS.get(),
                new ResourceLocation("angle"),
                new ClampedItemPropertyFunction() {
                    private double rotation;
                    private double rota;
                    private long lastUpdateTick;

                    @Override
                    public float unclampedCall(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int seed) {
                        if (livingEntity == null && !stack.isFramed()) {
                            return 0.484375F;
                        } else {
                            boolean flag = livingEntity != null;
                            Entity entity = flag ? livingEntity : stack.getFrame();
                            if (world == null) {
                                world = (ClientLevel) entity.level();
                            }

                            CompoundTag tag = stack.getTag();
                            if (tag != null && tag.contains(PlayerTrackerCompassItem.ROTATIONS_TAG) && tag.contains(PlayerTrackerCompassItem.ENTITY_TAG) && !stack.isFramed()) {
                                return (float) PlayerTrackerCompassItem.positiveModulo(PlayerTrackerCompassItem.RotationData.read(tag.getCompound(PlayerTrackerCompassItem.ROTATIONS_TAG)).rotation(), 1.0F);
                            } else {
                                double randRotation = Math.random();

                                if (flag) {
                                    randRotation = this.wobble(world, randRotation);
                                }
                                return (float) PlayerTrackerCompassItem.positiveModulo((float) randRotation, 1.0F);
                            }
                        }
                    }

                    private double wobble(ClientLevel world, double rotation) {
                        if (world.getGameTime() != this.lastUpdateTick) {
                            this.lastUpdateTick = world.getGameTime();
                            double d0 = rotation - this.rotation;
                            d0 = PlayerTrackerCompassItem.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                            this.rota += d0 * 0.1D;
                            this.rota *= 0.8D;
                            this.rotation = PlayerTrackerCompassItem.positiveModulo(this.rotation + this.rota, 1.0D);
                        }

                        return this.rotation;
                    }
                });
    }

    public static void handleGhostSyncPacket(GhostSyncPacket packet){
        Player player = Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayer());
        if(player != null){
            ((GCGhost)player).gcghosttracker$setGhostMode(packet.isEnabled());
        }
    }
}
