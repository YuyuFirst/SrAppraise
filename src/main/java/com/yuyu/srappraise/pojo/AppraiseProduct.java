package com.yuyu.srappraise.pojo;

import com.sakurarealm.sritem.api.SrItemAPI;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @BelongsProject: SrAppraise
 * @BelongsPackage: com.yuyu.srappraise.pojo
 * @FileName: AppraiseProduct
 * @Author: 峰。
 * @Date: 2024/4/23-14:09
 * @Version: 1.0
 * @Description: 鉴定的产物
 */
public class AppraiseProduct {
    //物品的名称
    private final String ItemName;
    //物品的索引
    private final String indexName;
    //最大的概率
    private final double ProMax;
    //最小的概率
    private final double ProMin;
    //获取ItemStack
    private final ItemStack itemStack;

    public String getItemName() {
        return ItemName;
    }

    public String getIndexName() {
        return indexName;
    }

    public double getProMax() {
        return ProMax;
    }

    public double getProMin() {
        return ProMin;
    }

    public AppraiseProduct(String itemName, String indexName, double proMax, double proMin) {
        ItemName = itemName;
        this.indexName = indexName;
        ProMax = proMax;
        ProMin = proMin;
        this.itemStack = SrItemAPI.getItem(indexName,new HashMap<>(),null,true,true);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public String toString() {
        return "AppraiseProduct{" +
                "ItemName='" + ItemName + '\'' +
                ", indexName='" + indexName + '\'' +
                ", ProMax=" + ProMax +
                ", ProMin=" + ProMin +
                ", itemStack=" + itemStack +
                '}';
    }
}
