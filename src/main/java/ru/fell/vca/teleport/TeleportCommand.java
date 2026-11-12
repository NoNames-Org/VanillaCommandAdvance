package ru.fell.vca.teleport;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import ru.fell.vca.Permissions;
import ru.fell.vca.VanillaCommandAdvance;

/**
 * {@code /tp} с теми же формами, что у ванили, плюс необязательный мир после координат:
 * <pre>
 * /tp &lt;кому&gt;                    /tp &lt;кого&gt; &lt;кому&gt;
 * /tp &lt;x y z&gt; [мир]              /tp &lt;кого&gt; &lt;x y z&gt; [мир]
 * </pre>
 */
public final class TeleportCommand {

    private final VanillaCommandAdvance plugin;
    private final Teleporter teleporter;

    public TeleportCommand(VanillaCommandAdvance plugin) {
        this.plugin = plugin;
        this.teleporter = new Teleporter(plugin);
    }

    public LiteralCommandNode<CommandSourceStack> node(String label) {
        return Commands.literal(label)
                .requires(source -> canUseAny(source.getSender()))
                .then(location(context -> selfOrNull(context)))
                .then(Commands.argument("destination", ArgumentTypes.entity())
                        .executes(context -> toEntity(context, selfOrNull(context))))
                .then(Commands.argument("targets", ArgumentTypes.entities())
                        .requires(source -> source.getSender().hasPermission(Permissions.TP_OTHERS))
                        .then(location(this::targets))
                        .then(Commands.argument("destination", ArgumentTypes.entity())
                                .executes(context -> toEntity(context, targets(context)))))
                .build();
    }

    private static boolean canUseAny(CommandSender sender) {
        return sender.hasPermission(Permissions.TP_SELF) || sender.hasPermission(Permissions.TP_OTHERS);
    }

    @FunctionalInterface
    private interface Targets {
        List<Entity> resolve(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }

    // <x y z> и <x y z> <мир>: ветка одинаковая для «себя» и для других.
    private RequiredArgumentBuilder<CommandSourceStack, FinePositionResolver> location(Targets targets) {
        return Commands.argument("location", ArgumentTypes.finePosition(true))
                .requires(source -> source.getSender().hasPermission(Permissions.TP_COORDS))
                .executes(context -> toLocation(context, targets.resolve(context), null))
                .then(Commands.argument("world", ArgumentTypes.world())
                        .executes(context -> toLocation(context, targets.resolve(context),
                                context.getArgument("world", World.class))));
    }

    /** {@code null}, если команду ввела не сущность (консоль, командный блок). */
    private static List<Entity> selfOrNull(CommandContext<CommandSourceStack> context) {
        Entity executor = context.getSource().getExecutor();
        return executor == null ? null : List.of(executor);
    }

    private List<Entity> targets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return context.getArgument("targets", EntitySelectorArgumentResolver.class).resolve(context.getSource());
    }

    private int toEntity(CommandContext<CommandSourceStack> context, List<Entity> entities)
            throws CommandSyntaxException {
        if (!allowed(context, entities)) {
            return 0;
        }
        Entity destination = context.getArgument("destination", EntitySelectorArgumentResolver.class)
                .resolve(context.getSource()).get(0);
        teleporter.toEntity(context.getSource().getSender(), entities, destination);
        return entities.size();
    }

    private int toLocation(CommandContext<CommandSourceStack> context, List<Entity> entities, World world)
            throws CommandSyntaxException {
        if (!allowed(context, entities)) {
            return 0;
        }
        CommandSourceStack source = context.getSource();
        // ~ и ^ считаются от того, кто ввёл команду, как в ванили.
        World target = world != null ? world : source.getLocation().getWorld();
        Location location = context.getArgument("location", FinePositionResolver.class).resolve(source)
                .toLocation(target);
        teleporter.toLocation(source.getSender(), entities, location);
        return entities.size();
    }

    /** Себя двигает {@code tp.self}, остальных {@code tp.others}. */
    private boolean allowed(CommandContext<CommandSourceStack> context, List<Entity> entities) {
        CommandSender sender = context.getSource().getSender();
        if (entities == null) {
            sender.sendMessage(plugin.messages().get("player-only"));
            return false;
        }
        Entity executor = context.getSource().getExecutor();
        boolean self = entities.contains(executor);
        boolean others = entities.stream().anyMatch(entity -> !entity.equals(executor));
        if ((self && !sender.hasPermission(Permissions.TP_SELF))
                || (others && !sender.hasPermission(Permissions.TP_OTHERS))) {
            sender.sendMessage(plugin.messages().get("no-permission"));
            return false;
        }
        return true;
    }
}
