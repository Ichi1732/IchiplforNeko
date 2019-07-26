package com.jp.ichi.pl.ichipl.ichiplforneko;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;



public class CustomConfig {
    private FileConfiguration config = null;
    private final File configFile;
    private final String file;
    private final Plugin plugin;


    //コンストラクタ
    public CustomConfig(Plugin plugin) {
        this(plugin, "config.yml");
    }

    public CustomConfig(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = fileName;
        configFile = new File(plugin.getDataFolder(), file);
    }
    //ここまで
    //コンフィグファイルを保存
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(file, false);
        }
    }
    //ここまで
    //configFile = コンフィグファイルを指定
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }



    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;


    }
    public void saveConfig() {
        if (config == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,  configFile+"をセーブできませんでした。",ex);
        }
    }

}

