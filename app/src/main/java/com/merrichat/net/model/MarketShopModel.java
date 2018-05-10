package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * 商品详情实体类
 * Created by amssy on 18/1/26.
 */

public class MarketShopModel implements Serializable{

    /**
     * data : {"availableSupplyOrPurchaseQuantity":10,"buyer":0,"createTime":1516955911012,"creator":323842518335488,"deleteFlag":0,"endTime":1517215111012,"freight":0.1,"groupId":325471074902016,"groupNum":3,"groupPrice":899.99,"groupType":3,"id":325482542129152,"img":"http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037?1516953412432","imgs":["http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037?1516953412432"],"isIdentity":false,"memberNick":"尼古拉斯☆赵四","memberUrl":"http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037?timestamp=1517033584806","productCategoryId":0,"productDescribe":"可选规格为红色/白色","productId":0,"productModelDesc":"可选规格为红色/白色","productName":"尼古拉斯-赵四同款背心","productPrice":999.99,"productType":0,"seller":323842518335488,"status":0,"supplyOrPurchaseQuantity":10,"transInfoType":0,"updateTime":1516955911012}
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

    public static class DataBean implements Serializable{
        /**
         * availableSupplyOrPurchaseQuantity : 10
         * buyer : 0
         * createTime : 1516955911012
         * creator : 323842518335488
         * deleteFlag : 0
         * endTime : 1517215111012
         * freight : 0.1
         * groupId : 325471074902016
         * groupNum : 3
         * groupPrice : 899.99
         * groupType : 3
         * id : 325482542129152
         * img : http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037?1516953412432
         * imgs : ["http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037?1516953412432"]
         * isIdentity : false
         * memberNick : 尼古拉斯☆赵四
         * memberUrl : http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037?timestamp=1517033584806
         * productCategoryId : 0
         * productDescribe : 可选规格为红色/白色
         * productId : 0
         * productModelDesc : 可选规格为红色/白色
         * productName : 尼古拉斯-赵四同款背心
         * productPrice : 999.99
         * productType : 0
         * seller : 323842518335488
         * status : 0
         * supplyOrPurchaseQuantity : 10
         * transInfoType : 0
         * updateTime : 1516955911012
         */

        private double availableSupplyOrPurchaseQuantity;
        private int buyer;
        private long createTime;
        private long creator;
        private int deleteFlag;
        private long endTime;
        private double freight;
        private long groupId;
        private int groupNum;
        private double groupPrice;
        private int groupType;
        private long id;
        private String img;
        private boolean isIdentity;
        private String memberNick;
        private String memberUrl;
        private int productCategoryId;
        private String productDescribe;
        private int productId;
        private String productModelDesc;
        private String productName;
        private double productPrice;
        private int productType;
        private long seller;
        private int status;
        private int supplyOrPurchaseQuantity;
        private int transInfoType;
        private long updateTime;
        private List<String> imgs;

        public double getAvailableSupplyOrPurchaseQuantity() {
            return availableSupplyOrPurchaseQuantity;
        }

        public void setAvailableSupplyOrPurchaseQuantity(double availableSupplyOrPurchaseQuantity) {
            this.availableSupplyOrPurchaseQuantity = availableSupplyOrPurchaseQuantity;
        }

        public int getBuyer() {
            return buyer;
        }

        public void setBuyer(int buyer) {
            this.buyer = buyer;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getCreator() {
            return creator;
        }

        public void setCreator(long creator) {
            this.creator = creator;
        }

        public int getDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(int deleteFlag) {
            this.deleteFlag = deleteFlag;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public double getFreight() {
            return freight;
        }

        public void setFreight(double freight) {
            this.freight = freight;
        }

        public long getGroupId() {
            return groupId;
        }

        public void setGroupId(long groupId) {
            this.groupId = groupId;
        }

        public int getGroupNum() {
            return groupNum;
        }

        public void setGroupNum(int groupNum) {
            this.groupNum = groupNum;
        }

        public double getGroupPrice() {
            return groupPrice;
        }

        public void setGroupPrice(double groupPrice) {
            this.groupPrice = groupPrice;
        }

        public int getGroupType() {
            return groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public boolean isIsIdentity() {
            return isIdentity;
        }

        public void setIsIdentity(boolean isIdentity) {
            this.isIdentity = isIdentity;
        }

        public String getMemberNick() {
            return memberNick;
        }

        public void setMemberNick(String memberNick) {
            this.memberNick = memberNick;
        }

        public String getMemberUrl() {
            return memberUrl;
        }

        public void setMemberUrl(String memberUrl) {
            this.memberUrl = memberUrl;
        }

        public int getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(int productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public String getProductDescribe() {
            return productDescribe;
        }

        public void setProductDescribe(String productDescribe) {
            this.productDescribe = productDescribe;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductModelDesc() {
            return productModelDesc;
        }

        public void setProductModelDesc(String productModelDesc) {
            this.productModelDesc = productModelDesc;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(double productPrice) {
            this.productPrice = productPrice;
        }

        public int getProductType() {
            return productType;
        }

        public void setProductType(int productType) {
            this.productType = productType;
        }

        public long getSeller() {
            return seller;
        }

        public void setSeller(long seller) {
            this.seller = seller;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSupplyOrPurchaseQuantity() {
            return supplyOrPurchaseQuantity;
        }

        public void setSupplyOrPurchaseQuantity(int supplyOrPurchaseQuantity) {
            this.supplyOrPurchaseQuantity = supplyOrPurchaseQuantity;
        }

        public int getTransInfoType() {
            return transInfoType;
        }

        public void setTransInfoType(int transInfoType) {
            this.transInfoType = transInfoType;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public List<String> getImgs() {
            return imgs;
        }

        public void setImgs(List<String> imgs) {
            this.imgs = imgs;
        }
    }
}
