package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/1/20.
 */

public class WaiteGroupBuyModel extends Response {

//        private Long orderId; //订单号
//        private Long tid; //支付系统交易号
//        /**
//         * 拼团信息
//         */
//        private Long serialNumber =0L ;//拼团编号 初始化sericalNumber字段,以防止没有此字段的时候,返回给手机端一个null值
//        private Long seriaCreateMemberId;//创建团购人id 购买第一人
//        private String seriaCreateMemberName;//创建团购人名字
//        private String seriaCreateMemberUrl; //创建团购人的头像
//        private List<MxSerialMemberInfo> serialMember;//拼团人员
//        /**
//         * 发布人、卖家信息
//         */
//        private Long shopId;    //    群id或个人id
//        private Long shopMemberId;//  卖家(群管) memberid
//        private String shopName;//    卖家 名称
//        private String shopMobile;//  卖家 电话
//        private Long inAccountId;//   卖家/群 进账
//        private String shopUrl; //卖家头像;
//        /**
//         * 买家信息
//         */
//        private Long memberId;//     买家id
//        private String memberPhone;//买家手机号
//        private String memberName;// 买家名称
//        private Long outAccountId;// 买家账户
//        private String memberUrl; //买家头像
//        /**
//         * 收货人信息
//         */
//        private String addresseeName;//    收货人姓名
//        private String addresseePhone;//   收货人电话
//        private Long addresseeTownId;//    收货人乡镇id
//        private String addresseeTownName;//收货人省市区县
//        private String addresseeDetailed;//收件人完整地址（省市区县+详细）
//        private String addresseeAddress;// 收货人的详细地址
//        private BigDecimal addresseeLongitude;//收货人经度
//        private BigDecimal addresseeLatitude;// 收货人纬度
//        /**
//         * 订单各种状态
//         */
//        private Short productType;     //下单入口类别       1:正常, 2:拼团 ,3:参团
//        private Short transactionType; //交易类型          1:个人，2:c2c群，3:b2c(微商)群
//        private Short orderStatus;//买家订单状态            0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 ,8仲裁
//        private Short shopOrderStatus;//shop(卖家)订单状态  0待成团, 1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 ,8仲裁
//        private Short iquidateStatus;//订单清算状态         0未清算， 1已清算（返利完成，退款完成,取消订单完成===相当于 订单结束）
//        private Short serialStatus;//拼团    0拼团中，1拼团成功，2拼团失败
//        private Short sendGoods;  //是否发货  0否,1是
//        private Short cancel;     //是否取消  0否,1是
//        private Short signParcels;//是否签收  0未，1已签收
//        /**
//         * 申请退款和仲裁
//         */
//        private Short arbitrateStatus;   //申请仲裁状态状态标识  0否  1是
//        private String arbitrateCause;   //仲裁原因
//        private String[] arbitrateUrls;  //仲裁图片
//
//
//        private Short returnPay;      // 0退款中,1已退款，2拒绝退款
//        private Short returnStatus;   //申请退款状态标识  0否  1是
//        private String reasonText;    //商铺退款原因
//
//        private Short isReturnProduct;//该订单是否申请退货   0否  1是
//        private Short returnProduct;//退货状态   0否，1已退
//        private String returnReason;  //买家退货原因
//        private String[] returnUrls; //退货图片
//        private Short buyerDeleteMark;//买家删除标志 0未删除1删除
//        private Short sellerDeleteMark;//卖家删除标志0未删除1删除
//        /**
//         * 订单商品信息
//         */
//        private List<MxOrderItem> orderItemList;//订单下商品Item
//        /**
//         * 支付信息
//         */
//        private Short paymentStatus;       //订单支付状态  1未付款 ,2已付款 ,3支付失败 ,4支付中, 5支付取消,
//        private Short paymentType;          //支付方式     1支付宝, 2微信支付 ,3余额支付
//        private BigDecimal deliveryFee;     //配送费用     运费
//        private BigDecimal totalAmount;     //实际总金额 = 配送费用运费 + 订单下商品总金额
//        private BigDecimal paymentAmount;   //实际支付金额
//        private BigDecimal returnPayAmount; //退款总金额
//        private BigDecimal guaranteeMoney;  //交易质保金
//        private BigDecimal commissionMoney; //交易佣金
//
//        /**
//         * 订单状态变化时间
//         */
//        private Short timeLimit;    //倒计时
//        private Date createdTime;   //订单创建时间
//        private Date cancelTime;    //取消订单时间
//        private Date serialTime;    //拼团时间
//        private Date offered;       //申请试用装时
//        private Date paymentTime;   //支付时间
//        private Date sendTime =new Date();      //开始发货时间
//        private Date configReceiptTime = new Date();//确认收货时间
//        private Date applyReturnTime;  //客户申请退货时间
//        private Date agreeReturnTime;  //商家同意退货时间
//        private Date returnTime;       //客户申请退款时间
//        private Date agreeTime;        //商家同意退款时间
//        private String sendStatusUpdateTime; //自动变更确认收货时间   如果买家没有确认收货
//        private String iquidateTime;         //自动清算时间
//        private Date lastModifiedTime;       //订单更新时间
//        private String lastModifiedTimeReason;//订单更新时间原因    自定义便于查找订单bug
//        /**
//         * 订单物流信息
//         */
//        private Short sendType;           //发货方式 1快递,2自取,3送货上门
//        private MxOrderNet orderNet;      //订单下快递网络公司 == 店铺发货
//        private MxOrderNet returnOrderNet;//订单下快递网络公司 == 客户退货
//
//        private String buyerMessage;//买家留言
//        private String sellerMessage;//卖家留言


    public List<Data> data;

    public class Data {   // 参数详情，参见  WaiteGroupBuyDetailModel.java 类
        public String shopMemberId;
        public String shopMemberName;
        public String memberId;   // 卖家memberid
        public List<OrderItemList> orderItemList;   //详细信息
        public List<serialMember> serialMemberList;   //详细信息
        public int reStatus;   //0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
        public int iquidateStatus;  //订单列表，详情  ，除了联系卖家、买家，不能申请退款，仲裁，等操作
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
        public String comfigReceiptTime;
        public String addresseePhone;  //收件人手机号
        public String shopUrl;
        public String sendType;
        public String orderId;
        public double deliveryFee;      //配送费用     运费
        public String paymentType;      //支付方式     1支付宝, 2微信支付 ,3余额支付
        public String reStatusText;

        public String returnReason;  //买家退货原因


    }

    public class OrderItemList {
        public Long productId;     //商品id
        public String productName; //商品名
        public String img;         //商品图片地址
        public Double productNum;  //商品数量
        public Double productPrice; //商品单价
        public Double discount;         //活动折扣比例
        public Double salesPrice;   //商品活动单价（包含拼团）
        public int salesMemberSum;   //拼团人数
        public Double productAmount; //商品售价和
        public String productPropertySpecValue;//属性规格集合
    }


    //拼团的用户列表
    public class serialMember {
        public String createdTime;
        public String serialNumber;
        public String memberId;
        public String memberName;
        public String url;
    }


}
