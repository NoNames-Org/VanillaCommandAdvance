package ru.fell.vca.config;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.bukkit.configuration.ConfigurationSection;

/**
 * config.yml.
 *
 * @param silentTarget телепортированному игроку ничего не пишется, сообщение видит только отправитель
 */
public record PluginConfig(CommandSettings gamemode, CommandSettings teleport, boolean silentTarget) {

    private static final Pattern LABEL = Pattern.compile("[a-z0-9_-]+");

    /** @param labels имя команды и алиасы, без повторов */
    public record CommandSettings(boolean enabled, List<String> labels) {

        public CommandSettings {
            labels = List.copyOf(labels);
        }
    }

    public static PluginConfig load(ConfigurationSection root, Consumer<String> warn) {
        return new PluginConfig(
                command(root, "gamemode", "gamemode", List.of("gm"), warn),
                command(root, "tp", "tp", List.of("teleport"), warn),
                root.getBoolean("silent-target", true));
    }

    private static CommandSettings command(ConfigurationSection root, String key, String name,
                                           List<String> defaultAliases, Consumer<String> warn) {
        String path = "commands." + key;
        boolean enabled = root.getBoolean(path + ".enabled", true);
        List<String> aliases = root.isList(path + ".aliases")
                ? root.getStringList(path + ".aliases")
                : defaultAliases;
        Set<String> labels = new LinkedHashSet<>();
        labels.add(name);
        for (String alias : aliases) {
            String label = alias.trim().toLowerCase(Locale.ROOT);
            if (LABEL.matcher(label).matches()) {
                labels.add(label);
            } else {
                warn.accept(path + ".aliases: '" + alias + "' пропущен, допустимы только a-z, 0-9, _ и -");
            }
        }
        return new CommandSettings(enabled, new ArrayList<>(labels));
    }
}
