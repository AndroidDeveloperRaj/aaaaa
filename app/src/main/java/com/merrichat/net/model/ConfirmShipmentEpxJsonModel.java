package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2018/1/26.
 * <p>
 * 确认发货--jsonStr 参数
 */

public class ConfirmShipmentEpxJsonModel {
    private String netId;//快递公司id
    private String netName;//快递公司名字
    private String expressCode;//快递公司简码zto，yunda
    private String expressLogo;//快递公司logo链接
    private String waybillNumber;//运单号
    private String kf;//客服电话

    public ConfirmShipmentEpxJsonModel(String netId, String netName, String expressCode, String expressLogo, String waybillNumber, String kf) {
        this.netId = netId;
        this.netName = netName;
        this.expressCode = expressCode;
        this.expressLogo = expressLogo;
        this.waybillNumber = waybillNumber;
        this.kf = kf;
    }

    public ConfirmShipmentEpxJsonModel() {

    }
}
