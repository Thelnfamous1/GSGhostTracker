package me.Thelnfamous1.gcghosttracker;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class PlayerTrackerCompassItem extends CompassItem {
    public static final String TRACKING_TAG = "TrackingPlayer";
    public static final String ENTITY_TAG = "PlayerStatus";
    public static final String ROTATIONS_TAG = "Rotations";

    public PlayerTrackerCompassItem(Properties pProperties) {
        super(pProperties);
    }

    public static void track(ItemStack playerTrackerCompass, Player tracking) {
        playerTrackerCompass.getOrCreateTag().put(TRACKING_TAG, NbtUtils.createUUID(tracking.getUUID()));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity holder, int pItemSlot, boolean pIsSelected) {
        if (!world.isClientSide) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                if (tag.contains(TRACKING_TAG)) {
                    Player trackedPlayer = this.getTrackedPlayer((ServerLevel) world, stack);
                    if (trackedPlayer != null) {
                        tag.put(ENTITY_TAG, PlayerStatusData.write(trackedPlayer));

                        /*
                        CompoundTag persistantData = trackedPlayer.getPersistentData();
                        persistantData.putBoolean(SCEvents.TAG_CHUNK_UPDATE, true);
                        persistantData.putInt(SCEvents.TAG_CHUNK_TIMER, 20);=
                         */
                    } else if (tag.contains(ENTITY_TAG)) {
                        PlayerStatusData data = PlayerStatusData.read(tag.getCompound(ENTITY_TAG));
                        /*
                        ChunkPos chunkpos = new ChunkPos(data.pos);
                        if (!SCEvents.isChunkForced((ServerLevel) world, chunkpos)) {
                            world.getChunkSource().updateChunkForced(chunkpos, false);
                        }
                         */

                        tag.put(ENTITY_TAG, PlayerStatusData.writeMissingPlayer(data));
                    }

                    if (tag.contains(ROTATIONS_TAG)) {
                        RotationData rotations = RotationData.read(tag.getCompound(ROTATIONS_TAG));

                        double turn;
                        if (tag.contains(ENTITY_TAG)) {
                            double yaw = holder.getYRot();
                            yaw = positiveModulo(yaw / 360.0D, 1.0D);
                            double angle = this.getAngleToTrackedEntity(stack, holder) / (double) ((float) Math.PI * 2F);
                            turn = 0.5D - (yaw - 0.25D - angle);
                        } else {
                            turn = Math.random();
                        }

                        Pair<Long, double[]> rotationData = this.wobble(world, turn, rotations.lastUpdateTick, rotations.rotation, rotations.rota);
                        rotations = new RotationData(rotationData.getSecond()[0], rotationData.getSecond()[1], rotationData.getFirst());

                        tag.put(ROTATIONS_TAG, RotationData.write(rotations));
                    } else {
                        RotationData rotations = new RotationData(0.0F, 0.0F, 0L);

                        double turn;
                        if (tag.contains(ENTITY_TAG)) {
                            double yaw = holder.getYRot();
                            yaw = positiveModulo(yaw / 360.0D, 1.0D);
                            double angle = this.getAngleToTrackedEntity(stack, holder) / (double) ((float) Math.PI * 2F);
                            turn = 0.5D - (yaw - 0.25D - angle);
                        } else {
                            turn = Math.random();
                        }

                        Pair<Long, double[]> rotationData = this.wobble(world, turn, rotations.lastUpdateTick, rotations.rotation, rotations.rota);
                        rotations = new RotationData(rotationData.getSecond()[0], rotationData.getSecond()[1], rotationData.getFirst());

                        tag.put(ROTATIONS_TAG, RotationData.write(rotations));
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    private Player getTrackedPlayer(ServerLevel world, ItemStack stack) {
        return world.getPlayerByUUID(NbtUtils.loadUUID(stack.getTag().get(TRACKING_TAG)));
    }

    private double getAngleToTrackedEntity(ItemStack stack, Entity entity) {
        PlayerStatusData data = PlayerStatusData.read(stack.getTag().getCompound(ENTITY_TAG));
        BlockPos pos = data.pos;
        return Math.atan2((double) pos.getZ() - entity.getZ(), (double) pos.getX() - entity.getX());
    }

    private Pair<Long, double[]> wobble(Level world, double angle, long lastUpdateTickIn, double rotationIn, double rotaIn) {
        long lastUpdateTick = lastUpdateTickIn;
        double rotation = rotationIn;
        double rota = rotaIn;

        if(world.getGameTime() != lastUpdateTick) {
            lastUpdateTick = world.getGameTime();
            double d0 = angle - rotation;
            d0 = positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            rota += d0 * 0.1D;
            rota *= 0.8D;
            rotation = positiveModulo(rotation + rota, 1.0D);
        }

        return Pair.of(lastUpdateTick, new double[] {rotation, rota});
    }

    public static double positiveModulo(double numerator, double denominator) {
        return (numerator % denominator + denominator) % denominator;
    }

    public record PlayerStatusData(BlockPos pos) {

        public static PlayerStatusData read(CompoundTag compound) {
                return new PlayerStatusData(
                        NbtUtils.readBlockPos(compound.getCompound("Pos")));
            }

            public static CompoundTag write(Player trackedPlayer) {
                CompoundTag tag = new CompoundTag();
                tag.put("Pos", NbtUtils.writeBlockPos(trackedPlayer.blockPosition()));
                return tag;
            }

            public static CompoundTag writeMissingPlayer(PlayerStatusData status) {
                CompoundTag tag = new CompoundTag();
                tag.put("Pos", NbtUtils.writeBlockPos(status.pos));
                return tag;
            }
        }

    public record RotationData(double rotation, double rota, long lastUpdateTick) {

        public static RotationData read(CompoundTag compound) {
            return new RotationData(compound.getDouble("Rotation"), compound.getDouble("Rota"), compound.getLong("LastUpdateTick"));
        }

        public static CompoundTag write(RotationData data) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("Rotation", data.rotation);
            tag.putDouble("Rota", data.rota);
            tag.putLong("LastUpdateTick", data.lastUpdateTick);
            return tag;
        }
    }
}
