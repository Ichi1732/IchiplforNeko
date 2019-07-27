package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class note implements Listener, CommandExecutor, TabCompleter {

    private Map<String, BoundingBox> noteLoc = new HashMap<>();
    final private Plugin pl;
    final private CustomConfig CCn;
    private FileConfiguration config;
    public note(Plugin pl,CustomConfig customConfig){
        pl.getServer().getPluginManager().registerEvents(this,pl);
        this.pl = pl;
        CCn = customConfig;
        config = CCn.getConfig();

        int fail=posreload();

        if(fail==-1){
            for(Player player:pl.getServer().getOnlinePlayers()){
                if(player.hasPermission("ichipl.fkc"))player.sendMessage(ChatColor.RED.toString()+"ノートブロックエリアは現在登録されていません");
            }
        } else if(fail>0){
            for(Player player:pl.getServer().getOnlinePlayers()){
                if(player.hasPermission("ichipl.fkc"))player.sendMessage(ChatColor.RED.toString()+fail+"個のノートブロックエリアの正規化に失敗しました。");
            }
        }



    }

    @EventHandler
    public void noteblock_event(NotePlayEvent e){
        Location location = e.getBlock().getLocation();
        for(String key:noteLoc.keySet()){
            if(location.getWorld().getName().equals(config.getString("location."+key+".world"))) {
                BoundingBox pos = noteLoc.get(key);
                if(pos.contains(location.getBlockX(),location.getBlockY(),location.getBlockZ())){
                    location.getWorld().playSound((new Location(location.getWorld(),config.getInt("location."+key+".to.x"),config.getInt("location."+key+".to.y"),config.getInt("location."+key+".to.z"))), Sound.valueOf("BLOCK_NOTE_BLOCK_"+toSound(e.getInstrument().toString())),(float) config.getDouble("location."+key+".vol",1),topitch(e.getNote()));
                    if(config.getBoolean("location."+key+".cancel")){
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
            sender.sendMessage(ChatColor.RED+"使用法:/movesound add|remove|list");
            return true;
        }else if(args[0].equals("add")){
            if(args.length<11){
                sender.sendMessage(ChatColor.RED+"使用法:/movesound add 名前 X Y Z X2 Y2 Z2 X3 Y3 Z3 (ボリューム) (true)");
                return true;
            }
            else {
                Player player = (Player) sender;
                config.set("location."+args[1]+".world",player.getLocation().getWorld().getName());
                try{
                    //感知範囲
                    BoundingBox pos = new BoundingBox(Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]),Double.parseDouble(args[5]),Double.parseDouble(args[6]),Double.parseDouble(args[7]));
                    config.set("location."+args[1]+".pos",pos);
                    //移動
                    config.set("location."+args[1]+".to.x",Integer.parseInt(args[8]));
                    config.set("location."+args[1]+".to.y",Integer.parseInt(args[9]));
                    config.set("location."+args[1]+".to.z",Integer.parseInt(args[10]));
                    config.set("location."+args[1]+".pos",pos);
                    if(args.length>=12){
                        config.set("location."+args[1]+".vol",Double.parseDouble(args[11]));
                        if(args.length>=13) {
                            if (args[12].equalsIgnoreCase("true")) {
                                config.set("location." + args[1] + ".cancel", true);
                            }
                        }
                    }
                    CCn.saveConfig();
                    posreload();
                }catch (NumberFormatException NFEx){
                    sender.sendMessage(ChatColor.RED+"使用法:/movesound add 名前 X Y Z X2 Y2 Z2 (true)");
                    CCn.reloadConfig();
                    posreload();
                    return true;
                }
                sender.sendMessage("ノートブロックエリアが正常に登録されました");
                return true;
            }
        }else if(args[0].equals("reload")){
            CCn.reloadConfig();
            config = CCn.getConfig();
            int fail = posreload();
            if(fail==-1){
                sender.sendMessage(ChatColor.RED+"ノートブロックエリアは現在登録されていません");
                return true;
            } else if(fail>0){
                sender.sendMessage(ChatColor.RED.toString()+fail+"個のノートブロックエリアの正規化に失敗しました。");
                return true;
            }else {
                sender.sendMessage(ChatColor.AQUA+"ノートブロックエリア群が正常にリロードされました。");
                return true;
            }
        } else if(args[0].equals("remove")){
            try {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "使用法:/movesound remove 名前");
                    return true;
                } else {
                    if (config.getConfigurationSection("location").getKeys(false).contains(args[1])) {
                        config.set("location." + args[1], null);
                        CCn.saveConfig();
                        posreload();
                        sender.sendMessage(ChatColor.AQUA + "ノートブロックエリアが正常に削除されました。");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "そのようなノートブロックエリアは登録されていません。");
                        return true;
                    }
                }
            }catch (NullPointerException NPEx){
                sender.sendMessage(ChatColor.RED+"ノートブロックエリアは現在登録されていません");
                return true;
            }
        }else if(args[0].equals("list")){
            try {
                Set<String> keys = config.getConfigurationSection("location").getKeys(false);
                if (keys.size() != 0) {
                    sender.sendMessage(ChatColor.AQUA + "登録されているノートブロックエリア名");
                    for (String name : keys) {
                        sender.sendMessage(ChatColor.AQUA + name);
                    }
                } else sender.sendMessage(ChatColor.AQUA + "ノートブロックエリアは現在登録されていません");
                return true;
            }catch (NullPointerException NPEx){
                sender.sendMessage(ChatColor.RED+"ノートブロックエリアは現在登録されていません");
                return true;
            }
        }else {
            sender.sendMessage("使用法:/movesound add|remove|list|reload");
            return true;
        }
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

        noteLoc.clear();
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
                        noteLoc.put(name, pos);
                    } catch (NullPointerException NPEx) {
                        fail++;
                    }
                } else fail++;
            }
        }catch (NullPointerException NPEx){
            return -1;
        }
        config = CCn.getConfig();
        return fail;
    }

    private String toSound(String instument){
        switch (instument){
            case "BASS_DRUM":
                return "BASEDRUM";
            case "BASS_GUITAR":
                return "BASS";
            case "STICKS":
                return "HAT";
            case "SNARE_DRUM":
                return "SNARE";
            case "PIANO":
                return "PLING";
            default:
                return instument;
        }
    }


    private float topitch(Note note){
        int Octave = note.getOctave();
        boolean isSharped = note.isSharped();
        String Tone = note.getTone().toString();
        int count = 0;
        switch (Tone){
            case "F":
                //F
                count=-1;
                break;
            case "G":
                //G
                count=1;
                break;
            case "A":
                //A
                count=3;
                break;
            case "B":
                //B
                count=5;
                break;
            case "C":
                //C
                count=6;
                break;
            case "D":
                //D
                count=8;
                break;
            case "E":
                //E
                count=10;
                break;
        }

        count= count+Octave*12;
        if(isSharped) count++;
        else if(Tone.equals("F"))count +=12;


        switch (count){
            case 0:
                return 0.50f;
            case 1:
                return 0.53f;
            case 2:
                return 0.56f;
            case 3:
                return 0.59f;
            case 4:
                return 0.63f;
            case 5:
                return 0.67f;
            case 6:
                return 0.70f;
            case 7:
                return 0.75f;
            case 8:
                return 0.80f;
            case 9:
                return 0.84f;
            case 10:
                return 0.89f;
            case 11:
                return 0.94f;
            case 12:
                return 1.00f;
            case 13:
                return 1.06f;
            case 14:
                return 1.12f;
            case 15:
                return 1.19f;
            case 16:
                return 1.26f;
            case 17:
                return 1.33f;
            case 18:
                return 1.4f;
            case 19:
                return 1.50f;
            case 20:
                return 1.59f;
            case 21:
                return 1.68f;
            case 22:
                return 1.78f;
            case 23:
                return 1.89f;
            case 24:
                return 2.00f;
            default:
                return 1.0f;
        }
    }


}
