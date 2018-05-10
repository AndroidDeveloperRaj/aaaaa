package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/3/5.
 */

public class QueryGoodFriendRequestModel extends Response {
    public Data data;

    public class Data{
        public int state;  //3为好友
    }
}
