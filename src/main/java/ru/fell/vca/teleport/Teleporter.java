package ru.fell.vca.teleport;

import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import ru.fell.vca.Permissions;
import ru.fell.vca.VanillaCommandAdvance;
import ru.fell.vca.api.event.CommandTeleportEvent;
import ru.fell.vca.config.Messages;

/**
 * Телепорт с учётом Folia: позицию сущности читаем и двигаем её только в потоке её региона, через
 * {@link Entity#getScheduler()}. На Paper это тот же главный поток, так что код один.
 */
final class Teleporter {

    private final VanillaCommandAdvance plugin;

    Teleporter(VanillaCommandAdvance plugin) {
        this.plugin = plugin;
    }

    void toEntity(CommandSender sender, List<Entity> entities, Entity destination) {
        destination.getScheduler().run(plugin, task -> {
            Location location = destination.getLocation();
            for (Entity entity : entities) {
                entity.getScheduler().run(plugin, inner -> teleport(sender, entity, location.clone(),
                        Placeholder.component("target", destination.name())), null);
            }
        }, null);
        sender.sendMessage(plugin.messages().get("tp-to-entity",
                Placeholder.component("player", plugin.names(entities)),
                Placeholder.component("target", destination.name())));
    }

    void toLocation(CommandSender sender, List<Entity> entities, Location location) {
        TagResolver where = position(location);
        for (Entity entity : entities) {
            entity.getScheduler().run(plugin, task -> {
                // Взгляд не трогаем: ванильный /tp на координаты тоже сохраняет поворот.
                Location keepRotation = location.clone();
                keepRotation.setYaw(entity.getYaw());
                keepRotation.setPitch(entity.getPitch());
                teleport(sender, entity, keepRotation, where);
            }, null);
        }
        sender.sendMessage(plugin.messages().get("tp-to-location",
                Placeholder.component("player", plugin.names(entities)), where));
    }

    private void teleport(CommandSender sender, Entity entity, Location location, TagResolver where) {
        Messages messages = plugin.messages();
        if (!entity.getWorld().equals(location.getWorld()) && !sender.hasPermission(Permissions.TP_WORLDS)) {
            sender.sendMessage(messages.get("tp-no-worlds", Placeholder.component("player", entity.name())));
            return;
        }
        CommandTeleportEvent event = new CommandTeleportEvent(sender, entity, location);
        if (!event.callEvent()) {
            return;
        }
        entity.teleportAsync(event.getDestination(), TeleportCause.COMMAND).thenAccept(success -> {
            if (success && shouldNotify(sender, entity)) {
                entity.sendMessage(messages.get("tp-notify", Placeholder.unparsed("sender", sender.getName()),
                        where));
            }
        });
    }

    private boolean shouldNotify(CommandSender sender, Entity entity) {
        return entity instanceof Player && !entity.equals(sender) && !plugin.settings().silentTarget()
                && !sender.hasPermission(Permissions.TP_SILENT);
    }

    private static TagResolver position(Location location) {
        Component target = Component.text(format(location.getX()) + " " + format(location.getY()) + " "
                + format(location.getZ()));
        return TagResolver.resolver(
                Placeholder.component("target", target),
                Placeholder.unparsed("x", format(location.getX())),
                Placeholder.unparsed("y", format(location.getY())),
                Placeholder.unparsed("z", format(location.getZ())),
                Placeholder.unparsed("world", location.getWorld() == null ? "" : location.getWorld().getName()));
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
