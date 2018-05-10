package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/20.
 */

public class SellOrderModel implements Serializable {

    /**
     * data : [{"orderNet":{},"returnOrderNet":{},"shopMemberName":"尼古拉斯☆赵四","serialNumber":326392152449025,"buyerMessage":"未留言","serialMember":[{"createdTime":1517389647585,"serialNumber":326392152449025,"memberId":323842518335488,"memberName":"尼古拉斯☆赵四","url":"http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037"}],"addresseeName":"测试1","shopMemberId":323842518335488,"addresseeDetailed":"北京市北京市海淀区华奥斯卡","shopName":"测试1群","isReturnProduct":0,"orderItemList":[{"salesPrice":8,"productPropertySpecValue":"干嘛","productNum":2,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516418958889.jpg","salesMemberSum":2,"productName":"测试003","productPrice":10,"productAmount":16,"discount":1,"productId":326210996264960}],"totalAmount":26,"returnStatus":0,"memberId":323842518335488,"memberName":"尼古拉斯☆赵四","addresseePhone":"15888888888","orderId":326392152449024,"deliveryFee":10}]
     * success : true
     */

    private boolean success;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * orderNet : {}
         * returnOrderNet : {}
         * shopMemberName : 尼古拉斯☆赵四
         * serialNumber : 326392152449025
         * buyerMessage : 未留言
         * serialMember : [{"createdTime":1517389647585,"serialNumber":326392152449025,"memberId":323842518335488,"memberName":"尼古拉斯☆赵四","url":"http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037"}]
         * addresseeName : 测试1
         * shopMemberId : 323842518335488
         * addresseeDetailed : 北京市北京市海淀区华奥斯卡
         * shopName : 测试1群
         * isReturnProduct : 0
         * orderItemList : [{"salesPrice":8,"productPropertySpecValue":"干嘛","productNum":2,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516418958889.jpg","salesMemberSum":2,"productName":"测试003","productPrice":10,"productAmount":16,"discount":1,"productId":326210996264960}]
         * totalAmount : 26.0
         * returnStatus : 0
         * memberId : 323842518335488
         * memberName : 尼古拉斯☆赵四
         * addresseePhone : 15888888888
         * orderId : 326392152449024
         * deliveryFee : 10
         */

        private OrderNetBean orderNet;
        private ReturnOrderNetBean returnOrderNet;
        private String shopMemberName;
        private String serialNumber;

        /**
         * 发货方式 1快递,2自取,3送货上门
         */
        private int sendType;


        private boolean arbitrate_due;


        /**
         * 交易类型 1:个人，2:c2c群，3:b2c(微商)群
         */
        private String transactionType;


        private String buyerMessage;
        private String addresseeName;
        private String shopMemberId;
        private String addresseeDetailed;
        private String shopName;
        /**
         * 0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
         */
        private String reStatus;
        private int isReturnProduct;
        private double totalAmount;
        private int returnStatus;
        private String memberId;
        private String memberName;
        private String addresseePhone;
        private String orderId;
        private int deliveryFee;
        private List<SerialMemberBean> serialMember;
        private List<OrderItemListBean> orderItemList;

        public String getReStatus() {
            return reStatus;
        }

        public void setReStatus(String reStatus) {
            this.reStatus = reStatus;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public boolean isArbitrate_due() {
            return arbitrate_due;
        }

        public void setArbitrate_due(boolean arbitrate_due) {
            this.arbitrate_due = arbitrate_due;
        }

        public int getSendType() {
            return sendType;
        }

        public void setSendType(int sendType) {
            this.sendType = sendType;
        }

        public OrderNetBean getOrderNet() {
            return orderNet;
        }

        public void setOrderNet(OrderNetBean orderNet) {
            this.orderNet = orderNet;
        }

        public ReturnOrderNetBean getReturnOrderNet() {
            return returnOrderNet;
        }

        public void setReturnOrderNet(ReturnOrderNetBean returnOrderNet) {
            this.returnOrderNet = returnOrderNet;
        }

        public String getShopMemberName() {
            return shopMemberName;
        }

        public void setShopMemberName(String shopMemberName) {
            this.shopMemberName = shopMemberName;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getBuyerMessage() {
            return buyerMessage;
        }

        public void setBuyerMessage(String buyerMessage) {
            this.buyerMessage = buyerMessage;
        }

        public String getAddresseeName() {
            return addresseeName;
        }

        public void setAddresseeName(String addresseeName) {
            this.addresseeName = addresseeName;
        }

        public String getShopMemberId() {
            return shopMemberId;
        }

        public void setShopMemberId(String shopMemberId) {
            this.shopMemberId = shopMemberId;
        }

        public String getAddresseeDetailed() {
            return addresseeDetailed;
        }

        public void setAddresseeDetailed(String addresseeDetailed) {
            this.addresseeDetailed = addresseeDetailed;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public int getIsReturnProduct() {
            return isReturnProduct;
        }

        public void setIsReturnProduct(int isReturnProduct) {
            this.isReturnProduct = isReturnProduct;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int getReturnStatus() {
            return returnStatus;
        }

        public void setReturnStatus(int returnStatus) {
            this.returnStatus = returnStatus;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }

        public String getAddresseePhone() {
            return addresseePhone;
        }

        public void setAddresseePhone(String addresseePhone) {
            this.addresseePhone = addresseePhone;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public int getDeliveryFee() {
            return deliveryFee;
        }

        public void setDeliveryFee(int deliveryFee) {
            this.deliveryFee = deliveryFee;
        }

        public List<SerialMemberBean> getSerialMember() {
            return serialMember;
        }

        public void setSerialMember(List<SerialMemberBean> serialMember) {
            this.serialMember = serialMember;
        }

        public List<OrderItemListBean> getOrderItemList() {
            return orderItemList;
        }

        public void setOrderItemList(List<OrderItemListBean> orderItemList) {
            this.orderItemList = orderItemList;
        }

        public static class OrderNetBean implements Serializable {
        }

        public static class ReturnOrderNetBean implements Serializable {
        }

        public static class SerialMemberBean implements Serializable {
            /**
             * createdTime : 1517389647585
             * serialNumber : 326392152449025
             * memberId : 323842518335488
             * memberName : 尼古拉斯☆赵四
             * url : http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037
             */

            private String createdTime;
            private String serialNumber;
            private String orderId;
            private String memberId;
            private String memberName;
            private String url;

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(String createdTime) {
                this.createdTime = createdTime;
            }

            public String getSerialNumber() {
                return serialNumber;
            }

            public void setSerialNumber(String serialNumber) {
                this.serialNumber = serialNumber;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class OrderItemListBean implements Serializable {
            /**
             * salesPrice : 8
             * productPropertySpecValue : 干嘛
             * productNum : 2
             * img : http://okdi.oss-cn-beijing.aliyuncs.com/1516418958889.jpg
             * salesMemberSum : 2
             * productName : 测试003
             * productPrice : 10
             * productAmount : 16.0
             * discount : 1
             * productId : 326210996264960
             */

            private int salesPrice;
            private String productPropertySpecValue;
            private int productNum;
            private String img;
            private int salesMemberSum;
            private String productName;
            private int productPrice;
            private double productAmount;
            private int discount;
            private String productId;

            public int getSalesPrice() {
                return salesPrice;
            }

            public void setSalesPrice(int salesPrice) {
                this.salesPrice = salesPrice;
            }

            public String getProductPropertySpecValue() {
                return productPropertySpecValue;
            }

            public void setProductPropertySpecValue(String productPropertySpecValue) {
                this.productPropertySpecValue = productPropertySpecValue;
            }

            public int getProductNum() {
                return productNum;
            }

            public void setProductNum(int productNum) {
                this.productNum = productNum;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public int getSalesMemberSum() {
                return salesMemberSum;
            }

            public void setSalesMemberSum(int salesMemberSum) {
                this.salesMemberSum = salesMemberSum;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public int getProductPrice() {
                return productPrice;
            }

            public void setProductPrice(int productPrice) {
                this.productPrice = productPrice;
            }

            public double getProductAmount() {
                return productAmount;
            }

            public void setProductAmount(double productAmount) {
                this.productAmount = productAmount;
            }

            public int getDiscount() {
                return discount;
            }

            public void setDiscount(int discount) {
                this.discount = discount;
            }

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }
        }
    }
}
