package me.Thelnfamous1.gcghosttracker;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class GCGTNetwork {
    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(GCGhostTracker.MODID, "sync");
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME, () -> "1.0",
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int INDEX;

    public static void init(){
        SYNC_CHANNEL.registerMessage(INDEX++, GhostSyncPacket.class, GhostSyncPacket::write, GhostSyncPacket::new, GhostSyncPacket::handle);
    }
}
