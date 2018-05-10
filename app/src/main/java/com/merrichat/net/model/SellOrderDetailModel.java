package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/31.
 */

public class SellOrderDetailModel implements Serializable {

    /**
     * data : {"paymentType":3,"paymentTime":1517303520198,"sendTime":1517303463479,"shopMemberName":"尼古拉斯☆赵四","serialNumber":326211412156417,"buyerMessage":"未留言","configReceiptTime":1517303463479,"transactionType":2,"addresseeName":"测试1","addresseeDetailed":"北京市北京市海淀区华奥斯卡","shopName":"测试1群","orderItemList":[{"salesPrice":8,"productPropertySpecValue":"咯囧囧囧","productNum":3,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516418958889.jpg","salesMemberSum":2,"productName":"测试003","productPrice":10,"productAmount":24,"discount":1,"productId":326210996264960}],"totalAmount":34,"memberName":"尼古拉斯☆赵四","comfigReceiptTime":1517303463479,"addresseePhone":"15888888888","orderId":326211412156416,"deliveryFee":10}
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

    public static class DataBean implements Serializable {
        /**
         * paymentType : 3
         * paymentTime : 1517303520198
         * sendTime : 1517303463479
         * shopMemberName : 尼古拉斯☆赵四
         * serialNumber : 326211412156417
         * buyerMessage : 未留言
         * configReceiptTime : 1517303463479
         * transactionType : 2
         * addresseeName : 测试1
         * addresseeDetailed : 北京市北京市海淀区华奥斯卡
         * shopName : 测试1群
         * orderItemList : [{"salesPrice":8,"productPropertySpecValue":"咯囧囧囧","productNum":3,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516418958889.jpg","salesMemberSum":2,"productName":"测试003","productPrice":10,"productAmount":24,"discount":1,"productId":326210996264960}]
         * totalAmount : 34.0
         * memberName : 尼古拉斯☆赵四
         * comfigReceiptTime : 1517303463479
         * addresseePhone : 15888888888
         * orderId : 326211412156416
         * deliveryFee : 10
         * <p>
         * shopMemberId
         */

        private String shopMemberId;
        private String paymentType;
        private String paymentTime;
        private String sendTime;
        private String shopMemberName;
        private String serialNumber;
        private String buyerMessage;
        private String configReceiptTime;
        private String transactionType;
        private String addresseeName;
        private String addresseeDetailed;
        private String groupUrl;
        private String shopName;
        private String shopId;
        private String totalAmount;
        private String totalProductAmount;

        /**
         * 发货方式 1快递,2自取,3送货上门
         */
        private String sendType;

        private String memberName;
        private String memberId;
        private String memberUrl;
        private String comfigReceiptTime;
        private String addresseePhone;
        private String orderId;
        private String deliveryFee;
        private String reStatus;
        private String returnReason;
        private String reasonText;
        private String returnMoneyTime;
        private String refuseMoneyTime;

        /**
         * 申请退款时间
         */
        private String beginReturnTime;
        private String arbitrateCause;
        private String arbitrateResult;
        private List<String> returnUrls;
        private List<String> arbitrateUrls;
        private List<OrderItemListBean> orderItemList;


        /**
         * 申请仲裁时间
         */
        private String arbitrateTime;

        /**
         * 自动取消拼团时间
         */
        private String remainTime;


        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            this.sendType = sendType;
        }

        public String getRemainTime() {
            return remainTime;
        }

        public void setRemainTime(String remainTime) {
            this.remainTime = remainTime;
        }

        public String getArbitrateTime() {
            return arbitrateTime;
        }

        public void setArbitrateTime(String arbitrateTime) {
            this.arbitrateTime = arbitrateTime;
        }

        public String getGroupUrl() {
            return groupUrl;
        }

        public void setGroupUrl(String groupUrl) {
            this.groupUrl = groupUrl;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getArbitrateCause() {
            return arbitrateCause;
        }

        public void setArbitrateCause(String arbitrateCause) {
            this.arbitrateCause = arbitrateCause;
        }

        public String getArbitrateResult() {
            return arbitrateResult;
        }

        public void setArbitrateResult(String arbitrateResult) {
            this.arbitrateResult = arbitrateResult;
        }

        public List<String> getArbitrateUrls() {
            return arbitrateUrls;
        }

        public void setArbitrateUrls(List<String> arbitrateUrls) {
            this.arbitrateUrls = arbitrateUrls;
        }

        public String getReasonText() {
            return reasonText;
        }

        public void setReasonText(String reasonText) {
            this.reasonText = reasonText;
        }

        public String getShopMemberId() {
            return shopMemberId;
        }

        public void setShopMemberId(String shopMemberId) {
            this.shopMemberId = shopMemberId;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getMemberUrl() {
            return memberUrl;
        }

        public void setMemberUrl(String memberUrl) {
            this.memberUrl = memberUrl;
        }

        public String getBeginReturnTime() {
            return beginReturnTime;
        }

        public void setBeginReturnTime(String beginReturnTime) {
            this.beginReturnTime = beginReturnTime;
        }

        public String getReStatus() {
            return reStatus;
        }

        public void setReStatus(String reStatus) {
            this.reStatus = reStatus;
        }

        public String getReturnReason() {
            return returnReason;
        }

        public void setReturnReason(String returnReason) {
            this.returnReason = returnReason;
        }

        public String getReturnMoneyTime() {
            return returnMoneyTime;
        }

        public void setReturnMoneyTime(String returnMoneyTime) {
            this.returnMoneyTime = returnMoneyTime;
        }

        public String getRefuseMoneyTime() {
            return refuseMoneyTime;
        }

        public void setRefuseMoneyTime(String refuseMoneyTime) {
            this.refuseMoneyTime = refuseMoneyTime;
        }

        public List<String> getReturnUrls() {
            return returnUrls;
        }

        public void setReturnUrls(List<String> returnUrls) {
            this.returnUrls = returnUrls;
        }

        public String getTotalProductAmount() {
            return totalProductAmount;
        }

        public void setTotalProductAmount(String totalProductAmount) {
            this.totalProductAmount = totalProductAmount;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getPaymentTime() {
            return paymentTime;
        }

        public void setPaymentTime(String paymentTime) {
            this.paymentTime = paymentTime;
        }

        public String getSendTime() {
            return sendTime;
        }

        public void setSendTime(String sendTime) {
            this.sendTime = sendTime;
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

        public String getConfigReceiptTime() {
            return configReceiptTime;
        }

        public void setConfigReceiptTime(String configReceiptTime) {
            this.configReceiptTime = configReceiptTime;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getAddresseeName() {
            return addresseeName;
        }

        public void setAddresseeName(String addresseeName) {
            this.addresseeName = addresseeName;
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

        public static class OrderItemListBean implements Serializable {
            /**
             * salesPrice : 8
             * productPropertySpecValue : 咯囧囧囧
             * productNum : 3
             * img : http://okdi.oss-cn-beijing.aliyuncs.com/1516418958889.jpg
             * salesMemberSum : 2
             * productName : 测试003
             * productPrice : 10
             * productAmount : 24.0
             * discount : 1
             * productId : 326210996264960
             */

            private String salesPrice;
            private String productPropertySpecValue;
            private String productNum;
            private String img;
            private String salesMemberSum;
            private String productName;
            private String productType;
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
