package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class combination implements CommandExecutor , TabCompleter {
    //組み合わせと数値保存用MAP
    static private Map<String,Integer> comMap = new HashMap<>();
    static private Map<String,Integer> correctMap = new HashMap<>();

    Plugin pl;
    CustomConfig CCc;
    FileConfiguration config;

    //コンストラクタ
    public combination(Plugin pl,CustomConfig CCc){
        comMap = setComMap(CCc.getConfig());
        correctMap = correctMap(CCc.getConfig());
        this.pl = pl;
        this.CCc = CCc;
        this.config =CCc.getConfig();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        if(args.length==1){
            if(args[0].length()==0){
                return Arrays.asList("create","remove","enter","reset","info","list","flag","reload");
            }else {
                if("create".startsWith(args[0]))return Collections.singletonList("create");
                else if("remove".equals(args[0]) || "reset".startsWith(args[0])||"reload".startsWith(args[0])) return Arrays.asList("remove","reset","reload");
                else if("enter".startsWith(args[0])) return Collections.singletonList("enter");
                else if("info".startsWith(args[0])) return Collections.singletonList("info");
                else if("list".startsWith(args[0])) return Collections.singletonList("list");
                else if("flag".startsWith(args[0])) return Collections.singletonList("flag");
            }
        }else if(args.length==2){
            if(args[0].equals("remove")||args[0].equals("enter")||args[0].equals("reset")||args[0].equals("info")||args[0].equals("flag")) {
                if (args[1].length() == 0) {
                    return new ArrayList<>(config.getConfigurationSection("combination").getKeys(false));
                } else {
                    List<String> list = new ArrayList<>();
                    for (String ID : config.getConfigurationSection("combination").getKeys(false)) {
                        if (ID.startsWith(args[1])) {
                            list.add(ID);
                        }
                    }
                    return list;
                }
            }
        }else if(args.length==3){
            if(args[0].equals("flag")){
                if(args[2].length()==0){
                    return Arrays.asList("location","material","combination");
                }else {
                    if("location".startsWith(args[2]))return Collections.singletonList("location");
                    else if("material".equals(args[2]))return Collections.singletonList("material");
                    else if("combination".equals(args[2]))return Collections.singletonList("combination");
                }
            }
        }
        return Collections.singletonList("");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length>=5){
            //組み合わせ型パスワードがあるか
            if(config.getConfigurationSection("combination")==null){
                sender.sendMessage(ChatColor.RED+"組み合わせ型パスワードは現在登録されていません");
                return true;
            }
            if(config.getConfigurationSection("combination").getKeys(false).contains(args[1])){
                if(args[2].equals("location")){
                    //X Y Z があるか
                    if(args.length==6){
                        Integer x=0;
                        Integer y=0;
                        Integer z=0;
                        try {
                            x = Integer.parseInt(args[3]);
                            y = Integer.parseInt(args[4]);
                            z = Integer.parseInt(args[5]);
                        }catch (NumberFormatException NFEx){
                            sender.sendMessage(ChatColor.RED+"X Y Zを正しく入力してください。");
                            return true;
                        }
                        config.set("combination."+args[1]+".location.x",x);
                        config.set("combination."+args[1]+".location.y",y);
                        config.set("combination."+args[1]+".location.z",z);
                        config.set("combination."+args[1]+".location.world",((Player)sender).getLocation().getWorld().getName());
                        CCc.saveConfig();
                        sender.sendMessage("正常に位置が登録されました");
                        return true;
                    }else {
                        sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID position X Y Z");
                    }

                }else if(args[2].equals("combination")){
                    //パスワード登録
                    config.set("combination."+args[1]+".pass",null);
                    for(int i =3;i<=args.length-1;i++){
                        try {
                            Integer pass = Integer.parseInt(args[i]);
                            if(pass<1||9<pass){
                                sender.sendMessage(ChatColor.RED+"パスワードは1-9の間で設定してください");
                                correctMap(config);
                                return true;
                            }
                            config.set("combination." + args[1] + ".pass." + (i - 2), pass);
                        }catch (NumberFormatException NFEx){
                            sender.sendMessage(ChatColor.RED+"正常に登録されませんでした:"+(i-2)+"番目");
                            return true;
                        }
                    }
                    CCc.saveConfig();
                    correctMap(config,args[1]);
                    sender.sendMessage("パスワードが登録されました。");
                    return true;
                }

            }else {
                sender.sendMessage(ChatColor.RED+"そのような組み合わせ型パスワードは存在しません:"+args[1]);
                return true;
            }



        } else if(args.length==4){
            if(args[0].equals("flag")){
                //組み合わせ型パスワードがあるか
                if(config.getConfigurationSection("combination")==null){
                 sender.sendMessage(ChatColor.RED+"組み合わせ型パスワードは現在登録されていません");
                 return true;
                }

                //args[1]があるか
                if(config.getConfigurationSection("combination").getKeys(false).contains(args[1])) {
                    if (args[2].equals("location")) {
                        if (args[3].equals("here")) {
                            try {
                                config.set("combination." + args[1] + ".location.x", ((Player) sender).getLocation().getBlockX());
                                config.set("combination." + args[1] + ".location.y", ((Player) sender).getLocation().getBlockY());
                                config.set("combination." + args[1] + ".location.z", ((Player) sender).getLocation().getBlockZ());
                                config.set("combination." + args[1] + ".location.world", (((Player) sender).getLocation().getWorld().getName()));
                                CCc.saveConfig();
                                sender.sendMessage("正常に位置が登録されました");
                                return true;
                            }catch (Exception Ex){
                                sender.sendMessage(ChatColor.RED+"flag locationオプションはプレイヤー専用です");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID location X Y Z");
                            return true;
                        }
                    } else if (args[2].equals("material")) {
                        for(Material material:Material.values()){
                            if(material.name().equals(args[3].toUpperCase())){
                                config.set("combination." + args[1] + ".material", args[3].toUpperCase());
                                CCc.saveConfig();
                                sender.sendMessage("ブロックが正常に登録されました");
                                return true;
                            }
                        }
                        sender.sendMessage(ChatColor.RED+"そのようなブロックは存在しません");
                        return true;
                    } else if (args[2].equals("combination")) {
                        config.set("combination."+args[1]+".pass",null);
                        try {
                            Integer pass = Integer.parseInt(args[3]);
                            config.set("combination."+args[1]+".pass.1",pass);
                            if(pass<1||9<pass){
                                sender.sendMessage(ChatColor.RED+"パスワードは1-9の間で設定してください");
                                correctMap(config);
                                return true;
                            }
                        }catch (NumberFormatException NFEx){
                            sender.sendMessage(ChatColor.RED+"正常に登録されませんでした:1番目");
                        }
                        CCc.saveConfig();
                        correctMap(config,args[1]);
                        sender.sendMessage("パスワードが正常に登録されました");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID <location|material|combination>...");
                        return true;
                    }
                }else {
                    sender.sendMessage(ChatColor.RED+"そのような組み合わせ型パスワードは存在しません:"+args[1]);
                    return true;
                }
            }
        } else if(args.length==3){
            if(args[0].equals("flag")){
                if(args[2].equals("location")) {
                    sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID <here|X Y Z>");
                    return true;
                }else if(args[2].equals("material")) {
                    sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID material <材質>");
                }else if(args[2].equals("combination")){
                    sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID combination <内容>");
                }else{
                    sender.sendMessage(ChatColor.RED + "使用法:/combination flag ID <location|material|combination>...");
                    return true;
                }
            }else if(args[0].equals("enter")){
                if(config.getConfigurationSection("combination").getKeys(false).contains(args[1])&&comMap.containsKey(args[1])){
                    try {
                        comEnter(args[1], Integer.parseInt(args[2]), config, sender);
                        return true;
                    }catch (NumberFormatException NFEx){
                        sender.sendMessage(ChatColor.RED+"ターンを正しく指定してください");
                        return true;
                    }
                }else {
                    sender.sendMessage(ChatColor.RED+"そのような組み合わせ型パスワードは存在しません。");
                    return true;
                }
            }
        }else if(args.length==2){
            if(args[0].equals("flag")){
                sender.sendMessage(ChatColor.RED+ "使用法:/combination flag ID <location|material|combination>...");
                return true;
            }else if(args[0].equals("enter")){
                sender.sendMessage(ChatColor.RED+"使用法:/combination entr ID <順番>");
                return true;
            } else if(args[0].equals("remove")||args[0].equals("reset")||args[0].equals("create")||args[0].equals("info")){
                switch (args[0]){
                    case "remove":
                        if(config.getConfigurationSection("combination").getKeys(false).contains(args[1])){
                            config.set("combination",null);
                            comMap.remove(args[1]);
                            CCc.saveConfig();
                            sender.sendMessage("組み合わせ型パスワードが正常に削除されました。");
                        }else {
                            sender.sendMessage(ChatColor.RED+"そのような組み合わせ型パスワードは存在しません。");
                        }
                        return true;
                    case "reset":
                        if(config.getConfigurationSection("combination").getKeys(false).contains(args[1])&&comMap.containsKey(args[1])){
                            comMap.replace(args[1],0);
                            sender.sendMessage(args[1]+"が正常にリセットされました。");
                        }else {
                            sender.sendMessage(ChatColor.RED+"そのような組み合わせ型パスワードは存在しません。");
                        }
                        return true;
                    case "create":
                        config.set("combination."+args[1],"");
                        CCc.saveConfig();
                        comMap.put(args[1],0);
                        sender.sendMessage("組み合わせ型パスワードが正常に登録されました。");
                        return true;
                    case "info":
                        try {
                            if (config.getConfigurationSection("combination").getKeys(false).contains(args[1]) && comMap.containsKey(args[1])) {
                                sender.sendMessage("ID:"+args[1]);
                                for(String key:config.getConfigurationSection("combination."+args[1]+".pass").getKeys(false)){
                                    sender.sendMessage(" "+key+"番目:"+config.getString("combination."+args[1]+".pass."+key));


                                }


                                for(String key:config.getConfigurationSection("combination."+args[1]).getKeys(false)){
                                    if(key.equals("location")){
                                        sender.sendMessage(" X:"+config.getString("combination."+args[1]+".location.x"));
                                        sender.sendMessage(" Y:"+config.getString("combination."+args[1]+".location.y"));
                                        sender.sendMessage(" Z:"+config.getString("combination."+args[1]+".location.z"));
                                    }else if(! key.equals("pass")){
                                        sender.sendMessage(" "+key+":"+config.getString("combination."+args[1]+"."+key));
                                    }
                                }
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "そのような組み合わせ型パスワードは存在しません。");
                                return true;
                            }
                        }catch (NullPointerException NPEx){
                            sender.sendMessage(ChatColor.RED+"組み合わせ型パスワードが登録されていません。");
                            return true;
                        }
                }
            }
        }else if(args.length==1){
            if(args[0].equals("list")){
                try {
                    sender.sendMessage(ChatColor.DARK_AQUA+"登録されている組み合わせ型パスワードの一覧");
                    for (String key : config.getConfigurationSection("combination").getKeys(false)) {
                        sender.sendMessage(" "+key);
                    }
                    return true;
                }catch (NullPointerException NPEx){
                    sender.sendMessage(ChatColor.RED+"組み合わせ型パスワードが登録されていません。");
                    return true;
                }
            } else if(args[0].equals("reload")){
                CCc.reloadConfig();
                setComMap(config);
                correctMap(config);
                sender.sendMessage("組み合わせ型パスワードのコンフィグがリロードされました");
                return true;
            } else if(args[0].equals("remove")||args[0].equals("reset")||args[0].equals("create")||args[0].equals("info")){
                sender.sendMessage(ChatColor.RED+"使用法:/combination "+args[0]+" <ID>");
                return true;
            } else if(args[0].equals("enter")||args[0].equals("flag")){
                sender.sendMessage(ChatColor.RED+"使用法:/combination "+args[0]+" <ID>...");
                return true;
            }
        }else if(args.length==0){
            combinationHelp(sender);
            return true;
        }
        sender.sendMessage(ChatColor.RED+"使用法:/combination <create|remove|enter|flag|reset|info|list>...");
        return true;

    }

    private void combinationHelp(CommandSender sender){
        sender.sendMessage(ChatColor.DARK_AQUA+"===================combination Help=========================");
        sender.sendMessage(ChatColor.DARK_AQUA+"create ID");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードを作成する");
        sender.sendMessage(ChatColor.DARK_AQUA+"remove ID");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードを削除する");
        sender.sendMessage(ChatColor.DARK_AQUA+"enter ID 番号");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワード入力");
        sender.sendMessage(ChatColor.DARK_AQUA+"reset ID");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードの入力をリセットする");
        sender.sendMessage(ChatColor.DARK_AQUA+"reload");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードのコンフィグをリロードする");
        sender.sendMessage(ChatColor.DARK_AQUA+"  現在入力中のパスワードもリセットされるので注意");
        sender.sendMessage(ChatColor.DARK_AQUA+"info ID");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードの詳細情報を表示する");
        sender.sendMessage(ChatColor.DARK_AQUA+"list");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードの一覧を表示する");
        sender.sendMessage(ChatColor.DARK_AQUA+"flag ID...");
        sender.sendMessage(ChatColor.DARK_AQUA+"  組み合わせ型パスワードのフラグを変更する");
        sender.sendMessage(ChatColor.DARK_AQUA+"  ・ location <X Y Z|here>");
        sender.sendMessage(ChatColor.DARK_AQUA+"     パスワードが合致した際にブロックを置く座標");
        sender.sendMessage(ChatColor.DARK_AQUA+"  ・ material [ブロックの種類]");
        sender.sendMessage(ChatColor.DARK_AQUA+"     パスワードが合致した際に置くブロックの種類");
        sender.sendMessage(ChatColor.DARK_AQUA+"  ・ combination 回数 回数...");
        sender.sendMessage(ChatColor.DARK_AQUA+"     組み合わせ型パスワード");
        sender.sendMessage(ChatColor.DARK_AQUA+"     回数は１～９の範囲で指定");
        sender.sendMessage(ChatColor.DARK_AQUA+"============================================================");

    }

    private void comEnter(String ID,Integer turn,FileConfiguration config,CommandSender sender){
        Integer integer = comMap.get(ID);
        if(integer!=-1){
            if(turn==1){
                comMap.replace(ID,integer+1);
                if(comMap.get(ID)>=10){
                    sender.sendMessage("失敗");
                    comMap.replace(ID,-1);
                    return;
                }
            }else {
                if((Math.floor(integer/Math.pow(10,turn-2)))%10== config.getInt("combination."+ID+".pass."+(turn-1),10)){
                    comMap.replace(ID,(int) (integer+Math.pow(10, turn-1)));
                    if(comMap.get(ID)>=Math.pow(10,turn)){
                        sender.sendMessage("失敗");
                        comMap.replace(ID,-1);
                        return;
                    }
                }else {
                    sender.sendMessage("失敗");
                    comMap.replace(ID,-1);
                    return;
                }
            }
        }else {
            sender.sendMessage("入力拒否");
            return;
        }

        if(comMap.get(ID).equals(correctMap.get(ID))){
            try {
                World world = Bukkit.getWorld(config.getString("combination." + ID + ".location.world"));
                Location location = new Location(world, config.getDouble("combination." + ID + ".location.x"), config.getDouble("combination." + ID + ".location.y"), config.getDouble("combination." + ID + ".location.z"));
                location.getBlock().setType(Material.getMaterial(config.getString("combination." + ID + ".material")));
                sender.sendMessage("解除");
                comMap.replace(ID, -1);
                return;
            }catch (Exception Ex){
                pl.getLogger().warning("ブロックが設置できませんでした");
            }
        }
        sender.sendMessage("解答"+correctMap.get(ID));
        return;
    }

    private Map<String,Integer> setComMap(FileConfiguration config){
        Map<String,Integer> map = new HashMap<>();
        if(config.getConfigurationSection("combination")==null)return null;
        for(String string:config.getConfigurationSection("combination").getKeys(false)){
            map.put(string,0);
        }
        return map;
    }

    private Map<String,Integer> correctMap(FileConfiguration config){
        Map<String,Integer> map = new HashMap<>();
        if(config.getConfigurationSection("combination")==null) return null;
        for(String key:config.getConfigurationSection("combination").getKeys(false)){
            Integer correct = 0;
            try {
                for (String passNum : config.getConfigurationSection("combination." + key + ".pass").getKeys(false)) {
                    Integer passNumint = Integer.parseInt(passNum);
                    passNumint = (int) Math.pow(10d, passNumint - 1);
                    passNumint = passNumint * config.getInt("combination." + key + ".pass." + passNum);
                    correct += passNumint;
                }
                map.put(key, correct);
            }catch (NullPointerException NPEx){}
        }
        return map;
    }

    private void correctMap(FileConfiguration config,String ID){
            Integer correct = 0;
            for(String passNum:config.getConfigurationSection("combination."+ID+".pass").getKeys(false)){
                Integer passNumint = Integer.parseInt(passNum);
                passNumint =  (int) Math.pow(10d,passNumint-1);
                passNumint = passNumint*config.getInt("combination."+ID+".pass."+passNum);
                correct +=passNumint;
            }
            correctMap.put(ID,correct);
        return;
    }



}
