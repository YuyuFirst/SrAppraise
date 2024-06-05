package com.yuyu.srappraise.config;

import com.yuyu.srappraise.pojo.AppraiseProduct;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @BelongsProject: SrAppraise
 * @BelongsPackage: com.yuyu.srappraise.config
 * @FileName: ConfigManager
 * @Author: 峰。
 * @Date: 2024/4/23-14:14
 * @Version: 1.0
 * @Description:用于加载文件
 */
public class ConfigManager {
    //用于存放鉴定的物品,根据玩家放入的物品，获取名称，用于此map集合获取可能得到的物品集合，然后随机数判断
    private  HashMap<String,HashMap<String, AppraiseProduct>> appraiseItemMap;
    private Plugin plugin;

    public HashMap<String, HashMap<String, AppraiseProduct>> getAppraiseItemMap() {
        return appraiseItemMap;
    }

    public ConfigManager(Plugin plugin) {
        Logger logger = plugin.getLogger();
        this.plugin = plugin;

        HashMap<String,HashMap<String, AppraiseProduct>> hashMapHashMap = new HashMap<>();

        //保存文件
        plugin.saveResource("AppraiseItem.yml",false);
        plugin.saveResource("ItemAppraiseProduct.yml",false);
        plugin.saveResource("AppraiseToProduct.yml",false);


        File appraiseItemFile = new File(plugin.getDataFolder(), "AppraiseItem.yml");
        File ItemAppraiseProduct = new File(plugin.getDataFolder(), "ItemAppraiseProduct.yml");
        File AppraiseToProduct = new File(plugin.getDataFolder(), "AppraiseToProduct.yml");

        FileConfiguration appraiseItemConfig = YamlConfiguration.loadConfiguration(appraiseItemFile);
        FileConfiguration itemAppraiseProduct = YamlConfiguration.loadConfiguration(ItemAppraiseProduct);
        FileConfiguration appraiseToProduct = YamlConfiguration.loadConfiguration(AppraiseToProduct);

        //获取可以鉴定的物品的列表
        List<String> stringList = appraiseItemConfig.getStringList("SrItem.Appraise");
        for (String srItem : stringList){//读取到每个可以鉴定的物品

            HashMap<String,AppraiseProduct> productMap = new HashMap<>();

            //读取该物品可以得到的产物
            List<String> productList = appraiseToProduct.getStringList("SrItem." + srItem);

            for (String product : productList){

                //读取配置文件中的数据
                Double proMax = itemAppraiseProduct.getDouble("SrItem."+srItem+"."+product+".probabilityMax");
                Double proMin = itemAppraiseProduct.getDouble("SrItem."+srItem+"."+product+".probabilityMin");
                String index = itemAppraiseProduct.getString("SrItem."+srItem+"."+product+".index");
                AppraiseProduct appraiseProduct = new AppraiseProduct(product, index, proMax, proMin);

                logger.info(ChatColor.AQUA+"鉴定物品:"+srItem+"产物:"+ appraiseProduct.toString());

                //针对产物存放
                productMap.put(product,appraiseProduct);
            }

            //针对鉴定物品存放
            hashMapHashMap.put(srItem,productMap);
        }

        this.appraiseItemMap = hashMapHashMap;
    }

    public void reloadConfig(){
        appraiseItemMap.clear();

        plugin.reloadConfig();

        HashMap<String,HashMap<String, AppraiseProduct>> hashMapHashMap = new HashMap<>();

        //保存文件
        plugin.saveResource("AppraiseItem.yml",false);
        plugin.saveResource("ItemAppraiseProduct.yml",false);
        plugin.saveResource("AppraiseToProduct.yml",false);


        File appraiseItemFile = new File(plugin.getDataFolder(), "AppraiseItem.yml");
        File ItemAppraiseProduct = new File(plugin.getDataFolder(), "ItemAppraiseProduct.yml");
        File AppraiseToProduct = new File(plugin.getDataFolder(), "AppraiseToProduct.yml");

        FileConfiguration appraiseItemConfig = YamlConfiguration.loadConfiguration(appraiseItemFile);
        FileConfiguration itemAppraiseProduct = YamlConfiguration.loadConfiguration(ItemAppraiseProduct);
        FileConfiguration appraiseToProduct = YamlConfiguration.loadConfiguration(AppraiseToProduct);

        //获取可以鉴定的物品的列表
        List<String> stringList = appraiseItemConfig.getStringList("SrItem.Appraise");
        for (String srItem : stringList){//读取到每个可以鉴定的物品

            HashMap<String,AppraiseProduct> productMap = new HashMap<>();

            //读取该物品可以得到的产物
            List<String> productList = appraiseToProduct.getStringList("SrItem." + srItem);

            for (String product : productList){

                //读取配置文件中的数据
                Double proMax = itemAppraiseProduct.getDouble("SrItem."+srItem+"."+product+".probabilityMax");
                Double proMin = itemAppraiseProduct.getDouble("SrItem."+srItem+"."+product+".probabilityMin");
                String index = itemAppraiseProduct.getString("SrItem."+srItem+"."+product+".index");
                AppraiseProduct appraiseProduct = new AppraiseProduct(product, index, proMax, proMin);

                this.plugin.getLogger().info(ChatColor.AQUA+"鉴定物品:"+srItem+"产物:"+ appraiseProduct.toString());

                //针对产物存放
                productMap.put(product,appraiseProduct);
            }

            //针对鉴定物品存放
            hashMapHashMap.put(srItem,productMap);
        }

        this.appraiseItemMap = hashMapHashMap;
    }
}
