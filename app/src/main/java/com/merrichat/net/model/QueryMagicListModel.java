package com.merrichat.net.model;

import java.util.List;

/**
 * Created by wangweiwei on 2018/3/31.
 */

public class QueryMagicListModel extends Response {

    public Data data;

    public class Data {
        public List<MagicType> list;
    }

    public static class MagicType {
        public String id;  // id
        public String sortId;  //排序ID
        public String listName;  //列表名称
        public String magicType;  //魔拍类型
        public String status;   //是否有效0：无效 1：有效 允许值: 0, 1
        public String imgUrl;  //道具下载地址
        public String createTime;  //创建地址
        public String thumbnailUrl;  //道具缩略图

    }


}
