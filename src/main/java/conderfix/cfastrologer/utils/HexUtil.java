package conderfix.cfastrologer.utils;

import org.bukkit.ChatColor;

public class HexUtil {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
