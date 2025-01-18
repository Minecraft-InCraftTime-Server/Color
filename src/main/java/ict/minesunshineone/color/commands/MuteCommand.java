package ict.minesunshineone.color.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ict.minesunshineone.color.managers.MuteManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MuteCommand implements CommandExecutor {

    private final MuteManager muteManager;

    public MuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mute")) {
            if (args.length < 3) {
                sender.sendMessage("用法: /mute <玩家> <时长(分钟)> <原因>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("找不到该玩家");
                return true;
            }

            try {
                long duration = Long.parseLong(args[1]) * 60 * 1000; // 转换为毫秒
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }

                muteManager.mutePlayer(target, duration, reason.toString().trim());

                Component message = Component.text("你已被管理员禁言！")
                        .color(TextColor.color(255, 85, 85))
                        .append(Component.newline())
                        .append(Component.text("原因: " + reason.toString().trim()))
                        .append(Component.newline())
                        .append(Component.text("时长: " + args[1] + "分钟"));

                target.sendMessage(message);
                sender.sendMessage("已禁言玩家 " + target.getName());
            } catch (NumberFormatException e) {
                sender.sendMessage("无效的时长");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            if (args.length < 1) {
                sender.sendMessage("用法: /unmute <玩家>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("找不到该玩家");
                return true;
            }

            muteManager.unmutePlayer(target);
            target.sendMessage(Component.text("你的禁言已被解除！").color(TextColor.color(85, 255, 85)));
            sender.sendMessage("已解除玩家 " + target.getName() + " 的禁言");
            return true;
        }
        return false;
    }
}
