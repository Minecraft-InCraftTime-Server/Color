package ict.minesunshineone.chat.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class TimeUtils {

    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d+)(s|m|h|d|w|mo|y)$");

    public static long parseDuration(String input) throws IllegalArgumentException {
        Matcher matcher = TIME_PATTERN.matcher(input.toLowerCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("无效的时间格式");
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" ->
                value * 1000;               // 秒转毫秒
            case "m" ->
                value * 60 * 1000;          // 分钟转毫秒
            case "h" ->
                value * 60 * 60 * 1000;     // 小时转毫秒
            case "d" ->
                value * 24 * 60 * 60 * 1000; // 天转毫秒
            case "w" ->
                value * 7 * 24 * 60 * 60 * 1000; // 周转毫秒
            case "mo" ->
                value * 30 * 24 * 60 * 60 * 1000L; // 月转毫秒(按30天计算)
            case "y" ->
                value * 365 * 24 * 60 * 60 * 1000L; // 年转毫秒(按365天计算)
            default ->
                throw new IllegalArgumentException("无效的时间单位");
        };
    }

    public static String formatDuration(long milliseconds) {
        if (milliseconds < 60000) { // 小于1分钟，显示秒
            return (milliseconds / 1000) + "秒";
        }

        StringBuilder result = new StringBuilder();

        // 年
        long years = milliseconds / (365 * 24 * 60 * 60 * 1000L);
        if (years > 0) {
            result.append(years).append("年");
            milliseconds %= (365 * 24 * 60 * 60 * 1000L);
        }

        // 月
        long months = milliseconds / (30 * 24 * 60 * 60 * 1000L);
        if (months > 0) {
            result.append(months).append("个月");
            milliseconds %= (30 * 24 * 60 * 60 * 1000L);
        }

        // 周
        long weeks = milliseconds / (7 * 24 * 60 * 60 * 1000L);
        if (weeks > 0) {
            result.append(weeks).append("周");
            milliseconds %= (7 * 24 * 60 * 60 * 1000L);
        }

        // 天
        long days = milliseconds / (24 * 60 * 60 * 1000);
        if (days > 0) {
            result.append(days).append("天");
            milliseconds %= (24 * 60 * 60 * 1000);
        }

        // 小时
        long hours = milliseconds / (60 * 60 * 1000);
        if (hours > 0) {
            result.append(hours).append("小时");
            milliseconds %= (60 * 60 * 1000);
        }

        // 分钟
        long minutes = milliseconds / (60 * 1000);
        if (minutes > 0) {
            result.append(minutes).append("分钟");
            milliseconds %= (60 * 1000);
        }

        // 秒
        long seconds = milliseconds / 1000;
        if (seconds > 0 || result.length() == 0) {
            result.append(seconds).append("秒");
        }

        return result.toString();
    }

    public static String[] getTimeUnits() {
        return new String[]{
            "1y", // 年
            "1mo", // 月
            "1w", // 周
            "1d", "2d", // 天
            "12h", "1h", // 小时
            "15m", "1m", // 分钟
            "1s" // 秒
        };
    }

    public static Component getTimeFormatHelpMessage() {
        return Component.text("无效的时间格式! 正确格式示例: ")
                .color(TextColor.color(255, 170, 0))
                .append(Component.newline())
                .append(Component.text("1s = 1秒").color(TextColor.color(255, 85, 85)))
                .append(Component.newline())
                .append(Component.text("1m = 1分钟").color(TextColor.color(255, 85, 85)))
                .append(Component.newline())
                .append(Component.text("1h = 1小时").color(TextColor.color(255, 85, 85)))
                .append(Component.newline())
                .append(Component.text("1d = 1天").color(TextColor.color(255, 85, 85)))
                .append(Component.newline())
                .append(Component.text("1w = 1周").color(TextColor.color(255, 85, 85)))
                .append(Component.newline())
                .append(Component.text("1mo = 1个月").color(TextColor.color(255, 85, 85)))
                .append(Component.newline())
                .append(Component.text("1y = 1年").color(TextColor.color(255, 85, 85)));
    }
}
