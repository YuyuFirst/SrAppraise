package com.yuyu.srappraise.appraise;

import com.germ.germplugin.api.dynamic.DynamicBase;
import com.germ.germplugin.api.dynamic.gui.*;
import com.sakurarealm.sritem.api.ItemStackHelper;
import com.sakurarealm.sritem.api.SrItemAPI;
import com.sakurarealm.sritem.api.builder.SrItemHandler;
import com.yuyu.srappraise.SrAppraise;
import com.yuyu.srappraise.pojo.AppraiseProduct;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @BelongsProject: SrAppraise
 * @BelongsPackage: com.yuyu.srappraise.appraise
 * @FileName: AppraiseScreen
 * @Author: 峰。
 * @Date: 2024/4/19-21:32
 * @Version: 1.0
 * @Description: 用于鉴定界面相关
 */
public class AppraiseGuiService extends GermGuiScreen {

    private GermGuiScreen gifScreen;
    private HashMap<String,GermGuiSlot> germGuiSlotHashMap;
    private List<ItemStack> itemStackHashMap;
    private int index;

    public AppraiseGuiService(ConfigurationSection configurationSection, String guiName) {
        super(guiName, configurationSection);
        this.index = 0;
        this.germGuiSlotHashMap = new HashMap<>();
        this.itemStackHashMap = new ArrayList<>();
//        //TODO(此处绑定给 √ 按钮绑定一个监听事件，在监听事件中获取放入了物品槽位的物品，然后鉴定)
        this.getSoltGui();
        this.soltRegister();
        this.btnRegister();
        this.registerLeft();
        this.registerRight();
        this.gifScreen = GermGuiScreen.getGermGuiScreen("player_inventory-appraise", AppraiseManager.getAppraiseGif());
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

        List<GermGuiSlot> allGuiParts = canvas.getAllGuiParts(GermGuiSlot.class);

        for (GermGuiSlot guiPart : allGuiParts) {

            if (guiPart.getItemStack().getType() == Material.AIR || (boolean)guiPart.getInteract() == false) {
                continue;
            }else {
                //不是空气就把东西放进玩家背包中去
                player.getInventory().addItem(guiPart.getItemStack());
            }
        }
    }



    public void getSoltGui(){
        //先获取canvas，再获取canvas中的槽位
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");

        GermGuiSlot slot_biomass = (GermGuiSlot) canvas.getGuiPart("slot_consume");
        GermGuiSlot slot_product_1 = (GermGuiSlot) canvas.getGuiPart("slot_product_1");
        GermGuiSlot slot_product_2 = (GermGuiSlot) canvas.getGuiPart("slot_product_2");
        GermGuiSlot slot_product_3 = (GermGuiSlot) canvas.getGuiPart("slot_product_3");
        GermGuiSlot slot_product_4 = (GermGuiSlot) canvas.getGuiPart("slot_product_4");
        GermGuiSlot slot_product_5 = (GermGuiSlot) canvas.getGuiPart("slot_product_5");

        this.germGuiSlotHashMap.put("slot_consume",slot_biomass);
        this.germGuiSlotHashMap.put("slot_product_1",slot_product_1);
        this.germGuiSlotHashMap.put("slot_product_2",slot_product_2);
        this.germGuiSlotHashMap.put("slot_product_3",slot_product_3);
        this.germGuiSlotHashMap.put("slot_product_4",slot_product_4);
        this.germGuiSlotHashMap.put("slot_product_5",slot_product_5);
    }

    /**
     * 监听物品槽
     */
    synchronized public void soltRegister() {
        GermGuiSlot slot_biomass = this.germGuiSlotHashMap.get("slot_consume");

        slot_biomass.registerCallbackHandler((player, germGuiSlot) -> {

            ItemStack itemStack = slot_biomass.getItemStack();

            //如果放上去空气直接返回不管
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }else if (ItemStackHelper.getSrItemHandler(itemStack) == null){
                return;
            }

            //获取名字判断是否在可鉴定的物品中]
            SrItemHandler srItemHandler = ItemStackHelper.getSrItemHandler(itemStack);
            String title = srItemHandler.getTitle();
            //这个是item的名字
            String itemName = title.substring(title.indexOf('l') + 1);

                    if (SrAppraise.getConfigManager().getAppraiseItemMap().containsKey(itemName)){

                        itemStackHashMap.clear();

                        HashMap<String, AppraiseProduct> appraiseProductHashMap =
                                SrAppraise.getConfigManager().getAppraiseItemMap().get(itemName);
                        for (String key : appraiseProductHashMap.keySet()){
                            //获取ItemStack
                            ItemStack product = appraiseProductHashMap.get(key).getItemStack();
                            if (product != null) {
                                product.setAmount(1);
                                this.itemStackHashMap.add(product);
                            }
                        }

                        //把可能的产物放上槽位中
                        for (int i = 1;i<= this.itemStackHashMap.size() && i <= 5;i++){
                            //物品槽位
                            String slot = "slot_product_"+ i;
                            //获取产物
                            ItemStack product = this.itemStackHashMap.get(i - 1);
                            GermGuiSlot productSlot = this.germGuiSlotHashMap.get(slot);
                            productSlot.setItemStack(product);
                            //把展示槽锁住
                            productSlot.setInteract(false);
                        }
                        this.index = 1;
                    }else {
                        //不包含就返回
                        return;
                    }
        },GermGuiSlot.EventType.LEFT_CLICK);  //左键点击时触发，注意可能会放空气进去

    }

    /**
     * 左侧按键监听
     */
    public void registerLeft(){
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");
        GermGuiButton leftButton = (GermGuiButton) canvas.getGuiPart("left_button");

        leftButton.registerCallbackHandler((player, germGuiSlot) -> {

            if (this.itemStackHashMap.size() <= 5){
                return;
            }

            GermGuiSlot slotConsume = this.germGuiSlotHashMap.get("slot_consume");

            if (slotConsume.getItemStack().getType() == Material.AIR) {
                return;
            }
            //先确定一号槽位的值
            if(this.index == this.itemStackHashMap.size()){
                this.index = 1;
                //放东西进去
                this.setItemToSlot();
            }else {
                this.index++;
                this.setItemToSlot();
            }
        },GermGuiButton.EventType.LEFT_CLICK);
    }


    /**
     * 右侧按键监听
     */
    public void registerRight(){
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");
        GermGuiButton rightButton = (GermGuiButton) canvas.getGuiPart("right_button");

        rightButton.registerCallbackHandler((player, germGuiSlot) -> {

            if (this.itemStackHashMap.size() <= 5){
                return;
            }

            GermGuiSlot slotConsume = this.germGuiSlotHashMap.get("slot_consume");

            if (slotConsume.getItemStack().getType() == Material.AIR) {
                return;
            }

            if (this.index == 1){
                this.index = this.itemStackHashMap.size();
                setItemToSlot();
            }else {
                this.index--;
                this.setItemToSlot();
            }


        },GermGuiButton.EventType.LEFT_CLICK);
    }

    /**
     * 用于在槽位中放置物品
     */
    public void setItemToSlot() {
        //用于获取ItemStackHashMap中的产物，因为List索引从0开始，所以此处-1
        int indexCopy = index - 1;

        for (int i = 1;i <= this.itemStackHashMap.size() && i <= 5;i++){
            //物品槽位
            String slot = "slot_product_"+ i;
            //获取产物
            ItemStack product = this.itemStackHashMap.get(indexCopy);
            GermGuiSlot productSlot = this.germGuiSlotHashMap.get(slot);
            productSlot.setItemStack(product);

            indexCopy += 1;

            if (indexCopy == this.itemStackHashMap.size()){
                indexCopy = 0;
            }
        }
    }


    /**
     * 此方法用于绑定鉴定事件的发生
     */
    public void btnRegister() {
        GermGuiCanvas  canvas = (GermGuiCanvas) getGuiPart("utility");

        GermGuiButton confrimBtn = (GermGuiButton) canvas.getGuiPart("b_confirm");
        confrimBtn.registerCallbackHandler((clickPlayer, btn) -> {

            Random random = new Random();
            HashMap<String,ItemStack> itemStacks= new HashMap<>();
            //获取物品槽
            GermGuiSlot slot_biomass = this.germGuiSlotHashMap.get("slot_consume");

            //开始鉴定后把槽位锁住
            slot_biomass.setInteract(true);
            //获取鉴定的物品
            ItemStack itemStack = slot_biomass.getItemStack();

            if(itemStack == null || itemStack.getType() == Material.AIR) {
                clickPlayer.sendMessage(ChatColor.RED + "你必须在鉴定槽中放一个东西!");
                return;
            }else if (ItemStackHelper.getSrItemHandler(itemStack) == null){
                return;
            }

                gifScreen.openChildGui(clickPlayer);

                SrItemHandler srItemHandler = ItemStackHelper.getSrItemHandler(itemStack);
                String title = srItemHandler.getTitle();
                //这个是item的名字
                String itemName = title.substring(title.indexOf('l') + 1);
//                String s = srItemHandler.getLore().get(0);

                //先判断物品是否在可以鉴定的列表里面
                if (SrAppraise.getConfigManager().getAppraiseItemMap().containsKey(itemName)){

                    //清空物品槽
                    for (int i = 1 ;i <= 5;i++){
                        String slot = "slot_product_" + i;
                        GermGuiSlot germGuiSlot = this.germGuiSlotHashMap.get(slot);
                        germGuiSlot.setItemStack(new ItemStack(Material.AIR));
                        germGuiSlot.setInteract(true);
                    }

                    HashMap<String, AppraiseProduct> stringAppraiseProductHashMap =
                            SrAppraise.getConfigManager().getAppraiseItemMap().get(itemName);

                    int amount = itemStack.getAmount();
                    //如果同时鉴定多个
                    for (int i = 0;i < amount;i++) {
                        //利用Random生成一个随机数来确定产物
                        float probility = random.nextFloat();
                        for (String product : stringAppraiseProductHashMap.keySet()) {
                            AppraiseProduct appraiseProduct = stringAppraiseProductHashMap.get(product);
                            if (probility >= appraiseProduct.getProMin() && probility < appraiseProduct.getProMax()) {
                                //概率通过，获取该产物
                                ItemStack item =
                                        appraiseProduct.getItemStack();
                                //获取产物后，将产物String，ItemStack K-V 的形式存到map中
                                if (itemStacks.containsKey(product)) {
                                    ItemStack itemStack1 = itemStacks.get(product);
                                    itemStack1.setAmount(itemStack1.getAmount() + 1);
                                    break;
                                }else {
                                    itemStacks.put(product, item);
                                    break;
                                }
                            }
                        }
                    }
                    //所有物品全部结束循环的时候
                    int j = 1;
                    int timeRun = 8000 / itemStacks.size();
                    for (ItemStack srItem : itemStacks.values()){
                        try {
                            Thread.sleep(timeRun);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        //拼接物品槽的名字
                        String slot = "slot_product_" + j;
                        GermGuiSlot product = this.germGuiSlotHashMap.get(slot);
                        ItemStack itemStack1 = product.getItemStack();
                        //有五个物品槽
                        if (j <= 5 && itemStack1.getType() == Material.AIR){
                            product.setItemStack(srItem);
                            itemStack.setAmount(itemStack.getAmount() - srItem.getAmount());
                            slot_biomass.setItemStack(itemStack);

                        }else {
                            //多出来的放进玩家背包
                            PlayerInventory inventory = clickPlayer.getInventory();
                            inventory.addItem(srItem);
                            itemStack.setAmount(itemStack.getAmount() - srItem.getAmount());
                            slot_biomass.setItemStack(itemStack);
                        }
                        j++;
                    }
                }else {
                    int amount = itemStack.getAmount();
                    clickPlayer.sendMessage(ChatColor.AQUA+"这是"+amount+"个"+itemName);
                }

            slot_biomass.setInteract(false);
            gifScreen.close();

        }, GermGuiButton.EventType.LEFT_CLICK);
    }

}
