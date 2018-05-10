package com.merrichat.net.model;

public class AllocateModel extends Response {

    public Data data;

    public class Data {
        public String receiver;
        public String socketIp;
        public String socketPort;
        public String httpPort;
    }
}
