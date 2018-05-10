package com.merrichat.net.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by amssy on 2018/1/23.
 */

public class ChattingMultipleItem implements MultiItemEntity, Serializable {


    public static final int TEXT = 1;
    public static final int IMG = 2;
    public static final int IMG_TEXT = 3;

    private int itemType;


    public ChattingMultipleItem(int itemType) {
        this.itemType = itemType;
    }


    @Override
    public int getItemType() {
        return itemType;
    }

}
