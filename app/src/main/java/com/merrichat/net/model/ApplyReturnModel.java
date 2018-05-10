package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/1/27.
 */

public class ApplyReturnModel extends Response {
    public Data data;

    public class Data {
        public String message;
        public boolean success;

    }

}
