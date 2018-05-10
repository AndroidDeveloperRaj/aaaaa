package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/22.
 */

public class PendingDeliveryModel implements Serializable {

    /**
     * data : [{"orderNet":{},"returnOrderNet":{},"sendTime":1517293618924,"serialNumber":0,"buyerMessage":"未留言","configReceiptTime":1517293618924,"addresseeName":"测试1","shopMemberId":323842518335488,"addresseeDetailed":"北京市北京市海淀区华奥斯卡","shopName":"尼古拉斯☆赵四","totalAmount":11,"orderItemList":[{"salesPrice":10,"productPropertySpecValue":"测试","productNum":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/IMG_-320728804.jpg","salesMemberSum":3,"productName":"福利彩票001","productPrice":10,"productAmount":10,"discount":1,"productId":326165704073216}],"memberId":323842518335488,"memberName":"尼古拉斯☆赵四","comfigReceiptTime":1517293618924,"orderId":326170493968384,"deliveryFee":1},{"orderNet":{},"returnOrderNet":{},"sendTime":1517293618924,"serialNumber":326187506065409,"buyerMessage":"未留言","configReceiptTime":1517293618924,"addresseeName":"北京","shopMemberId":323842518335488,"addresseeDetailed":"昌平昌平沙河。。。。","shopName":"尼古拉斯☆赵四","totalAmount":0.02,"orderItemList":[{"salesPrice":0.01,"productPropertySpecValue":"999","productNum":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg","salesMemberSum":3,"productName":"测试商品1","productPrice":0.01,"productAmount":0.01,"discount":1,"productId":326022856564736}],"memberId":315917552893952,"memberName":"锦衣卫","comfigReceiptTime":1517293618924,"orderId":326187506065408,"deliveryFee":0.01},{"orderNet":{},"returnOrderNet":{},"sendTime":1517293618925,"serialNumber":326188357509121,"buyerMessage":"未留言","configReceiptTime":1517293618925,"addresseeName":"北京","shopMemberId":323842518335488,"addresseeDetailed":"昌平昌平沙河。。。。","shopName":"尼古拉斯☆赵四","totalAmount":0.02,"orderItemList":[{"salesPrice":0.01,"productPropertySpecValue":"999","productNum":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg","salesMemberSum":3,"productName":"测试商品1","productPrice":0.01,"productAmount":0.01,"discount":1,"productId":326022856564736}],"memberId":315917552893952,"memberName":"锦衣卫","comfigReceiptTime":1517293618925,"orderId":326188357509120,"deliveryFee":0.01},{"orderNet":{},"returnOrderNet":{},"sendTime":1517293618925,"serialNumber":0,"buyerMessage":"未留言","configReceiptTime":1517293618925,"addresseeName":"测试1","shopMemberId":323842518335488,"addresseeDetailed":"北京市北京市海淀区华奥斯卡","shopName":"尼古拉斯☆赵四","totalAmount":10.01,"orderItemList":[{"salesPrice":0.01,"productPropertySpecValue":"绿色","productNum":1000,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg","salesMemberSum":3,"productName":"测试商品1","productPrice":0.01,"productAmount":10,"discount":1,"productId":326022856564736}],"memberId":323842518335488,"memberName":"尼古拉斯☆赵四","comfigReceiptTime":1517293618925,"orderId":326189756309504,"deliveryFee":0.01}]
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
         * sendTime : 1517293618924
         * serialNumber : 0
         * buyerMessage : 未留言
         * configReceiptTime : 1517293618924
         * addresseeName : 测试1
         * shopMemberId : 323842518335488
         * addresseeDetailed : 北京市北京市海淀区华奥斯卡
         * shopName : 尼古拉斯☆赵四
         * totalAmount : 11.0
         * orderItemList : [{"salesPrice":10,"productPropertySpecValue":"测试","productNum":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/IMG_-320728804.jpg","salesMemberSum":3,"productName":"福利彩票001","productPrice":10,"productAmount":10,"discount":1,"productId":326165704073216}]
         * memberId : 323842518335488
         * memberName : 尼古拉斯☆赵四
         * comfigReceiptTime : 1517293618924
         * orderId : 326170493968384
         * deliveryFee : 1
         */

        private OrderNetBean orderNet;
        private ReturnOrderNetBean returnOrderNet;
        private String sendTime;
        private String sendType;
        private String serialNumber;
        private String reStatus;
        private String reStatusText;
        private String buyerMessage;
        private String configReceiptTime;
        private String addresseeName;
        private String addresseePhone;
        private String shopMemberId;
        private String addresseeDetailed;
        private String shopName;
        private String totalAmount;
        private String memberId;
        private String memberName;
        private String memberUrl;
        private String comfigReceiptTime;
        private String orderId;
        private String deliveryFee;
        private List<OrderItemListBean> orderItemList;

        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            this.sendType = sendType;
        }

        public String getReStatusText() {
            return reStatusText;
        }

        public void setReStatusText(String reStatusText) {
            this.reStatusText = reStatusText;
        }

        public String getMemberUrl() {
            return memberUrl;
        }

        public void setMemberUrl(String memberUrl) {
            this.memberUrl = memberUrl;
        }

        public String getReStatus() {
            return reStatus;
        }

        public void setReStatus(String reStatus) {
            this.reStatus = reStatus;
        }

        public String getAddresseePhone() {
            return addresseePhone;
        }

        public void setAddresseePhone(String addresseePhone) {
            this.addresseePhone = addresseePhone;
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

        public String getSendTime() {
            return sendTime;
        }

        public void setSendTime(String sendTime) {
            this.sendTime = sendTime;
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

        public String getConfigReceiptTime() {
            return configReceiptTime;
        }

        public void setConfigReceiptTime(String configReceiptTime) {
            this.configReceiptTime = configReceiptTime;
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

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
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

        public String getComfigReceiptTime() {
            return comfigReceiptTime;
        }

        public void setComfigReceiptTime(String comfigReceiptTime) {
            this.comfigReceiptTime = comfigReceiptTime;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getDeliveryFee() {
            return deliveryFee;
        }

        public void setDeliveryFee(String deliveryFee) {
            this.deliveryFee = deliveryFee;
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

        public static class OrderItemListBean implements Serializable {
            /**
             * salesPrice : 10
             * productPropertySpecValue : 测试
             * productNum : 1
             * img : http://okdi.oss-cn-beijing.aliyuncs.com/IMG_-320728804.jpg
             * salesMemberSum : 3
             * productName : 福利彩票001
             * productPrice : 10
             * productAmount : 10.0
             * discount : 1
             * productId : 326165704073216
             */

            private String salesPrice;
            private String productPropertySpecValue;
            private String productNum;
            private String productType;
            private String img;
            private String salesMemberSum;
            private String productName;
            private String productPrice;
            private String productAmount;
            private String discount;
            private String productId;

            public String getProductType() {
                return productType;
            }

            public void setProductType(String productType) {
                this.productType = productType;
            }

            public String getSalesPrice() {
                return salesPrice;
            }

            public void setSalesPrice(String salesPrice) {
                this.salesPrice = salesPrice;
            }

            public String getProductPropertySpecValue() {
                return productPropertySpecValue;
            }

            public void setProductPropertySpecValue(String productPropertySpecValue) {
                this.productPropertySpecValue = productPropertySpecValue;
            }

            public String getProductNum() {
                return productNum;
            }

            public void setProductNum(String productNum) {
                this.productNum = productNum;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getSalesMemberSum() {
                return salesMemberSum;
            }

            public void setSalesMemberSum(String salesMemberSum) {
                this.salesMemberSum = salesMemberSum;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getProductPrice() {
                return productPrice;
            }

            public void setProductPrice(String productPrice) {
                this.productPrice = productPrice;
            }

            public String getProductAmount() {
                return productAmount;
            }

            public void setProductAmount(String productAmount) {
                this.productAmount = productAmount;
            }

            public String getDiscount() {
                return discount;
            }

            public void setDiscount(String discount) {
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
