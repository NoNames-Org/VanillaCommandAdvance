package ru.fell.vca.api.event;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Перед сменой режима через {@code /gamemode}. Вызывается в потоке игрока (на Folia — в потоке его региона).
 * Отмена оставляет режим как есть, режим можно подменить.
 */
public final class CommandGameModeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CommandSender sender;
    private final Player player;
    private GameMode gameMode;
    private boolean cancelled;

    public CommandGameModeEvent(CommandSender sender, Player player, GameMode gameMode) {
        this.sender = sender;
        this.player = player;
        this.gameMode = gameMode;
    }

    /** Кто ввёл команду. */
    public CommandSender getSender() {
        return sender;
    }

    /** Чей режим меняется. */
    public Player getPlayer() {
        return player;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
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
