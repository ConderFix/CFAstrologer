package conderfix.cfastrologer.utils;

import conderfix.cfastrologer.AstrologerPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Storage {

    private File file;
    private FileConfiguration config;

    public Storage(String name) {
        this.file = new File(AstrologerPlugin.inst.getDataFolder(), name);
        try {
            if (!this.file.exists()) {
                if (!this.file.getParentFile().exists()) {
                    this.file.getParentFile().mkdirs(); // Создание несуществующих директорий
                }
                if (!this.file.createNewFile()) {
                    throw new IOException("Failed to create file");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: ", e);
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file:", e);
        }
    }
}
