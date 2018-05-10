package com.merrichat.net.model;

import java.util.List;

public class EncounterPersonModel extends Response {

//
//    {
//        "data": {
//        "list": [
    //        {
    //                "age": 0,
    //                "constellations": "",
    //                "createTime": 1514428472511,
    //                "distance": 21000,
    //                "encounterId": 317831596261376,
    //                "gender": 1,
    //                "headImgUrl": "http://cas.okdit.net/nfs_data/mob/head/317831596261376.jpg",
    //                "id": 318421739028480,
    //                "memberId": 318421739028480,
    //                "memberName": "大风哥",
    //                "mobile": "",
    //                "signature": "",
    //                "updateTime": 1514428472511
    //        },
    //        {
    //                "age": 0,
    //                "constellations": "",
    //                "createTime": 1515206755748,
    //                "distance": 0.0000002671850131704045,
    //                "encounterId": 315917552893952,
    //                "gender": 1,
    //                "headImgUrl": "http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1514377032694",
    //                "id": 321814296051713,
    //                "memberId": 318421739028480,
    //                "memberName": "锦衣卫",
    //                "mobile": "",
    //                "signature": "",
    //                "updateTime": 1515206755748
    //        }
    //    ],
//        "success": true
//    },
//        "success": true
//    }

    public Data data;

    public class Data {
        public List<EncounterBody> list;

    }

    public class EncounterBody {
        public int age;
        public String constellations;
        public long createTime;
        public String distance;
        public long encounterId;
        public Object gender;
        public String headImgUrl;
        public long id;
        public long memberId;
        public String memberName;
        public String signature;
        public long updateTime;
        public String mobile;
    }

}
