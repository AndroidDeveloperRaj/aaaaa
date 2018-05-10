package com.merrichat.net.exception;

public class ResultException extends Exception {
    public int errCode;
    public String msg;

    public ResultException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.msg = errMsg;
    }
}
