package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/1/9.
 */

public class GetCommunicationFeeModel extends Response {
//    {
//        "data": {
//        "list": {
//                    "boy": 0,
//                    "createTime": 1522118097691,
//                    "girl": 0,
//                    "goddess": 0,
//                    "id": 336308430823424,
//                    "menofGod": 0,
//                    "type": 1,
//                    "unit": "min",
//                    "updateTime": 1522118097691
//            }
//        },
//        "success": true
//    }

    public Data data;

    public class Data {
        public listItems list;
    }

    public class listItems {
        public double boy;
        public long createTime;
        public double girl;
        public double goddess;
        public long id;
        public double menofGod;
        public int type;
        public String unit;
        public long updateTime;

    }
}
