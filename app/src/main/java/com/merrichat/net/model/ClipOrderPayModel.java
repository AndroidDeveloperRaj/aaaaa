package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/1/4.
 */

public class ClipOrderPayModel extends Response {
    public String error_code;
    public String error_msg;
    public String page;
    public boolean hasnext;

    public String total;
    public String rows;
    public String data;


//    {
//            "success": false,
//            "error_code": "err.cash.201",
//            "error_msg": "可用余额不足",
//            "page": "0",
//            "hasnext": false,
//            "rows": "0",
//            "total": "0",
//            "data": ""
//    }


}
