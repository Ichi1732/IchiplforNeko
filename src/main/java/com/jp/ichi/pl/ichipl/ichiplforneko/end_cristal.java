package com.jp.ichi.pl.ichipl.ichiplforneko;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class end_cristal implements CommandExecutor {

    final Plugin pl;
    end_cristal(Plugin pl){
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "使用法:/cristalrotation(cr) [セレクタ] (flash)...");
                return true;
            } else if (args[1].equals("flash")) {
                if (args.length < 8) {
                    sender.sendMessage(ChatColor.RED + "使用法:/cristalrotation(cr) [セレクタ] flash [光っている時間(tick)] [消えている時間(tick)] [持続時間(秒)] x y z...");
                    return true;
                } else {
                    if ((args.length - 5) % 3 != 0) {
                        sender.sendMessage(ChatColor.RED + "使用法:対応した３軸を設定してください");
                        return true;
                    } else {
                        //準備
                        sender.sendMessage("" + (args.length - 5));
                        sender.sendMessage("" + ((args.length - 5) / 3));
                        sender.sendMessage("" + ((args.length - 5) / 3) / 10);


                        int forrandom = (args.length - 5) / 3;
                        List<Integer> targetList = new ArrayList<>();
                        for (int i = 5; i <= args.length - 1; i++) targetList.add(Integer.parseInt(args[i]));
                        int interval = Integer.parseInt(args[2]) + Integer.parseInt(args[3]);
                        //実行
                        for (Entity entity : pl.getServer().selectEntities(sender, args[0])) {
                            if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
                                int limit = Integer.parseInt(args[4]) * 20;
                                World world = entity.getWorld();
                                EnderCrystal enderCrystal = (EnderCrystal) entity;
                                new BukkitRunnable() {
                                    int count = 0;

                                    @Override
                                    public void run() {
                                        int random = (int) (Math.random() * forrandom);
                                        Location loc = new Location(world, targetList.get(random * 3), targetList.get(random * 3 + 1), targetList.get(random * 3 + 2));
                                        enderCrystal.setBeamTarget(loc);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                enderCrystal.setBeamTarget(null);
                                            }
                                        }.runTaskLater(pl, Integer.parseInt(args[2]));
                                        count += interval;
                                        if (count >= limit) cancel();
                                    }
                                }.runTaskTimer(pl, 0, interval);
                            }
                        }
                        return true;
                    }
                }
            } else {
                if (args.length < 7) {
                    sender.sendMessage(ChatColor.RED + "使用法:/cristalrotation(cr) [セレクタ] [速度(度/秒)] [半径] [xオフセット] [yオフセット] [zオフセット] [持続時間] (回転軸) (初めの角度) (x) (y) (z)");
                    return true;
                } else {
                    for (Entity entity : pl.getServer().selectEntities(sender, args[0])) {
                        if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
                            EnderCrystal enderCrystal = (EnderCrystal) entity;

                            Location loc = enderCrystal.getLocation().add(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                            int r = Integer.parseInt(args[2]);
                            int limit = Integer.parseInt(args[6]) * 20;
                            double speed = Integer.parseInt(args[1]) / 20;

                            if(args.length>=8){
                                if(!(args[7].equals("x")||args[7].equals("y")||args[7].equals("z"))){
                                    sender.sendMessage("回転軸にはx.y.zのいずれかを設定してください。");
                                    return true;
                                }
                            }

                            new BukkitRunnable() {
                                int count = 0;
                                double lotation = 0;

                                @Override
                                public void run() {
                                    if (count == 0) {
                                        if (args.length >= 9) lotation = Integer.parseInt(args[8]);
                                    }
                                    try {
                                        switch (args[7]) {
                                            case "x":
                                                double y = Math.cos(Math.toRadians(lotation)) * r;
                                                double z = Math.sin(Math.toRadians(lotation)) * r;
                                                enderCrystal.setBeamTarget(loc.clone().add(0, y, z));
                                                break;
                                            case "y":
                                                double x = Math.cos(Math.toRadians(lotation)) * r;
                                                z = Math.sin(Math.toRadians(lotation)) * r;
                                                enderCrystal.setBeamTarget(loc.clone().add(x, 0, z));
                                                break;
                                            case "z":
                                                x = Math.cos(Math.toRadians(lotation)) * r;
                                                y = Math.sin(Math.toRadians(lotation)) * r;
                                                enderCrystal.setBeamTarget(loc.clone().add(x, y, 0));
                                                break;
                                        }
                                    }catch (IndexOutOfBoundsException IOOBEx){
                                        double x = Math.cos(Math.toRadians(lotation)) * r;
                                        double z = Math.sin(Math.toRadians(lotation)) * r;
                                        enderCrystal.setBeamTarget(loc.clone().add(x, 0, z));
                                    }
                                    lotation += speed;
                                    count++;
                                    if (count >= limit) {
                                        if (args.length >= 10) {
                                            if (args.length >= 12) {
                                                enderCrystal.setBeamTarget(new Location(enderCrystal.getWorld(), Integer.parseInt(args[9]), Integer.parseInt(args[10]), Integer.parseInt(args[11])));
                                            } else {
                                                sender.sendMessage("x,y,zの３点を記述してください");
                                            }
                                        }
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(pl, 0, 1);
                        }
                    }
                    return true;
                }
            }
        }catch (NumberFormatException NFEx){
            sender.sendMessage("数字を正しく入力してください");
            return true;
        }
    }
}
