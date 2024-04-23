package com.yuyu.srappraise.command;

import com.sakurarealm.sritem.bukkit.command.SubCommand;
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

        if (strings.length < 3){
            commandSender.sendMessage(ChatColor.RED+"正确的格式为:/srappraise open 姓名 GUIname");
            return;
        }


        Player player = Bukkit.getPlayer(strings[1]);
        if (player == null){
            commandSender.sendMessage(ChatColor.RED+"玩家"+strings[2]+"不存在!");
            return;
        }

        String gui = strings[2];

        if (gui.equalsIgnoreCase("appraise")){
            //为玩家打开GUI列表
            AppraiseManager.getInstance().open(player,gui);
        }else {
            commandSender.sendMessage(ChatColor.RED+"请输入正确的GUI名字");
        }

    }
}
