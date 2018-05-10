package com.merrichat.net.model;

import java.util.List;

/**
 * 下单成功实体类
 * Created by amssy on 18/1/29.
 */

public class OrderModel {


    /**
     * data : {"message":"下单成功！","flag":3,"mxOrderInfo":{"addresseeAddress":"华奥斯卡","addresseeDetailed":"北京市北京市海淀区华奥斯卡","addresseeLatitude":0,"addresseeLongitude":0,"addresseeName":"测试1","addresseePhone":"15888888888","addresseeTownId":326023018045440,"addresseeTownName":"北京市北京市海淀区","agreeReturnTime":"","agreeTime":"","applyReturnTime":"","arbitrateCause":"","arbitrateStatus":0,"arbitrateUrls":[],"buyerDeleteMark":0,"buyerMessage":"未留言","cancel":0,"cancelTime":"","comfigReceiptTime":1517213746483,"commissionMoney":0,"configReceiptTime":1517213746483,"createdTime":1517213746486,"deliveryFee":0.01,"guaranteeMoney":0,"inAccountId":163010360051528704,"iquidateStatus":0,"iquidateTime":"","isReturnProduct":0,"lastModifiedTime":1517213746486,"lastModifiedTimeReason":"用户提交订单！","memberId":323842518335488,"memberName":"尼古拉斯☆赵四","memberPhone":"15123757720","memberUrl":"","offered":"","orderId":326023261315072,"orderItemList":[{"discount":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg","productAmount":0.01,"productId":326022856564736,"productName":"测试商品1","productNum":1,"productPrice":0.01,"productPropertySpecValue":"面包非常好吃，北京特产，送礼首选 保证好吃","salesMemberSum":3,"salesPrice":0.01}],"orderNet":{"expressCode":"","expressLogo":"","kf":"","netId":0,"netName":"","waybillNumber":""},"orderStatus":1,"outAccountId":161921258640912384,"paymentAmount":0,"paymentStatus":0,"paymentTime":"","paymentType":0,"productType":1,"reasonText":"","returnOrderNet":{"$ref":"$.data.mxOrderInfo.orderNet"},"returnPay":0,"returnPayAmount":0,"returnProduct":0,"returnReason":"","returnStatus":0,"returnTime":"","returnUrls":[],"sellerDeleteMark":0,"sellerMessage":"未留言","sendGoods":0,"sendStatusUpdateTime":"","sendTime":1517213746483,"sendType":0,"seriaCreateMemberId":0,"seriaCreateMemberName":"","seriaCreateMemberUrl":"","serialMember":[],"serialNumber":0,"serialStatus":0,"serialTime":"","shopId":326020860076032,"shopMemberId":323842518335488,"shopMobile":"15123757720","shopName":"尼古拉斯☆赵四","shopOrderStatus":1,"shopUrl":"http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037","signParcels":0,"tid":0,"timeLimit":0,"totalAmount":0.02,"transactionType":2}}
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
         * message : 下单成功！
         * flag : 3
         * mxOrderInfo : {"addresseeAddress":"华奥斯卡","addresseeDetailed":"北京市北京市海淀区华奥斯卡","addresseeLatitude":0,"addresseeLongitude":0,"addresseeName":"测试1","addresseePhone":"15888888888","addresseeTownId":326023018045440,"addresseeTownName":"北京市北京市海淀区","agreeReturnTime":"","agreeTime":"","applyReturnTime":"","arbitrateCause":"","arbitrateStatus":0,"arbitrateUrls":[],"buyerDeleteMark":0,"buyerMessage":"未留言","cancel":0,"cancelTime":"","comfigReceiptTime":1517213746483,"commissionMoney":0,"configReceiptTime":1517213746483,"createdTime":1517213746486,"deliveryFee":0.01,"guaranteeMoney":0,"inAccountId":163010360051528704,"iquidateStatus":0,"iquidateTime":"","isReturnProduct":0,"lastModifiedTime":1517213746486,"lastModifiedTimeReason":"用户提交订单！","memberId":323842518335488,"memberName":"尼古拉斯☆赵四","memberPhone":"15123757720","memberUrl":"","offered":"","orderId":326023261315072,"orderItemList":[{"discount":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg","productAmount":0.01,"productId":326022856564736,"productName":"测试商品1","productNum":1,"productPrice":0.01,"productPropertySpecValue":"面包非常好吃，北京特产，送礼首选 保证好吃","salesMemberSum":3,"salesPrice":0.01}],"orderNet":{"expressCode":"","expressLogo":"","kf":"","netId":0,"netName":"","waybillNumber":""},"orderStatus":1,"outAccountId":161921258640912384,"paymentAmount":0,"paymentStatus":0,"paymentTime":"","paymentType":0,"productType":1,"reasonText":"","returnOrderNet":{"$ref":"$.data.mxOrderInfo.orderNet"},"returnPay":0,"returnPayAmount":0,"returnProduct":0,"returnReason":"","returnStatus":0,"returnTime":"","returnUrls":[],"sellerDeleteMark":0,"sellerMessage":"未留言","sendGoods":0,"sendStatusUpdateTime":"","sendTime":1517213746483,"sendType":0,"seriaCreateMemberId":0,"seriaCreateMemberName":"","seriaCreateMemberUrl":"","serialMember":[],"serialNumber":0,"serialStatus":0,"serialTime":"","shopId":326020860076032,"shopMemberId":323842518335488,"shopMobile":"15123757720","shopName":"尼古拉斯☆赵四","shopOrderStatus":1,"shopUrl":"http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037","signParcels":0,"tid":0,"timeLimit":0,"totalAmount":0.02,"transactionType":2}
         */

        private String message;
        private int flag;
        private MxOrderInfoBean mxOrderInfo;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public MxOrderInfoBean getMxOrderInfo() {
            return mxOrderInfo;
        }

        public void setMxOrderInfo(MxOrderInfoBean mxOrderInfo) {
            this.mxOrderInfo = mxOrderInfo;
        }

        public static class MxOrderInfoBean {
            /**
             * addresseeAddress : 华奥斯卡
             * addresseeDetailed : 北京市北京市海淀区华奥斯卡
             * addresseeLatitude : 0
             * addresseeLongitude : 0
             * addresseeName : 测试1
             * addresseePhone : 15888888888
             * addresseeTownId : 326023018045440
             * addresseeTownName : 北京市北京市海淀区
             * agreeReturnTime :
             * agreeTime :
             * applyReturnTime :
             * arbitrateCause :
             * arbitrateStatus : 0
             * arbitrateUrls : []
             * buyerDeleteMark : 0
             * buyerMessage : 未留言
             * cancel : 0
             * cancelTime :
             * comfigReceiptTime : 1517213746483
             * commissionMoney : 0.0
             * configReceiptTime : 1517213746483
             * createdTime : 1517213746486
             * deliveryFee : 0.01
             * guaranteeMoney : 0.0
             * inAccountId : 163010360051528704
             * iquidateStatus : 0
             * iquidateTime :
             * isReturnProduct : 0
             * lastModifiedTime : 1517213746486
             * lastModifiedTimeReason : 用户提交订单！
             * memberId : 323842518335488
             * memberName : 尼古拉斯☆赵四
             * memberPhone : 15123757720
             * memberUrl :
             * offered :
             * orderId : 326023261315072
             * orderItemList : [{"discount":1,"img":"http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg","productAmount":0.01,"productId":326022856564736,"productName":"测试商品1","productNum":1,"productPrice":0.01,"productPropertySpecValue":"面包非常好吃，北京特产，送礼首选 保证好吃","salesMemberSum":3,"salesPrice":0.01}]
             * orderNet : {"expressCode":"","expressLogo":"","kf":"","netId":0,"netName":"","waybillNumber":""}
             * orderStatus : 1
             * outAccountId : 161921258640912384
             * paymentAmount : 0
             * paymentStatus : 0
             * paymentTime :
             * paymentType : 0
             * productType : 1
             * reasonText :
             * returnOrderNet : {"$ref":"$.data.mxOrderInfo.orderNet"}
             * returnPay : 0
             * returnPayAmount : 0
             * returnProduct : 0
             * returnReason :
             * returnStatus : 0
             * returnTime :
             * returnUrls : []
             * sellerDeleteMark : 0
             * sellerMessage : 未留言
             * sendGoods : 0
             * sendStatusUpdateTime :
             * sendTime : 1517213746483
             * sendType : 0
             * seriaCreateMemberId : 0
             * seriaCreateMemberName :
             * seriaCreateMemberUrl :
             * serialMember : []
             * serialNumber : 0
             * serialStatus : 0
             * serialTime :
             * shopId : 326020860076032
             * shopMemberId : 323842518335488
             * shopMobile : 15123757720
             * shopName : 尼古拉斯☆赵四
             * shopOrderStatus : 1
             * shopUrl : http://cas.okdit.net/nfs_data/mob/head/323842518335488.jpg?time=1516174112037
             * signParcels : 0
             * tid : 0
             * timeLimit : 0
             * totalAmount : 0.02
             * transactionType : 2
             */

            private String addresseeAddress;
            private String addresseeDetailed;
            private int addresseeLatitude;
            private int addresseeLongitude;
            private String addresseeName;
            private String addresseePhone;
            private long addresseeTownId;
            private String addresseeTownName;
            private String agreeReturnTime;
            private String agreeTime;
            private String applyReturnTime;
            private String arbitrateCause;
            private int arbitrateStatus;
            private int buyerDeleteMark;
            private String buyerMessage;
            private int cancel;
            private String cancelTime;
            private long comfigReceiptTime;
            private double commissionMoney;
            private long configReceiptTime;
            private long createdTime;
            private double deliveryFee;
            private double guaranteeMoney;
            private long inAccountId;
            private int iquidateStatus;
            private String iquidateTime;
            private int isReturnProduct;
            private long lastModifiedTime;
            private String lastModifiedTimeReason;
            private long memberId;
            private String memberName;
            private String memberPhone;
            private String memberUrl;
            private String offered;
            private long orderId;
            private OrderNetBean orderNet;
            private int orderStatus;
            private long outAccountId;
            private int paymentAmount;
            private int paymentStatus;
            private String paymentTime;
            private int paymentType;
            private int productType;
            private String reasonText;
            private int returnPay;
            private int returnPayAmount;
            private int returnProduct;
            private String returnReason;
            private int returnStatus;
            private String returnTime;
            private int sellerDeleteMark;
            private String sellerMessage;
            private int sendGoods;
            private String sendStatusUpdateTime;
            private long sendTime;
            private int sendType;
            private int seriaCreateMemberId;
            private String seriaCreateMemberName;
            private String seriaCreateMemberUrl;
            private int serialNumber;
            private int serialStatus;
            private String serialTime;
            private long shopId;
            private long shopMemberId;
            private String shopMobile;
            private String shopName;
            private int shopOrderStatus;
            private String shopUrl;
            private int signParcels;
            private int tid;
            private int timeLimit;
            private double totalAmount;
            private int transactionType;
            private List<?> arbitrateUrls;
            private List<OrderItemListBean> orderItemList;
            private List<?> returnUrls;
            private List<?> serialMember;

            public String getAddresseeAddress() {
                return addresseeAddress;
            }

            public void setAddresseeAddress(String addresseeAddress) {
                this.addresseeAddress = addresseeAddress;
            }

            public String getAddresseeDetailed() {
                return addresseeDetailed;
            }

            public void setAddresseeDetailed(String addresseeDetailed) {
                this.addresseeDetailed = addresseeDetailed;
            }

            public int getAddresseeLatitude() {
                return addresseeLatitude;
            }

            public void setAddresseeLatitude(int addresseeLatitude) {
                this.addresseeLatitude = addresseeLatitude;
            }

            public int getAddresseeLongitude() {
                return addresseeLongitude;
            }

            public void setAddresseeLongitude(int addresseeLongitude) {
                this.addresseeLongitude = addresseeLongitude;
            }

            public String getAddresseeName() {
                return addresseeName;
            }

            public void setAddresseeName(String addresseeName) {
                this.addresseeName = addresseeName;
            }

            public String getAddresseePhone() {
                return addresseePhone;
            }

            public void setAddresseePhone(String addresseePhone) {
                this.addresseePhone = addresseePhone;
            }

            public long getAddresseeTownId() {
                return addresseeTownId;
            }

            public void setAddresseeTownId(long addresseeTownId) {
                this.addresseeTownId = addresseeTownId;
            }

            public String getAddresseeTownName() {
                return addresseeTownName;
            }

            public void setAddresseeTownName(String addresseeTownName) {
                this.addresseeTownName = addresseeTownName;
            }

            public String getAgreeReturnTime() {
                return agreeReturnTime;
            }

            public void setAgreeReturnTime(String agreeReturnTime) {
                this.agreeReturnTime = agreeReturnTime;
            }

            public String getAgreeTime() {
                return agreeTime;
            }

            public void setAgreeTime(String agreeTime) {
                this.agreeTime = agreeTime;
            }

            public String getApplyReturnTime() {
                return applyReturnTime;
            }

            public void setApplyReturnTime(String applyReturnTime) {
                this.applyReturnTime = applyReturnTime;
            }

            public String getArbitrateCause() {
                return arbitrateCause;
            }

            public void setArbitrateCause(String arbitrateCause) {
                this.arbitrateCause = arbitrateCause;
            }

            public int getArbitrateStatus() {
                return arbitrateStatus;
            }

            public void setArbitrateStatus(int arbitrateStatus) {
                this.arbitrateStatus = arbitrateStatus;
            }

            public int getBuyerDeleteMark() {
                return buyerDeleteMark;
            }

            public void setBuyerDeleteMark(int buyerDeleteMark) {
                this.buyerDeleteMark = buyerDeleteMark;
            }

            public String getBuyerMessage() {
                return buyerMessage;
            }

            public void setBuyerMessage(String buyerMessage) {
                this.buyerMessage = buyerMessage;
            }

            public int getCancel() {
                return cancel;
            }

            public void setCancel(int cancel) {
                this.cancel = cancel;
            }

            public String getCancelTime() {
                return cancelTime;
            }

            public void setCancelTime(String cancelTime) {
                this.cancelTime = cancelTime;
            }

            public long getComfigReceiptTime() {
                return comfigReceiptTime;
            }

            public void setComfigReceiptTime(long comfigReceiptTime) {
                this.comfigReceiptTime = comfigReceiptTime;
            }

            public double getCommissionMoney() {
                return commissionMoney;
            }

            public void setCommissionMoney(double commissionMoney) {
                this.commissionMoney = commissionMoney;
            }

            public long getConfigReceiptTime() {
                return configReceiptTime;
            }

            public void setConfigReceiptTime(long configReceiptTime) {
                this.configReceiptTime = configReceiptTime;
            }

            public long getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(long createdTime) {
                this.createdTime = createdTime;
            }

            public double getDeliveryFee() {
                return deliveryFee;
            }

            public void setDeliveryFee(double deliveryFee) {
                this.deliveryFee = deliveryFee;
            }

            public double getGuaranteeMoney() {
                return guaranteeMoney;
            }

            public void setGuaranteeMoney(double guaranteeMoney) {
                this.guaranteeMoney = guaranteeMoney;
            }

            public long getInAccountId() {
                return inAccountId;
            }

            public void setInAccountId(long inAccountId) {
                this.inAccountId = inAccountId;
            }

            public int getIquidateStatus() {
                return iquidateStatus;
            }

            public void setIquidateStatus(int iquidateStatus) {
                this.iquidateStatus = iquidateStatus;
            }

            public String getIquidateTime() {
                return iquidateTime;
            }

            public void setIquidateTime(String iquidateTime) {
                this.iquidateTime = iquidateTime;
            }

            public int getIsReturnProduct() {
                return isReturnProduct;
            }

            public void setIsReturnProduct(int isReturnProduct) {
                this.isReturnProduct = isReturnProduct;
            }

            public long getLastModifiedTime() {
                return lastModifiedTime;
            }

            public void setLastModifiedTime(long lastModifiedTime) {
                this.lastModifiedTime = lastModifiedTime;
            }

            public String getLastModifiedTimeReason() {
                return lastModifiedTimeReason;
            }

            public void setLastModifiedTimeReason(String lastModifiedTimeReason) {
                this.lastModifiedTimeReason = lastModifiedTimeReason;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public String getMemberPhone() {
                return memberPhone;
            }

            public void setMemberPhone(String memberPhone) {
                this.memberPhone = memberPhone;
            }

            public String getMemberUrl() {
                return memberUrl;
            }

            public void setMemberUrl(String memberUrl) {
                this.memberUrl = memberUrl;
            }

            public String getOffered() {
                return offered;
            }

            public void setOffered(String offered) {
                this.offered = offered;
            }

            public long getOrderId() {
                return orderId;
            }

            public void setOrderId(long orderId) {
                this.orderId = orderId;
            }

            public OrderNetBean getOrderNet() {
                return orderNet;
            }

            public void setOrderNet(OrderNetBean orderNet) {
                this.orderNet = orderNet;
            }

            public int getOrderStatus() {
                return orderStatus;
            }

            public void setOrderStatus(int orderStatus) {
                this.orderStatus = orderStatus;
            }

            public long getOutAccountId() {
                return outAccountId;
            }

            public void setOutAccountId(long outAccountId) {
                this.outAccountId = outAccountId;
            }

            public int getPaymentAmount() {
                return paymentAmount;
            }

            public void setPaymentAmount(int paymentAmount) {
                this.paymentAmount = paymentAmount;
            }

            public int getPaymentStatus() {
                return paymentStatus;
            }

            public void setPaymentStatus(int paymentStatus) {
                this.paymentStatus = paymentStatus;
            }

            public String getPaymentTime() {
                return paymentTime;
            }

            public void setPaymentTime(String paymentTime) {
                this.paymentTime = paymentTime;
            }

            public int getPaymentType() {
                return paymentType;
            }

            public void setPaymentType(int paymentType) {
                this.paymentType = paymentType;
            }

            public int getProductType() {
                return productType;
            }

            public void setProductType(int productType) {
                this.productType = productType;
            }

            public String getReasonText() {
                return reasonText;
            }

            public void setReasonText(String reasonText) {
                this.reasonText = reasonText;
            }

            public int getReturnPay() {
                return returnPay;
            }

            public void setReturnPay(int returnPay) {
                this.returnPay = returnPay;
            }

            public int getReturnPayAmount() {
                return returnPayAmount;
            }

            public void setReturnPayAmount(int returnPayAmount) {
                this.returnPayAmount = returnPayAmount;
            }

            public int getReturnProduct() {
                return returnProduct;
            }

            public void setReturnProduct(int returnProduct) {
                this.returnProduct = returnProduct;
            }

            public String getReturnReason() {
                return returnReason;
            }

            public void setReturnReason(String returnReason) {
                this.returnReason = returnReason;
            }

            public int getReturnStatus() {
                return returnStatus;
            }

            public void setReturnStatus(int returnStatus) {
                this.returnStatus = returnStatus;
            }

            public String getReturnTime() {
                return returnTime;
            }

            public void setReturnTime(String returnTime) {
                this.returnTime = returnTime;
            }

            public int getSellerDeleteMark() {
                return sellerDeleteMark;
            }

            public void setSellerDeleteMark(int sellerDeleteMark) {
                this.sellerDeleteMark = sellerDeleteMark;
            }

            public String getSellerMessage() {
                return sellerMessage;
            }

            public void setSellerMessage(String sellerMessage) {
                this.sellerMessage = sellerMessage;
            }

            public int getSendGoods() {
                return sendGoods;
            }

            public void setSendGoods(int sendGoods) {
                this.sendGoods = sendGoods;
            }

            public String getSendStatusUpdateTime() {
                return sendStatusUpdateTime;
            }

            public void setSendStatusUpdateTime(String sendStatusUpdateTime) {
                this.sendStatusUpdateTime = sendStatusUpdateTime;
            }

            public long getSendTime() {
                return sendTime;
            }

            public void setSendTime(long sendTime) {
                this.sendTime = sendTime;
            }

            public int getSendType() {
                return sendType;
            }

            public void setSendType(int sendType) {
                this.sendType = sendType;
            }

            public int getSeriaCreateMemberId() {
                return seriaCreateMemberId;
            }

            public void setSeriaCreateMemberId(int seriaCreateMemberId) {
                this.seriaCreateMemberId = seriaCreateMemberId;
            }

            public String getSeriaCreateMemberName() {
                return seriaCreateMemberName;
            }

            public void setSeriaCreateMemberName(String seriaCreateMemberName) {
                this.seriaCreateMemberName = seriaCreateMemberName;
            }

            public String getSeriaCreateMemberUrl() {
                return seriaCreateMemberUrl;
            }

            public void setSeriaCreateMemberUrl(String seriaCreateMemberUrl) {
                this.seriaCreateMemberUrl = seriaCreateMemberUrl;
            }

            public int getSerialNumber() {
                return serialNumber;
            }

            public void setSerialNumber(int serialNumber) {
                this.serialNumber = serialNumber;
            }

            public int getSerialStatus() {
                return serialStatus;
            }

            public void setSerialStatus(int serialStatus) {
                this.serialStatus = serialStatus;
            }

            public String getSerialTime() {
                return serialTime;
            }

            public void setSerialTime(String serialTime) {
                this.serialTime = serialTime;
            }

            public long getShopId() {
                return shopId;
            }

            public void setShopId(long shopId) {
                this.shopId = shopId;
            }

            public long getShopMemberId() {
                return shopMemberId;
            }

            public void setShopMemberId(long shopMemberId) {
                this.shopMemberId = shopMemberId;
            }

            public String getShopMobile() {
                return shopMobile;
            }

            public void setShopMobile(String shopMobile) {
                this.shopMobile = shopMobile;
            }

            public String getShopName() {
                return shopName;
            }

            public void setShopName(String shopName) {
                this.shopName = shopName;
            }

            public int getShopOrderStatus() {
                return shopOrderStatus;
            }

            public void setShopOrderStatus(int shopOrderStatus) {
                this.shopOrderStatus = shopOrderStatus;
            }

            public String getShopUrl() {
                return shopUrl;
            }

            public void setShopUrl(String shopUrl) {
                this.shopUrl = shopUrl;
            }

            public int getSignParcels() {
                return signParcels;
            }

            public void setSignParcels(int signParcels) {
                this.signParcels = signParcels;
            }

            public int getTid() {
                return tid;
            }

            public void setTid(int tid) {
                this.tid = tid;
            }

            public int getTimeLimit() {
                return timeLimit;
            }

            public void setTimeLimit(int timeLimit) {
                this.timeLimit = timeLimit;
            }

            public double getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(double totalAmount) {
                this.totalAmount = totalAmount;
            }

            public int getTransactionType() {
                return transactionType;
            }

            public void setTransactionType(int transactionType) {
                this.transactionType = transactionType;
            }

            public List<?> getArbitrateUrls() {
                return arbitrateUrls;
            }

            public void setArbitrateUrls(List<?> arbitrateUrls) {
                this.arbitrateUrls = arbitrateUrls;
            }

            public List<OrderItemListBean> getOrderItemList() {
                return orderItemList;
            }

            public void setOrderItemList(List<OrderItemListBean> orderItemList) {
                this.orderItemList = orderItemList;
            }

            public List<?> getReturnUrls() {
                return returnUrls;
            }

            public void setReturnUrls(List<?> returnUrls) {
                this.returnUrls = returnUrls;
            }

            public List<?> getSerialMember() {
                return serialMember;
            }

            public void setSerialMember(List<?> serialMember) {
                this.serialMember = serialMember;
            }

            public static class OrderNetBean {
                /**
                 * expressCode :
                 * expressLogo :
                 * kf :
                 * netId : 0
                 * netName :
                 * waybillNumber :
                 */

                private String expressCode;
                private String expressLogo;
                private String kf;
                private int netId;
                private String netName;
                private String waybillNumber;

                public String getExpressCode() {
                    return expressCode;
                }

                public void setExpressCode(String expressCode) {
                    this.expressCode = expressCode;
                }

                public String getExpressLogo() {
                    return expressLogo;
                }

                public void setExpressLogo(String expressLogo) {
                    this.expressLogo = expressLogo;
                }

                public String getKf() {
                    return kf;
                }

                public void setKf(String kf) {
                    this.kf = kf;
                }

                public int getNetId() {
                    return netId;
                }

                public void setNetId(int netId) {
                    this.netId = netId;
                }

                public String getNetName() {
                    return netName;
                }

                public void setNetName(String netName) {
                    this.netName = netName;
                }

                public String getWaybillNumber() {
                    return waybillNumber;
                }

                public void setWaybillNumber(String waybillNumber) {
                    this.waybillNumber = waybillNumber;
                }
            }

            public static class ReturnOrderNetBean {
                /**
                 * $ref : $.data.mxOrderInfo.orderNet
                 */

                private String $ref;

                public String get$ref() {
                    return $ref;
                }

                public void set$ref(String $ref) {
                    this.$ref = $ref;
                }
            }

            public static class OrderItemListBean {
                /**
                 * discount : 1
                 * img : http://okdi.oss-cn-beijing.aliyuncs.com/1516946736628.jpg
                 * productAmount : 0.01
                 * productId : 326022856564736
                 * productName : 测试商品1
                 * productNum : 1
                 * productPrice : 0.01
                 * productPropertySpecValue : 面包非常好吃，北京特产，送礼首选 保证好吃
                 * salesMemberSum : 3
                 * salesPrice : 0.01
                 */

                private int discount;
                private String img;
                private double productAmount;
                private long productId;
                private String productName;
                private int productNum;
                private double productPrice;
                private String productPropertySpecValue;
                private int salesMemberSum;
                private double salesPrice;

                public int getDiscount() {
                    return discount;
                }

                public void setDiscount(int discount) {
                    this.discount = discount;
                }

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }

                public double getProductAmount() {
                    return productAmount;
                }

                public void setProductAmount(double productAmount) {
                    this.productAmount = productAmount;
                }

                public long getProductId() {
                    return productId;
                }

                public void setProductId(long productId) {
                    this.productId = productId;
                }

                public String getProductName() {
                    return productName;
                }

                public void setProductName(String productName) {
                    this.productName = productName;
                }

                public int getProductNum() {
                    return productNum;
                }

                public void setProductNum(int productNum) {
                    this.productNum = productNum;
                }

                public double getProductPrice() {
                    return productPrice;
                }

                public void setProductPrice(double productPrice) {
                    this.productPrice = productPrice;
                }

                public String getProductPropertySpecValue() {
                    return productPropertySpecValue;
                }

                public void setProductPropertySpecValue(String productPropertySpecValue) {
                    this.productPropertySpecValue = productPropertySpecValue;
                }

                public int getSalesMemberSum() {
                    return salesMemberSum;
                }

                public void setSalesMemberSum(int salesMemberSum) {
                    this.salesMemberSum = salesMemberSum;
                }

                public double getSalesPrice() {
                    return salesPrice;
                }

                public void setSalesPrice(double salesPrice) {
                    this.salesPrice = salesPrice;
                }
            }
        }
    }
}
