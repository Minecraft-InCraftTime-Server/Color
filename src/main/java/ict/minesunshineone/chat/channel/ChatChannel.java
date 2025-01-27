package ict.minesunshineone.chat.channel;

import org.bukkit.entity.Player;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public interface ChatChannel {

    /**
     * 获取频道名称
     *
     * @return 频道名称
     */
    String getName();

    /**
     * 获取频道显示名称
     *
     * @return 频道显示名称
     */
    Component getDisplayName();

    /**
     * 检查玩家是否可以加入该频道
     *
     * @param player 玩家
     * @return 是否可以加入
     */
    boolean canJoin(Player player);

    /**
     * 处理聊天事件
     *
     * @param event 聊天事件
     */
    void handleChat(AsyncChatEvent event);

    /**
     * 获取频道权限节点
     *
     * @return 权限节点
     */
    String getPermission();

    /**
     * 获取频道描述
     *
     * @return 频道描述
     */
    Component getDescription();

    /**
     * 获取频道格式
     *
     * @return 频道格式
     */
    String getFormat();

    /**
     * 是否支持跨服聊天
     *
     * @return 是否支持跨服
     */
    boolean isCrossServer();
}
