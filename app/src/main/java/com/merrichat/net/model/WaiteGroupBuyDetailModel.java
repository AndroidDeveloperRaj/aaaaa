package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/1/29.
 */

public class WaiteGroupBuyDetailModel extends Response {

    public Data data;

    public class Data {
        public String shopUrl;   //商店的URL
        public String shopMemberName;
        public String shopMemberId;//群主MemberId
        public String shopId;//群Id
        public int reStatus;   //0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
        public int returnPay;      // 0退款中,1已退款，2拒绝退款
        public long remainTime;   //拼团 剩余时间
        public long createdTime;    //订单创建时间
        public long serialTime;   //拼团时间
        public long paymentTime;   //支付时间
        public long sendTime;   //开始发货时间
        public String buyerMessage;  //买家留言
        public String serialNumber;  //拼团编号 初始化sericalNumber字段,以防止没有此字段的时候,返回给手机端一个null值
        public long configReceiptTime;  //确认收货时间
        public int transactionType;  //交易类型          1:个人，2:c2c群，3:b2c(微商)群
        public String addresseeName;  //收件人姓名
        public String addresseeDetailed;  //收货地址
        public String shopName;//    卖家 名称
        public String totalAmount;  //总共价钱
        public String memberName;// 买家名称
        public double productPrice;  //商品单价
        public long returnTime;  //发起退款时间
        public String addresseePhone;  //收件人手机号
        public String orderId;
        public int iquidateStatus;  //订单列表，详情  ，除了联系卖家、买家，不能申请退款，仲裁，等操作
        public double deliveryFee;      //配送费用     运费
        public String paymentType;      //支付方式     1支付宝, 2微信支付 ,3余额支付
        public List<WaiteGroupBuyModel.OrderItemList> orderItemList;   //详细信息
        public List<WaiteGroupBuyModel.serialMember> serialMember;   //详细信息
        public List<String> returnUrls; // 图片证据
        public String returnReason;     //订单状态
        public String reStatusText;  //仲裁状态
        public String reasonText;  //退款原因
        public String sendType;
    }
}
