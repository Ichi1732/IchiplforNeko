package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class flykick_cancel implements Listener , CommandExecutor , TabCompleter {

    private Map<String, BoundingBox> cancelLoc = new HashMap<>();
    final private CustomConfig Cconfig;
    private FileConfiguration config;
    final private Plugin pl;

    //コンストラクタ
    public flykick_cancel(Plugin pl, CustomConfig CustomConfig){
        this.Cconfig = CustomConfig;
        this.config = CustomConfig.getConfig();
        this.pl=pl;
        pl.getServer().getPluginManager().registerEvents(this, pl);

        int fail=posreload();

        if(fail==-1){
            for(Player player:pl.getServer().getOnlinePlayers()){
                if(player.hasPermission("ichipl.fkc"))player.sendMessage(ChatColor.RED.toString()+"キャンセルエリアは現在登録されていません");
            }
        } else if(fail>0){
            for(Player player:pl.getServer().getOnlinePlayers()){
                if(player.hasPermission("ichipl.fkc"))player.sendMessage(ChatColor.RED.toString()+fail+"個のキャンセルエリアの正規化に失敗しました。");
            }
        }

    }

    @EventHandler
    public void kick(PlayerKickEvent e){
        if(e.getReason().equals("Flying is not enabled on this server")) {
            Location location = e.getPlayer().getLocation();
            for(String key:cancelLoc.keySet()){
                if(location.getWorld().getName().equals(config.getString("location."+key+".world"))) {
                    BoundingBox pos = cancelLoc.get(key);
                    if(pos.contains(location.getBlockX(),location.getBlockY(),location.getBlockZ())){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"プレイヤー専用コマンドです。");
            return true;
        }


        if(args.length==0){
            sender.sendMessage(ChatColor.RED+"使用法:/flykickcancel(fkc) add|remove|list");
        }else if(args[0].equals("add")){
            if(args.length<8)sender.sendMessage(ChatColor.RED+"使用法:/flykickcancel 名前 X Y Z X2 Y2 Z2");
            else {
                Player player = (Player) sender;
                config.set("location."+args[1]+".world",player.getLocation().getWorld().getName());
                try{
                    BoundingBox pos = new BoundingBox(Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]),Double.parseDouble(args[5]),Double.parseDouble(args[6]),Double.parseDouble(args[7]));
                    config.set("location."+args[1]+".pos",pos);
                    Cconfig.saveConfig();
                    posreload();
                }catch (NumberFormatException NFEx){
                    sender.sendMessage(ChatColor.RED+"使用法:/flykickcancel add 名前 X Y Z X2 Y2 Z2");
                    Cconfig.reloadConfig();
                    posreload();
                    return true;
                }
                sender.sendMessage("キャンセルエリアが正常に登録されました");
                return true;
            }
        }else if(args[0].equals("reload")){
            Cconfig.reloadConfig();
            config = Cconfig.getConfig();
            int fail = posreload();
            if(fail==-1){
                sender.sendMessage(ChatColor.RED+"キャンセルエリアは現在登録されていません");
                return true;
            } else if(fail>0){
                sender.sendMessage(ChatColor.RED.toString()+fail+"個のキャンセルエリアの正規化に失敗しました。");
                return true;
            }else {
                sender.sendMessage(ChatColor.AQUA+"キャンセルエリア群が正常にリロードされました。");
                return true;
            }
        } else if(args[0].equals("remove")){
            try {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "使用法:/flykickcancel remove 名前");
                    return true;
                } else {
                    if (config.getConfigurationSection("location").getKeys(false).contains(args[1])) {
                        config.set("location." + args[1], null);
                        Cconfig.saveConfig();
                        posreload();
                        sender.sendMessage(ChatColor.AQUA + "キャンセルエリアが正常に削除されました。");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "そのようなキャンセルエリアは登録されていません。");
                        return true;
                    }
                }
            }catch (NullPointerException NPEx){
                sender.sendMessage(ChatColor.RED+"キャンセルエリアは現在登録されていません");
                return true;
            }
        }else if(args[0].equals("list")){
            try {
                Set<String> keys = config.getConfigurationSection("location").getKeys(false);
                if (keys.size() != 0) {
                    sender.sendMessage(ChatColor.AQUA + "登録されているキャンセルエリア名");
                    for (String name : keys) {
                        sender.sendMessage(ChatColor.AQUA + name);
                    }
                } else sender.sendMessage(ChatColor.AQUA + "キャンセルエリアは現在登録されていません");
                return true;
            }catch (NullPointerException NPEx){
                sender.sendMessage(ChatColor.RED+"キャンセルエリアは現在登録されていません");
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length==0){
            return Arrays.asList("add","remove","reload","list");
        }else if(args.length==1){
            if(args[0].length()==0){
                return Arrays.asList("add","remove","reload","list");
            }else{
                if("add".startsWith(args[0]))return Collections.singletonList("add");
                if("list".startsWith(args[0]))return Collections.singletonList("list");
                if(args[0].startsWith("r")){
                    if(args[0].length()<=2) {
                        return Arrays.asList("remove", "reload");
                    } else {
                        if (String.valueOf(args[0].charAt(2)).equals("m")) return Collections.singletonList("remove");
                        if (String.valueOf(args[0].charAt(2)).equals("l")) return Collections.singletonList("reload");
                    }
                }
            }
        }
        return Collections.singletonList("");
    }

    private int posreload(){

        cancelLoc.clear();
        int fail=0;
        try {
            for (String name : config.getConfigurationSection("location").getKeys(false)) {
                String worldname = config.getString("location." + name + ".world");

                //ワールドが存在するか
                boolean boo = false;
                for (World world : pl.getServer().getWorlds()) {
                    if (world.getName().equals(worldname)) {
                        boo = true;
                        break;
                    }
                }
                if (boo == true) {
                    try {
                        BoundingBox pos = (BoundingBox) config.get("location."+name+".pos");
                        cancelLoc.put(name, pos);
                    } catch (NullPointerException NPEx) {
                        fail++;
                    }
                } else fail++;
            }
        }catch (NullPointerException NPEx){
            return -1;
        }
        config = Cconfig.getConfig();
        return fail;
    }
}
