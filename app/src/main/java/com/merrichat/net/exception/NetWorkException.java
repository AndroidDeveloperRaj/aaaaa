package com.merrichat.net.exception;

import java.io.IOException;

public class NetWorkException extends IOException {
    public NetWorkException() {
    }

    public NetWorkException(String message) {
        super(message);
    }
}
