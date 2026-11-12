package ru.fell.vca.api.event;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Перед телепортом через {@code /tp}, отдельно для каждой сущности. Вызывается в потоке сущности (на Folia — в
 * потоке её региона). Отмена пропускает телепорт этой сущности, точку назначения можно подменить.
 */
public final class CommandTeleportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CommandSender sender;
    private final Entity entity;
    private Location destination;
    private boolean cancelled;

    public CommandTeleportEvent(CommandSender sender, Entity entity, Location destination) {
        this.sender = sender;
        this.entity = entity;
        this.destination = destination;
    }

    /** Кто ввёл команду. */
    public CommandSender getSender() {
        return sender;
    }

    /** Кого телепортируют. */
    public Entity getEntity() {
        return entity;
    }

    /** Копия: изменения применяются только через {@link #setDestination}. */
    public Location getDestination() {
        return destination.clone();
    }

    public void setDestination(Location destination) {
        this.destination = destination.clone();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
