package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by AMSSY1 on 2018/1/25.
 */

public class AddressModel implements Serializable{

    /**
     * id : 123456
     * name : 11
     * mobile : 18317773784
     * recAddress : 河南省郑州市金水区
     * detAddress : 大中华村
     */

    private String id;
    private String name;
    private String mobile;
    private String recAddress;
    private String detAddress;
    private String type;
    private String accountAddr;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountAddr() {
        return accountAddr;
    }

    public void setAccountAddr(String accountAddr) {
        this.accountAddr = accountAddr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRecAddress() {
        return recAddress;
    }

    public void setRecAddress(String recAddress) {
        this.recAddress = recAddress;
    }

    public String getDetAddress() {
        return detAddress;
    }

    public void setDetAddress(String detAddress) {
        this.detAddress = detAddress;
    }
}
