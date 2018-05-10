package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 18/2/1.
 * 交易商品设置model
 */

public class GoodsTradingModel implements Serializable {

    /**
     * 商品名称
     */
    private String productName;
    private List<String> productImgUrlList;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getProductImgUrlList() {
        return productImgUrlList;
    }

    public void setProductImgUrlList(List<String> productImgUrlList) {
        this.productImgUrlList = productImgUrlList;
    }
}
