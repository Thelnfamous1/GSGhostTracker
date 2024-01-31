package me.Thelnfamous1.gcghosttracker;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GhostSyncPacket {

    private final UUID player;
    private final boolean enabled;

    public GhostSyncPacket(Player player, boolean enabled){
        this.player = player.getUUID();
        this.enabled = enabled;
    }

    public GhostSyncPacket(FriendlyByteBuf buf){
        this.player = buf.readUUID();
        this.enabled = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeUUID(this.player);
        buf.writeBoolean(this.enabled);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> GCGhostTrackerClient.handleGhostSyncPacket(this));
        ctx.get().setPacketHandled(true);
    }

    public UUID getPlayer() {
        return this.player;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
