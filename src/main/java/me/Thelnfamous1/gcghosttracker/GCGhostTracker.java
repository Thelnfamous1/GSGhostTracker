package me.Thelnfamous1.gcghosttracker;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(GCGhostTracker.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GCGhostTracker {
    public static final String MODID = "gcghosttracker";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> PLAYER_TRACKER_COMPASS = ITEMS.register("player_tracker_compass", () -> new PlayerTrackerCompassItem(new Item.Properties()));

    public GCGhostTracker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> GCGTCommands.register(event.getDispatcher()));
        if(FMLEnvironment.dist == Dist.CLIENT){
            GCGhostTrackerClient.registerClientEvents();
        }
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.StartTracking event) -> {
            if(event.getTarget() instanceof ServerPlayer serverPlayer){
                GCGTNetwork.SYNC_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                        new GhostSyncPacket(serverPlayer, ((GCGhost)serverPlayer).gcghosttracker$isGhostMode()));
            }
        });
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GCGTNetwork::init);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(GCGhostTrackerClient::registerItemProperties);
    }
}
