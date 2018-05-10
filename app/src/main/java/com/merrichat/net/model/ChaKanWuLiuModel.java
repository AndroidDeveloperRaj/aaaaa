package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 17/9/25.
 * 查看物流model
 */

public class ChaKanWuLiuModel implements Serializable {

    private String log_time;

    private String log_info;


    public void setLog_time(String log_time) {
        this.log_time = log_time;
    }

    public String getLog_time() {
        return this.log_time;
    }

    public void setLog_info(String log_info) {
        this.log_info = log_info;
    }

    public String getLog_info() {
        return this.log_info;
    }

}
