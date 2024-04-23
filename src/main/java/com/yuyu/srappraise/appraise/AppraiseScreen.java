package com.yuyu.srappraise.appraise;

import com.germ.germplugin.api.dynamic.DynamicBase;
import com.germ.germplugin.api.dynamic.gui.*;
import com.sakurarealm.sritem.api.ItemStackHelper;
import com.sakurarealm.sritem.api.SrItemAPI;
import com.sakurarealm.sritem.api.SrItemManager;
import com.sakurarealm.sritem.api.builder.SrItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @BelongsProject: SrAppraise
 * @BelongsPackage: com.yuyu.srappraise.appraise
 * @FileName: AppraiseScreen
 * @Author: 峰。
 * @Date: 2024/4/19-21:32
 * @Version: 1.0
 * @Description: 用于鉴定界面相关
 */
public class AppraiseScreen extends GermGuiScreen {

    public AppraiseScreen(ConfigurationSection configurationSection,String guiName) {
        super(guiName,configurationSection);
        //TODO(此处绑定给 √ 按钮绑定一个监听事件，在监听事件中获取放入了物品槽位的物品，然后鉴定)
        this.btnRegister();
        this.setClosedHandler(((player, germGuiScreen) -> {
            //窗口关闭事件
            this.returnItemStacks(player);
            this.close();
        }));
    }

    /**
     * 返回鉴定槽位内的物品给玩家
     *
     * @param player player
     */
    synchronized public void returnItemStacks(Player player) {
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");

        GermGuiSlot slot_biomass = (GermGuiSlot) canvas.getGuiPart("slot_biomass");


        ItemStack biomass_itemStack = slot_biomass.getItemStack();

            if (biomass_itemStack != null && biomass_itemStack.getType() != Material.AIR) {
                player.getInventory().addItem(biomass_itemStack);
            }
        slot_biomass.setItemStack(Material.AIR);

    }

//    public ItemStack getSrItem(){
//        ItemStack item = SrItemAPI.getItem();
//    }

    /**
     * 此方法用于绑定鉴定事件的发生
     */
    public void btnRegister(){
        //先获取canvas，再获取canvas中的按键
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");

        GermGuiButton confrimBtn = (GermGuiButton) canvas.getGuiPart("b_confirm");
        confrimBtn.registerCallbackHandler((clickPlayer, btn) -> {

            GermGuiSlot slot_biomass = (GermGuiSlot) canvas.getGuiPart("slot_biomass");

            ItemStack itemStack = slot_biomass.getItemStack();

            SrItemHandler srItemHandler = ItemStackHelper.getSrItemHandler(itemStack);
            String title = srItemHandler.getTitle();
            String index = title.substring(title.indexOf('l') + 1);
            String s = srItemHandler.getLore().get(0);

            if (SrItemAPI.hasItem(index)) {
                //TODO(此处hasItem的索引能通过，可以试试getItem能不能通过获取rpg物品)
//                SrItemManager.getInstance().getItem(index)
                index = s.substring(s.indexOf('-') + 1);
                itemStack = SrItemAPI.getItem(index,new HashMap<>(), Bukkit.getPlayer(clickPlayer.getName()),true,true);
                if (itemStack == null){
                    clickPlayer.sendMessage(ChatColor.RED+title+"不存在");
                    return;
                }

                if (itemStack.getAmount() != 1) {
                    clickPlayer.sendMessage(ChatColor.RED + "每次只能鉴定一个物品!");
                    return;
                }

                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    itemStack.setAmount(2);
                    clickPlayer.sendMessage(ChatColor.RED + "这是" + itemStack.getType().name());
                    SrItemAPI instance = SrItemAPI.INSTANCE;
                    slot_biomass.setItemStack(itemStack);
                }
            }else {
                clickPlayer.sendMessage(ChatColor.GREEN+"这是一个"+itemStack.getType().name());
            }
        }, GermGuiButton.EventType.LEFT_CLICK);
    }

}
