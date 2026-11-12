package ru.fell.vca.gamemode;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.GameMode;

/** Все варианты ввода режима, как в ванили и привычных плагинах: {@code creative}, {@code c}, {@code 1}. */
public final class GameModes {

    /** Порядок как в ванили: survival, creative, adventure, spectator. */
    public static final List<GameMode> ORDER =
            List.of(GameMode.SURVIVAL, GameMode.CREATIVE, GameMode.ADVENTURE, GameMode.SPECTATOR);

    private GameModes() {
    }

    public static Optional<GameMode> parse(String raw) {
        return Optional.ofNullable(switch (raw.toLowerCase(Locale.ROOT)) {
            case "survival", "s", "0" -> GameMode.SURVIVAL;
            case "creative", "c", "1" -> GameMode.CREATIVE;
            case "adventure", "a", "2" -> GameMode.ADVENTURE;
            case "spectator", "sp", "3" -> GameMode.SPECTATOR;
            default -> null;
        });
    }

    public static String id(GameMode mode) {
        return mode.name().toLowerCase(Locale.ROOT);
    }
}
