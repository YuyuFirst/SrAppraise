package com.yuyu.srappraise.appraise;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.sakurarealm.sritem.germ.GermGuiService;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @BelongsProject: SrAppraise
 * @BelongsPackage: com.yuyu.srappraise
 * @FileName: AppraiseManager
 * @Author: 峰。
 * @Date: 2024/4/16-16:37
 * @Version: 1.0
 * @Description:提供鉴定服务相关
 */
public class AppraiseManager {

    @Getter
    private final static AppraiseManager instance = new AppraiseManager();

    private AppraiseManager() {
    }

    public static ConfigurationSection getForgePlayerInventory() {
        return GermGuiService.getGermGui("appraise", "player_inventory-appraise");
    }
    public static ConfigurationSection getAppraiseInventory() {
        return GermGuiService.getGermGui("appraise", "appraise");
    }

    public static ConfigurationSection getAppraiseGif(){
        return GermGuiService.getGermGui("appraise", "appraise_gif");

    }



    public void open(Player player,String guiName){
        //获取鉴定的窗口
        AppraiseGuiService screen = new AppraiseGuiService(getAppraiseInventory(),guiName);
        //获取gui的配置,此处会把读取到的gui配置传到ForgeScreen的构造类中
        GermGuiScreen packscreen = GermGuiScreen.getGermGuiScreen("player_inventory-forge", getForgePlayerInventory());

//        screen.btnRegister();
        //为玩家打开鉴定窗口和背包窗口
        packscreen.openGui(player);
        screen.openChildGui(player);
    }
}
