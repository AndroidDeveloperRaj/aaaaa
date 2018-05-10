package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/1/4.
 */

public class QueryWalletInfoModel extends Response {
    //    {
//            "total": "0",
//            "hasnext": false,
//            "page": "0",
//            "data": {
//                "couponsNum": "0",
//                "couponsMoney": "0",
//                "giftBalance": "0.00",
//                "availableCouponNum": "0",
//                "couponBalance": "0.00",
//                "isSetPassword": "1",
//                "cashBalance": "2675.90"
//            },
//            "error_code": "",
//            "success": true,
//            "error_msg": "",
//            "rows": "0"
//    }
    public String total;
    public String error_code;
    public String error_msg;
    public String rows;
    public String page;
    public boolean hasnext;

    public Data data;


    public class Data {
        public String couponsNum;           //所有可用红包数
        public String couponsMoney;         //所有红包金额
        public String giftBalance;          //金币余额
        public String availableCouponNum;   //（当前订单下）可用红包数
        public String couponBalance;        //账户余额
        public String isSetPassword;        //是否设置密码
        public String cashBalance;

    }


}
