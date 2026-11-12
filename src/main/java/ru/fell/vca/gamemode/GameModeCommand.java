package ru.fell.vca.gamemode;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.fell.vca.Permissions;
import ru.fell.vca.VanillaCommandAdvance;
import ru.fell.vca.api.event.CommandGameModeEvent;
import ru.fell.vca.config.Messages;

/** {@code /gamemode <режим> [игроки]}. */
public final class GameModeCommand {

    private final VanillaCommandAdvance plugin;

    public GameModeCommand(VanillaCommandAdvance plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> node(String label) {
        return Commands.literal(label)
                .requires(source -> canUseAny(source.getSender()))
                .then(Commands.argument("mode", StringArgumentType.word())
                        .suggests(this::suggestModes)
                        .executes(this::changeOwn)
                        .then(Commands.argument("targets", ArgumentTypes.players())
                                .requires(source -> source.getSender().hasPermission(Permissions.GAMEMODE_OTHERS))
                                .executes(this::changeOthers)))
                .build();
    }

    private static boolean canUseAny(CommandSender sender) {
        return GameModes.ORDER.stream().anyMatch(mode -> sender.hasPermission(Permissions.gameMode(mode)));
    }

    private CompletableFuture<Suggestions> suggestModes(CommandContext<CommandSourceStack> context,
                                                        SuggestionsBuilder builder) {
        CommandSender sender = context.getSource().getSender();
        for (GameMode mode : GameModes.ORDER) {
            String id = GameModes.id(mode);
            if (sender.hasPermission(Permissions.gameMode(mode)) && id.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(id);
            }
        }
        return builder.buildFuture();
    }

    private int changeOwn(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            sender.sendMessage(plugin.messages().get("player-only"));
            return 0;
        }
        return change(context, List.of(player));
    }

    private int changeOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Player> players = context.getArgument("targets", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource());
        return change(context, players);
    }

    private int change(CommandContext<CommandSourceStack> context, List<Player> players) {
        CommandSender sender = context.getSource().getSender();
        Messages messages = plugin.messages();
        String input = StringArgumentType.getString(context, "mode");
        Optional<GameMode> parsed = GameModes.parse(input);
        if (parsed.isEmpty()) {
            sender.sendMessage(messages.get("gamemode-unknown", Placeholder.unparsed("input", input)));
            return 0;
        }
        GameMode mode = parsed.get();
        if (!sender.hasPermission(Permissions.gameMode(mode))) {
            sender.sendMessage(messages.get("no-permission"));
            return 0;
        }
        for (Player player : players) {
            player.getScheduler().run(plugin, task -> apply(sender, player, mode), null);
        }
        boolean onlySelf = players.size() == 1 && players.get(0).equals(sender);
        sender.sendMessage(messages.get(onlySelf ? "gamemode-self" : "gamemode-other",
                Placeholder.component("player", plugin.names(players)),
                Placeholder.parsed("gamemode", modeName(messages, mode))));
        return players.size();
    }

    // Поток игрока: на Folia менять режим можно только из потока его региона.
    private void apply(CommandSender sender, Player player, GameMode mode) {
        CommandGameModeEvent event = new CommandGameModeEvent(sender, player, mode);
        if (!event.callEvent()) {
            return;
        }
        player.setGameMode(event.getGameMode());
        if (!player.equals(sender)) {
            Messages messages = plugin.messages();
            player.sendMessage(messages.get("gamemode-changed-by",
                    Placeholder.unparsed("sender", sender.getName()),
                    Placeholder.parsed("gamemode", modeName(messages, event.getGameMode()))));
        }
    }

    private static String modeName(Messages messages, GameMode mode) {
        return messages.raw("gamemode-names." + GameModes.id(mode));
    }
}
