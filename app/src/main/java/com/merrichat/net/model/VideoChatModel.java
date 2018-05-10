package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/1/3.
 */

public class VideoChatModel extends Response{

    public Data data;

    public class Data{
        public String httpIp;
        public String httpPort;
        public String socketIp;
        public int socketPort;

    }

}
