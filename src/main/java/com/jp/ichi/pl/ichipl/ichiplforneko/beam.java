package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;

public class beam implements CommandExecutor , TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        if(args.length==0||args.length==1){
            return null;
        }
        return Collections.singletonList("");
    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length==4){
            int renge = 0;
            double thickness = 0.5;
            int count = 1;
            try {
                renge = Integer.parseInt(args[1]);
                thickness = Double.parseDouble(args[2]);
                count = Integer.parseInt(args[3]);
            }catch (NumberFormatException NFEx){
                sender.sendMessage(ChatColor.RED+"数字を入力してください");
                return true;
            }
            List<Entity> entityList = Bukkit.selectEntities(sender,args[0]);


            for(Entity entity:entityList){
                Location loc = entity.getLocation();
                //横回転
                float yaw = loc.getYaw();
                //縦回転
                float pitch = loc.getPitch();
                double x =0;
                double y =0;
                double z=0;

                for(double i=0;i<=renge;i+=0.2){
                    x = Math.sin(Math.toRadians(yaw))*i;
                    y = Math.sin(Math.toRadians(pitch))*i*-1;
                    z = Math.cos(Math.toRadians(yaw))*i;
                    entity.getWorld().spawnParticle(Particle.FLAME,loc.clone().getX()-x,loc.getY()+y+1.5,loc.clone().getZ()+z,count,thickness,thickness,thickness,0);
                }
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED+"使用法:/beam <エンティティ> <距離> <太さ> <数>");
        return true;
    }
}
