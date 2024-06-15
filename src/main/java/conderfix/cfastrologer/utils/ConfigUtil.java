package conderfix.cfastrologer.utils;

import conderfix.cfastrologer.AstrologerPlugin;

public class ConfigUtil {

    public static String getString(String path) {
        return HexUtil.translate(AstrologerPlugin.inst.getConfig().getString(path));
    }

    public static int getInt(String path) {
        return AstrologerPlugin.inst.getConfig().getInt(path);
    }

    public static int getSlot(Integer path) {
        String item1Value = getString("positions.item-"+path);
        String[] parts = item1Value.split(";");
        int slot = Integer.parseInt(parts[0]);
        return slot;
    }

    public static int getAmount(Integer path) {
        String item1Value = ConfigUtil.getString("positions.item-"+path);
        String[] parts = item1Value.split(";");
        int amountItem = Integer.parseInt(parts[1]);
        return amountItem;
    }
}
