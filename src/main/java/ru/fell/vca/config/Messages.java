package ru.fell.vca.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Тексты из messages.yml в формате MiniMessage. Ключ, которого нет в файле игрока, берётся из messages.yml
 * внутри jar, так что после обновления плагина новые сообщения появляются сами.
 */
public final class Messages {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Map<String, String> templates;

    private Messages(Map<String, String> templates) {
        this.templates = Map.copyOf(templates);
    }

    public static Messages load(ConfigurationSection file, ConfigurationSection defaults, Consumer<String> warn) {
        Map<String, String> templates = new HashMap<>();
        for (String key : defaults.getKeys(true)) {
            if (!defaults.isConfigurationSection(key)) {
                templates.put(key, file.getString(key, defaults.getString(key, "")));
            }
        }
        for (String key : file.getKeys(true)) {
            if (!file.isConfigurationSection(key) && !templates.containsKey(key)) {
                warn.accept("Неизвестный ключ " + key + " в messages.yml пропущен");
            }
        }
        return new Messages(templates);
    }

    /** {@code <prefix>} доступен в любом сообщении. */
    public Component get(String key, TagResolver... placeholders) {
        String template = templates.get(key);
        if (template == null) {
            throw new IllegalArgumentException("неизвестное сообщение " + key);
        }
        TagResolver prefix = Placeholder.parsed("prefix", templates.getOrDefault("prefix", ""));
        return MINI_MESSAGE.deserialize(template, TagResolver.resolver(TagResolver.resolver(placeholders), prefix));
    }

    /** Сырой текст без разбора, например название режима для подстановки в другое сообщение. */
    public String raw(String key) {
        return templates.getOrDefault(key, key);
    }
}
