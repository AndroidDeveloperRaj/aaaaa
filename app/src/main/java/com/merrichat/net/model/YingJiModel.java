package com.merrichat.net.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by AMSSY1 on 2017/11/9.
 */

public class YingJiModel implements MultiItemEntity {
    private final String url;
    public static final int itemType = 0;
    private final int item;

    public YingJiModel(int item,String url) {
        this.url = url;
        this.item = item;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int getItemType() {
        return item;
    }
}
