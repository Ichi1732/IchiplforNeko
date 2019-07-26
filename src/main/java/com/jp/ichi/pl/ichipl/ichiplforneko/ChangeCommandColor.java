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
                    ChatColor.translateAlternateColorCodes('&',string);
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
