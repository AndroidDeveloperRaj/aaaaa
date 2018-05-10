package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/1/5.
 */

public class AddGoodFriendsModel extends Response {
//    {
//        "data": {
//                "message": "请求成功！",
//                "suc": true
//        },
//        "success": true
//    }

    public Data data;

    public class Data {
        public String message;
        public boolean suc;
    }

}
