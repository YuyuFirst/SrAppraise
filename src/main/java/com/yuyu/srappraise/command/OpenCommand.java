package com.yuyu.srappraise.command;

import com.sakurarealm.sritem.bukkit.command.SubCommand;
import com.yuyu.srappraise.SrAppraise;
import com.yuyu.srappraise.appraise.AppraiseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @BelongsProject: SrAppraise
 * @BelongsPackage: com.yuyu.srappraise.command
 * @FileName: OpenCommand
 * @Author: 峰。
 * @Date: 2024/4/19-21:14
 * @Version: 1.0
 * @Description: 用于打开gui窗口
 */
public class OpenCommand implements SubCommand {
    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length == 1){
            if (strings[0].equalsIgnoreCase("reload")){
                SrAppraise.getConfigManager().reloadConfig();
                commandSender.sendMessage(ChatColor.GREEN+"SrAppraise文件重载成功");
                return;
            }
        }

        if (strings.length < 2){
            commandSender.sendMessage(ChatColor.RED+"正确的格式为:/srappraise open 姓名 ");
            return;
        }


        Player player = Bukkit.getPlayer(strings[1]);
        if (player == null){
            commandSender.sendMessage(ChatColor.RED+"玩家"+strings[1]+"不存在!");
            return;
        }




            //为玩家打开GUI列表
            AppraiseManager.getInstance().open(player);


    }
}
