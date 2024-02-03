package me.Thelnfamous1.gcghosttracker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.Thelnfamous1.gcghosttracker.duck.GCGhost;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class GCGTCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> secondChance = Commands.literal("secondchance").requires(source -> source.hasPermission(4));
        secondChance.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("enable", BoolArgumentType.bool())
                        .executes(ctx -> {
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                            boolean enable = BoolArgumentType.getBool(ctx, "enable");
                            boolean wasGhost = ((GCGhost)player).gcghosttracker$isGhostMode();
                            ((GCGhost)player).gcghosttracker$setGhostMode(enable);
                            if(wasGhost != enable){
                                logGhostModeChange(ctx.getSource(), player, enable);
                            }
                            return 1;
                        })));
        LiteralArgumentBuilder<CommandSourceStack> targetcompass = Commands.literal("targetcompass").requires(source -> source.hasPermission(4));
        targetcompass.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("tracking", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                            ServerPlayer tracking = EntityArgument.getPlayer(ctx, "tracking");
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



    private static void logGhostModeChange(CommandSourceStack pSource, ServerPlayer pPlayer, boolean enable) {
        Component component = enable ? Component.translatable("gameMode.%s.ghost".formatted(GCGhostTracker.MODID)) : Component.translatable("gameMode.%s".formatted(pPlayer.gameMode.getGameModeForPlayer().getName()));
        if (pSource.getEntity() == pPlayer) {
            pSource.sendSuccess(() -> {
                return Component.translatable("commands.gamemode.success.self", component);
            }, true);
        } else {
            if (pSource.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
                pPlayer.sendSystemMessage(Component.translatable("gameMode.changed", component));
            }

            pSource.sendSuccess(() -> {
                return Component.translatable("commands.gamemode.success.other", pPlayer.getDisplayName(), component);
            }, true);
        }

    }
}
