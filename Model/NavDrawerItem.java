package com.addventure.loanadda.Model;

/**
 * Created by Ravi on 29/07/15.
 */
public class NavDrawerItem {
    private String itemName;
    private String itemDesc;
    private Integer itemImg;


    private long iconId;

    public NavDrawerItem(String itemName) {
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.iconId = iconId;
    }

    public NavDrawerItem(String itemName, Integer itemImg) {
        this.itemName=itemName;
        this.itemImg=itemImg;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public long getIconId() {
        return iconId;
    }

    public void setIconId(long iconId) {
        this.iconId = iconId;
    }

    public int getItemImg() {
        return itemImg;
    }

    public void setItemImg(int itemImg) {
        this.itemImg = itemImg;
    }

}

