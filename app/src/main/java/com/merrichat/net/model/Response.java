package com.merrichat.net.model;

import java.io.Serializable;

public class Response implements Serializable {
    public boolean success;
    public String message;
    public String msg;
    public int errCode;
    public String errSubcode;
}
