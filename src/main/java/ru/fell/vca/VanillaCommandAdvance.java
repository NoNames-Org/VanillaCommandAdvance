package ru.fell.vca;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fell.vca.config.Messages;
import ru.fell.vca.config.PluginConfig;
import ru.fell.vca.gamemode.GameModeCommand;
import ru.fell.vca.teleport.TeleportCommand;

public final class VanillaCommandAdvance extends JavaPlugin {

    private static final String MESSAGES = "messages.yml";

    private volatile PluginConfig settings;
    private volatile Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!new File(getDataFolder(), MESSAGES).exists()) {
            saveResource(MESSAGES, false);
        }
        reload();
        GameModeCommand gamemode = new GameModeCommand(this);
        TeleportCommand teleport = new TeleportCommand(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            PluginConfig current = settings;
            register(registrar, current.gamemode(), gamemode::node, "Смена режима игры");
            register(registrar, current.teleport(), teleport::node, "Телепорт");
            registrar.register(adminNode(), "VanillaCommandAdvance", List.of("vanillacommandadvance"));
        });
    }

    // Каждое имя регистрируется отдельной командой: у Paper ванильную команду перекрывает только главное имя,
    // алиас вроде /teleport остался бы ванильным.
    private static void register(Commands registrar, PluginConfig.CommandSettings command,
                                 Function<String, LiteralCommandNode<CommandSourceStack>> node, String description) {
        if (!command.enabled()) {
            return;
        }
        for (String label : command.labels()) {
            registrar.register(node.apply(label), description, List.of());
        }
    }

    private LiteralCommandNode<CommandSourceStack> adminNode() {
        return Commands.literal("vca")
                .requires(source -> source.getSender().hasPermission(Permissions.RELOAD))
                .then(Commands.literal("reload").executes(context -> {
                    reload();
                    context.getSource().getSender().sendMessage(messages.get("reloaded"));
                    return 1;
                }))
                .build();
    }

    /** Перечитывает config.yml и messages.yml. Включение команд и алиасы применяются после рестарта. */
    private void reload() {
        reloadConfig();
        settings = PluginConfig.load(getConfig(), getLogger()::warning);
        YamlConfiguration file = YamlConfiguration.loadConfiguration(new File(getDataFolder(), MESSAGES));
        messages = Messages.load(file, bundledMessages(), getLogger()::warning);
    }

    // loadConfiguration сам закрывает reader.
    private YamlConfiguration bundledMessages() {
        Reader reader = getTextResource(MESSAGES);
        if (reader == null) {
            throw new IllegalStateException("В jar плагина нет " + MESSAGES);
        }
        return YamlConfiguration.loadConfiguration(reader);
    }

    public PluginConfig settings() {
        return settings;
    }

    public Messages messages() {
        return messages;
    }

    /** Имя одной сущности или «N сущностей» для сообщений. */
    public Component names(List<? extends Entity> entities) {
        if (entities.size() == 1) {
            return entities.get(0).name();
        }
        return messages.get("many-entities", Placeholder.unparsed("count", Integer.toString(entities.size())));
    }
}
