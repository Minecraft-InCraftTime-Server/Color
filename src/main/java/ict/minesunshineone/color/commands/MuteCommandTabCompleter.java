package ict.minesunshineone.color.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ict.minesunshineone.color.utils.TimeUtils;

public class MuteCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("mute")) {
            if (args.length == 1) {
                // 补全在线玩家名字
                String partialPlayerName = args[0].toLowerCase();
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partialPlayerName))
                        .collect(Collectors.toList()));
            } else if (args.length == 2) {
                // 补全时间单位
                String partialTime = args[1].toLowerCase();
                for (String timeUnit : TimeUtils.getTimeUnits()) {
                    if (timeUnit.startsWith(partialTime)) {
                        completions.add(timeUnit);
                    }
                }
            } else if (args.length == 3) {
                // 补全常用禁言理由
                completions.add("违规发言");
                completions.add("刷屏");
                completions.add("辱骂他人");
                completions.add("广告");
            }
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            if (args.length == 1) {
                // 补全在线玩家名字
                String partialPlayerName = args[0].toLowerCase();
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partialPlayerName))
                        .collect(Collectors.toList()));
            }
        }

        return completions;
    }
}
