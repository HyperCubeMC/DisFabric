package br.com.brforgers.mods.disfabric.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import br.com.brforgers.mods.disfabric.DisFabric;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode<ServerCommandSource> node = registerMain(commandDispatcher); // Registers main command
    }

    public static LiteralCommandNode<ServerCommandSource> registerMain(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        return commandDispatcher.register(CommandManager.literal("discord")
                .then(CommandManager.literal("version")
                        // The command to be executed if the command "discord" is entered with the argument "version"
                        .executes(DiscordCommand::ModVersion))
                .then(CommandManager.literal("broadcast")
                        // The command to be executed if the command "discord" is entered with the argument "broadcast"
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("message", greedyString())
                                .executes(DiscordCommand::DiscordBroadcast))
                        .executes(ctx -> {
                            ServerCommandSource source = ctx.getSource();
                            source.sendFeedback(new LiteralText("You did not specify a message to broadcast!").formatted(Formatting.RED), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                // The command "discord" to execute if there are no arguments.
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    String discordCommandText = DisFabric.config.texts.discordCommandText;
                    source.sendFeedback(new LiteralText(discordCommandText).formatted(Formatting.GOLD), false);
                    return Command.SINGLE_SUCCESS;
                })
        );
    }

    public static int ModVersion(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        String version = FabricLoader.getInstance().getModContainer("disfabric").get().getMetadata().getVersion().getFriendlyString();
        source.sendFeedback(new LiteralText("This server is using Justsnoopy30's fork of DisFabric v" + version).formatted(Formatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int DiscordBroadcast(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        String message = getString(ctx, "message");
        DisFabric.textChannel.sendMessage(message).queue();
        return Command.SINGLE_SUCCESS;
    }
}
