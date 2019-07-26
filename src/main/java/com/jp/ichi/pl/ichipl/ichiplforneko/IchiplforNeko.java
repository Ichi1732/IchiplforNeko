package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.plugin.java.JavaPlugin;

public final class IchiplforNeko extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        CustomConfig CCc =  new CustomConfig(this,"combination.yml");
        CustomConfig CCf = new CustomConfig(this,"flykick_cancel.yml");
        CustomConfig CCn = new CustomConfig(this,"note.yml");
        CCc.saveDefaultConfig();
        CCf.saveDefaultConfig();
        CCn.saveDefaultConfig();
        getLogger().info("起動");
        getCommand("combination").setExecutor(new combination(this,CCc));
        getCommand("beam").setExecutor(new beam());
        getCommand("flykickcancel").setExecutor(new flykick_cancel(this, CCf));
        getCommand("movesound").setExecutor(new note(this,CCn));
        getCommand("changecolor").setExecutor(new ChangeCommandColor());
        getCommand("cristalrotation").setExecutor(new end_cristal(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("停止");
    }
}
