package me.Thelnfamous1.gcghosttracker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public class GCGTCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> secondChance = Commands.literal("secondchance").requires(source -> source.hasPermission(4));
        secondChance.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("enable", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Player player = EntityArgument.getPlayer(ctx, "player");
                            boolean enable = BoolArgumentType.getBool(ctx, "enable");
                            ((GCGhost)player).gcghosttracker$setGhostMode(enable);
                            GCGTNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new GhostSyncPacket(player, enable));
                            return 1;
                        })));
        LiteralArgumentBuilder<CommandSourceStack> targetcompass = Commands.literal("targetcompass").requires(source -> source.hasPermission(4));
        targetcompass.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("tracking", EntityArgument.player())
                        .executes(ctx -> {
                            Player player = EntityArgument.getPlayer(ctx, "player");
                            Player tracking = EntityArgument.getPlayer(ctx, "tracking");
                            ItemStack playerTrackerCompass = GCGhostTracker.PLAYER_TRACKER_COMPASS.get().getDefaultInstance();
                            PlayerTrackerCompassItem.track(playerTrackerCompass, tracking);
                            if(!player.getInventory().add(playerTrackerCompass)){
                                player.drop(playerTrackerCompass, false);
                            }
                            return 1;
                        })));
        dispatcher.register(secondChance);
        dispatcher.register(targetcompass);
    }
}
