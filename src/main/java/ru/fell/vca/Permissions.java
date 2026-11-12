package ru.fell.vca;

import java.util.Locale;
import org.bukkit.GameMode;

public final class Permissions {

    private static final String ROOT = "vanillacommandadvance.";

    public static final String GAMEMODE_OTHERS = ROOT + "gamemode.others";
    public static final String TP_SELF = ROOT + "tp.self";
    public static final String TP_OTHERS = ROOT + "tp.others";
    public static final String TP_COORDS = ROOT + "tp.coords";
    public static final String TP_WORLDS = ROOT + "tp.worlds";
    public static final String TP_SILENT = ROOT + "tp.silent";
    public static final String RELOAD = ROOT + "reload";

    private Permissions() {
    }

    public static String gameMode(GameMode mode) {
        return ROOT + "gamemode." + mode.name().toLowerCase(Locale.ROOT);
    }
}
