package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/1/10.
 */

public class QueryReportingTypesModel extends Response {

//    {
//        "data": [
//                    {
//                        "id": 320401258176512,
//                        "typeName": "违反法律法规1",
//                        "createTime": 1514532966566,
//                        "updateTime": 1514950675019,
//                        "number": 1
//                    },
//                    {
//                        "id": 320401289633792,
//                        "typeName": "违反app规则",
//                        "createTime": 1514532981929,
//                        "number": 2
//                    }
//            ],
//        "success": true
//    }

    public List<ReportingTypesItem> data;

    public class ReportingTypesItem {
        public long id;
        public String typeName;
        public long createTime;
        public long updateTime;
        public int number;
    }

}
