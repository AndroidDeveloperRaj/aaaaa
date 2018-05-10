package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/1/27.
 */

public class QueryOrderModel extends Response {

    public List<Data> data;

//    "data": [{
//                "addresseeName": "小龙女",
//                "addresseeDetailed": "北京市北京市海淀区华奥中心",
//                "shopName": "阿尔法",
//                "sendTime": 1517048117064,
//                "totalAmount": 0.01,
//                "buyerMessage": "未留言",
//                "serialNumber": 0,
//                "memberName": "阿尔法",
//                "comfigReceiptTime": 1517048117064,
//                "deliveryFee": 0,
//                "orderId": 325664176463872,
//                "configReceiptTime": 1517048117064
//            }
//    }]

    public  class Data {
        public String addresseeName;
        public String addresseeDetailed;
        public String shopName;
        public String sendTime;
        public String totalAmount;
        public String buyerMessage;
        public String serialNumber;
        public String memberName;
        public String comfigReceiptTime;
        public String deliveryFee;
        public String orderId;
        public String configReceiptTime;

    }


    public static class QueryOrderRequestParams {
        public String orderStatus;
        public String key;
        public String groupId;
        public String memberId;
        public String orderId;  //订单id
        public String pageSize;   //每页的条数
        public String currentPage;   //当前页数
        public String serialNumber;


    }
}
