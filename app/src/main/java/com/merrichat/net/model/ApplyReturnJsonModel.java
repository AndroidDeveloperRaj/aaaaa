package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2018/1/30.
 * <p>
 * 销售订单--取消订单参数
 */

public class ApplyReturnJsonModel {
    private int orderStatus;
    private String returnReason;
    private String returnUrls;
    private String netId;
    private String netName;
    private String expressCode;
    private String expressLogo;
    private String waybillNumber;
    private String arbitrateUrls;
    private String arbitrateCause;

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getReturnUrls() {
        return returnUrls;
    }

    public void setReturnUrls(String returnUrls) {
        this.returnUrls = returnUrls;
    }

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getExpressLogo() {
        return expressLogo;
    }

    public void setExpressLogo(String expressLogo) {
        this.expressLogo = expressLogo;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getArbitrateUrls() {
        return arbitrateUrls;
    }

    public void setArbitrateUrls(String arbitrateUrls) {
        this.arbitrateUrls = arbitrateUrls;
    }

    public String getArbitrateCause() {
        return arbitrateCause;
    }

    public void setArbitrateCause(String arbitrateCause) {
        this.arbitrateCause = arbitrateCause;
    }
}
