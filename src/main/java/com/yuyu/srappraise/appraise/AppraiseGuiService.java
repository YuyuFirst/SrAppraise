package com.yuyu.srappraise.appraise;

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

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

//    private GermGuiScreen gifScreen;
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
//        this.gifScreen = GermGuiScreen.getGermGuiScreen("player_inventory-appraise", AppraiseManager.getAppraiseGif());
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
                //清空所有槽位
                guiPart.setItemStack(Material.AIR);
            }
        }

        //这样就能确保上一次的东西不会在下次打开的时候出现
        this.itemStackHashMap.clear();
    }

    /**
     * 清空产物槽
     */
    public void clearSlotProduct(){
        for (int i = 1;i<=5;i++){
            String slot = "slot_product_"+i;
            GermGuiSlot germGuiSlot = this.germGuiSlotHashMap.get(slot);
            germGuiSlot.setItemStack(Material.AIR);
        }
    }

    public void getSoltGui(){
        //先获取canvas，再获取canvas中的槽位
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");

        GermGuiSlot slot_biomass = (GermGuiSlot) canvas.getGuiPart("slotConsume");
        GermGuiSlot slot_product_1 = (GermGuiSlot) canvas.getGuiPart("slot_product_1");
        GermGuiSlot slot_product_2 = (GermGuiSlot) canvas.getGuiPart("slot_product_2");
        GermGuiSlot slot_product_3 = (GermGuiSlot) canvas.getGuiPart("slot_product_3");
        GermGuiSlot slot_product_4 = (GermGuiSlot) canvas.getGuiPart("slot_product_4");
        GermGuiSlot slot_product_5 = (GermGuiSlot) canvas.getGuiPart("slot_product_5");

        this.germGuiSlotHashMap.put("slotConsume",slot_biomass);
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
        GermGuiSlot slot_biomass = this.germGuiSlotHashMap.get("slotConsume");
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");
        GermGuiButton button_consume = (GermGuiButton) canvas.getGuiPart("button_consume");
//        button_consume.setDefaultPath("local<->textures/gui/appraise/touming.gif");
//        button_consume.setHoverPath("local<->textures/gui/appraise/touming.gif");

        button_consume.registerCallbackHandler((player, germGuiSlot) -> {

            //验证产物槽上面是否存在没拿走的产物
            for (int i = 1;i<=5;i++){
                String slot = "slot_product_"+i;
                GermGuiSlot Germslot = this.germGuiSlotHashMap.get(slot);
                if (Germslot.getItemStack().getType() != Material.AIR && (boolean)Germslot.getInteract()) {
                    player.sendMessage(ChatColor.RED+"请拿出产物槽的物品!");
                    return;
                }
            }


            ItemStack itemStack = slot_biomass.getItemStack();


            //如果放上去空气直接返回不管
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                //清空产物槽
                this.clearSlotProduct();
                return;
            }else if (ItemStackHelper.getSrItemHandler(itemStack) == null){
                return;
            }

            //获取名字判断是否在可鉴定的物品中]
            SrItemHandler srItemHandler = ItemStackHelper.getSrItemHandler(itemStack);
            String title = srItemHandler.getTitle();
            //这个是item的名字
            String itemName = title.substring(title.indexOf('l') + 1);

            //此处通过表示存在可鉴定的产物
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
        },GermGuiButton.EventType.BEGIN_HOVER,GermGuiButton.EventType.LEAVE_HOVER);


    }

    /**
     * 用来判断是否可以左右移动产物槽中的物品
     * @return
     */
    public boolean boolToSlotMove(){
        GermGuiSlot germGuiSlot = this.germGuiSlotHashMap.get("slot_product_1");
        if ((boolean)germGuiSlot.getInteract()){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 左侧按键监听
     */
    public void registerLeft(){
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");
        GermGuiButton leftButton = (GermGuiButton) canvas.getGuiPart("left_button");

        leftButton.registerCallbackHandler((player, germGuiSlot) -> {

            //如果可能的产物小于五个，或者产物槽可以交互，就不能移动槽位中的产品
            if (this.itemStackHashMap.size() <= 5 || !this.boolToSlotMove()){
                return;
            }


            GermGuiSlot slotConsume = this.germGuiSlotHashMap.get("slotConsume");

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

            //如果可能的产物小于五个，或者产物槽可以交互，就不能移动槽位中的产品
            if (this.itemStackHashMap.size() <= 5 || !this.boolToSlotMove()){
                return;
            }

            GermGuiSlot slotConsume = this.germGuiSlotHashMap.get("slotConsume");

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
    synchronized public void btnRegister() {
        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("utility");

        GermGuiButton confrimBtn = (GermGuiButton) canvas.getGuiPart("b_confirm");
        confrimBtn.registerCallbackHandler((clickPlayer, btn) -> {

            //开始之前循环一边产品槽判断是否有东西没有取出来
            for (int i = 1;i <= 5;i++){
                String slot = "slot_product_"+ i;
                GermGuiSlot germGuiSlot = this.germGuiSlotHashMap.get(slot);
                if ((boolean)germGuiSlot.getInteract() && germGuiSlot.getItemStack().getType() != Material.AIR){
                    clickPlayer.sendMessage(ChatColor.RED+"请把上次鉴定的产物拿出来!");
                    return;
                }
            }


            Random random = new Random();
            HashMap<String,ItemStack> itemStacks= new LinkedHashMap<>();
            //获取物品槽
            GermGuiSlot slot_biomass = this.germGuiSlotHashMap.get("slotConsume");
            //获取透明gui
//            GermGuiScreen touMingGui = GermGuiScreen.getGermGuiScreen("touming", AppraiseManager.getTouMingButton());
//            touMingGui.openChildGui(clickPlayer);

            //获取鉴定的物品
            ItemStack itemStack = slot_biomass.getItemStack();
            AtomicInteger amount = new AtomicInteger(itemStack.getAmount());
//            slot_biomass.setInteract(false);


            if(itemStack == null || itemStack.getType() == Material.AIR) {
                clickPlayer.sendMessage(ChatColor.RED + "你必须在鉴定槽中放一个东西!");
                return;
            }else if (ItemStackHelper.getSrItemHandler(itemStack) == null){
                return;
            }
            GermGuiScreen gifScreen = GermGuiScreen.getGermGuiScreen("player_inventory-appraise", AppraiseManager.getAppraiseGif());
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

                    //如果同时鉴定多个
                    for (int i = 0; i < amount.get(); i++) {
                        //利用Random生成一个随机数来确定产物
                        float probility = random.nextFloat();
                        for (String product : stringAppraiseProductHashMap.keySet()) {
                            AppraiseProduct appraiseProduct = stringAppraiseProductHashMap.get(product);
                            if (probility >= appraiseProduct.getProMin() && probility < appraiseProduct.getProMax()) {
                                //概率通过，获取该产物 TODO(此处注意，appraiseProduct对象中的产物只能用来展示鉴定！不能做出任何修改！！！)
                                ItemStack item =
                                        SrItemAPI.getItem(appraiseProduct.getIndexName(),new HashMap<>(),null,true,true);
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

                    int timeRun = 9 / itemStacks.size();

                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

                    AtomicInteger j = new AtomicInteger(0);
                    //定时执行的函数
                    executor.scheduleAtFixedRate(() -> {
                        if (j.intValue() == 0){
                            j.incrementAndGet();
                            return;
                        }
                        //迭代循环
                        if (!itemStacks.isEmpty()) {
                            Iterator<Map.Entry<String, ItemStack>> iterator = itemStacks.entrySet().iterator();
                            if (iterator.hasNext()) {
                                Map.Entry<String, ItemStack> entry = iterator.next();
                                iterator.remove();//删除元素
                                if (j.get() <= 5) {
                                    String slot = "slot_product_" + j;
                                    GermGuiSlot germGuiSlot = this.germGuiSlotHashMap.get(slot);
                                    germGuiSlot.setItemStack(entry.getValue());
                                     amount.set(amount.get() - entry.getValue().getAmount());
                                     if (itemStack == slot_biomass.getItemStack()) {
                                    itemStack.setAmount(itemStack.getAmount() - entry.getValue().getAmount());
                                    slot_biomass.setItemStack(itemStack);
                                     }else {
                                         clickPlayer.sendMessage(ChatColor.RED+"请不要在鉴定过程中取出鉴定物品!!!");
                                         germGuiSlot.setItemStack(new ItemStack(Material.AIR));
                                         executor.shutdown();
                                     }
                                }else {
                                //j大于5，直接放进背包
                                    PlayerInventory inventory = clickPlayer.getInventory();
                                    inventory.addItem(entry.getValue());
                                    amount.set(amount.get() - entry.getValue().getAmount());
                                    if (itemStack == slot_biomass.getItemStack()) {
                                        itemStack.setAmount(itemStack.getAmount() - entry.getValue().getAmount());
                                        slot_biomass.setItemStack(itemStack);
                                    }else{
                                        inventory.remove(entry.getValue());
                                        clickPlayer.sendMessage(ChatColor.RED+"请不要在鉴定过程中取出鉴定物品!!!");
                                        executor.shutdown();
                                    }
                                }
                                j.incrementAndGet();
                                //最后放一个j++
                            }
                        }else {
                            //集合为空就结束
                            executor.shutdown();
                        }
                    },0 , timeRun , TimeUnit.SECONDS);
                }else {
                    clickPlayer.sendMessage(ChatColor.AQUA+"这是"+amount+"个"+itemName);
                }

            if(itemStack.getAmount() > 0){
                //如果鉴定完产物还有多的，放入槽位中 TODO(一般不会出现这种情况)
                itemStack.setAmount(amount.intValue());
                slot_biomass.setItemStack(itemStack);
            }
//            touMingGui.close();
            gifScreen.close();
            slot_biomass.setInteract(true);
        }, GermGuiButton.EventType.LEFT_CLICK);
    }

}
