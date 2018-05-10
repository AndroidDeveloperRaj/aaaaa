package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/1/26.
 */

public class CreateGroupOfModel extends Response {
    public Data data;

    public class Data {
        public boolean success;
        public String message;
        public String cid;
    }
}
