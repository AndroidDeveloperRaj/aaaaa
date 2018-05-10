package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 2018/2/23.
 */

public class ProductListModel implements Serializable{


    /**
     * data : {"message":"","list":[{"productName":"商品1","productType":"","productId":330545681522688},{"productName":"商品2","productType":"","productId":330545681522689}],"success":true,"isSpecifiedProduct":0,"tradeMsg":0,"productType":1}
     * success : true
     */

    private DataBean data;
    private boolean success;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * message :
         * list : [{"productName":"商品1","productType":"","productId":330545681522688},{"productName":"商品2","productType":"","productId":330545681522689}]
         * success : true
         * isSpecifiedProduct : 0
         * tradeMsg : 0
         * productType : 1
         */

        private String message;
        private boolean success;
        private int isSpecifiedProduct;
        private int tradeMsg;
        private int productType;
        private List<GoodsTradingModel> list;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getIsSpecifiedProduct() {
            return isSpecifiedProduct;
        }

        public void setIsSpecifiedProduct(int isSpecifiedProduct) {
            this.isSpecifiedProduct = isSpecifiedProduct;
        }

        public int getTradeMsg() {
            return tradeMsg;
        }

        public void setTradeMsg(int tradeMsg) {
            this.tradeMsg = tradeMsg;
        }

        public int getProductType() {
            return productType;
        }

        public void setProductType(int productType) {
            this.productType = productType;
        }

        public List<GoodsTradingModel> getList() {
            return list;
        }
        public void setList(List<GoodsTradingModel> list) {
            this.list = list;
        }
    }
}
