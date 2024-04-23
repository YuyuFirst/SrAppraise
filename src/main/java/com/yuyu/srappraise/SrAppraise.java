package com.yuyu.srappraise;

import com.sakurarealm.sritem.bukkit.command.CommandBase;
import com.yuyu.srappraise.command.OpenCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class SrAppraise extends JavaPlugin {

    @Getter
    private static SrAppraise instance;//用于获取实例
    @Override
    public void onEnable() {

        getLogger().info(ChatColor.MAGIC+"鉴定插件启动");

        CommandBase commandBase = new CommandBase();
        this.getCommand("srappraise").setExecutor(commandBase);
        commandBase.registerSubCommand("open",new OpenCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
