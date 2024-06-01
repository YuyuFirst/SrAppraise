package com.yuyu.srappraise;


import com.sakurarealm.sritem.bukkit.command.CommandBase;
import com.yuyu.srappraise.command.OpenCommand;
import com.yuyu.srappraise.config.ConfigManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class SrAppraise extends JavaPlugin {

    @Getter
    private static SrAppraise instance;//用于获取实例
    @Getter
    private static ConfigManager configManager;//用于鉴定
    @Override
    public void onEnable() {

        getLogger().info(ChatColor.MAGIC+"鉴定插件启动");
        //加载配置文件
        this.configManager = loadConfig();

        //注册指令
        CommandBase commandBase = new CommandBase();
        this.getCommand("srappraise").setExecutor(commandBase);
        commandBase.registerSubCommand("open",new OpenCommand());
        commandBase.registerSubCommand("reload",new OpenCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigManager loadConfig(){
        ConfigManager configManager1 = new ConfigManager(this);
        return configManager1;
    }

}
