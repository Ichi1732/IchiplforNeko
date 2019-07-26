package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ChangeCommandColor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

        if(!(sender instanceof BlockCommandSender)){
            sender.sendMessage(ChatColor.RED+ "使用法:/changecolor(cc) (do:コマンドを実行する。任意) [書き換えるコマンド]");
            sender.sendMessage(ChatColor.RED+"コマンドブロック専用コマンドです。");
            return true;
        }

        if(args.length==0){
            sender.sendMessage(ChatColor.RED+"書き換えるコマンドを入力してください");
            return true;
        }else {
            List<String> list = new ArrayList<>();


            for(String string:args){
                if(string!=null) {
                    string = string.replace("&0", ChatColor.BLACK.toString());
                    string = string.replace("&1", ChatColor.DARK_BLUE.toString());
                    string = string.replace("&2", ChatColor.DARK_GREEN.toString());
                    string = string.replace("&3", ChatColor.DARK_AQUA.toString());
                    string = string.replace("&4", ChatColor.DARK_RED.toString());
                    string = string.replace("&5", ChatColor.DARK_PURPLE.toString());
                    string = string.replace("&6", ChatColor.GOLD.toString());
                    string = string.replace("&7", ChatColor.GRAY.toString());
                    string = string.replace("&8", ChatColor.DARK_GRAY.toString());
                    string = string.replace("&9", ChatColor.BLUE.toString());
                    string = string.replace("&a", ChatColor.GREEN.toString());
                    string = string.replace("&b", ChatColor.AQUA.toString());
                    string = string.replace("&c", ChatColor.RED.toString());
                    string = string.replace("&d", ChatColor.LIGHT_PURPLE.toString());
                    string =string.replace("&e", ChatColor.YELLOW.toString());
                    string = string.replace("&f", ChatColor.WHITE.toString());
                    string = string.replace("&l", ChatColor.BOLD.toString());
                    string = string.replace("&m", ChatColor.STRIKETHROUGH.toString());
                    string = string.replace("&n", ChatColor.UNDERLINE.toString());
                    string = string.replace("&o", ChatColor.ITALIC.toString());
                    string = string.replace("&r", ChatColor.RESET.toString());
                    list.add(string);
                }
            }

            BlockCommandSender commandSender = (BlockCommandSender) sender;
           CommandBlock commandBlock =  (CommandBlock) commandSender.getBlock().getState();

           boolean isdo = false;
            if(list.get(0).equalsIgnoreCase("do")){
                list.remove(0);
                isdo = true;
            }
            if(list.size()!=0) {
                if (list.get(0).equalsIgnoreCase("give") || list.get(0).equalsIgnoreCase("/give")) {
                    list.set(0, "minecraft:give");
                }
            }else {
                sender.sendMessage(ChatColor.RED+"書き換えるコマンドを入力してください。");
                return true;
            }

           String command ="";
           for(String str:list){
               command += str+" ";
           }
           commandBlock.setCommand(command);
           commandBlock.update();

           if(isdo){
               Bukkit.dispatchCommand(sender,command);
           }


            return true;

        }
    }
}
