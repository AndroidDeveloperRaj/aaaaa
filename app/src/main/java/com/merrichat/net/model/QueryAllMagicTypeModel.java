package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/3/31.
 */

public class QueryAllMagicTypeModel extends Response {  //魔拍类型

    public Data data;

    public class Data {
        public List<MagicDataType> list;
    }

    public class MagicDataType {
        public String createTime;
        public String id;  // id
        public String sortId;  //排序ID
        public String typeName;  //魔拍类型名称
        public String updateTime;

    }


}
