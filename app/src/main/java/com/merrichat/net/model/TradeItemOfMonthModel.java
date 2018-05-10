package com.merrichat.net.model;

/**
 * Created by amssy on 18/1/3.
 */

public class TradeItemOfMonthModel {


    /**
     * imgUrl :
     * user_name : okdi_okdi_131675477732
     * title : 李恩丹测试充值
     * nickName :
     * created : 2017-12-14 17:54:12
     * remark :
     * jour_value : 5000.00
     * user_account_id : 157958776351388672
     * tid : 158850362666659840
     * outer_tid :
     * trade_type : 0
     */

    private String imgUrl;
    private String user_name;
    private String title;
    private String nickName;
    private String created;
    private String remark;
    private String jour_value;
    private String user_account_id;
    private String tid;
    private String outer_tid;
    private String trade_type;
    private String monthTitle;
    private int titleId;

    public String getMonthTitle() {
        return monthTitle;
    }

    public void setMonthTitle(String monthTitle) {
        this.monthTitle = monthTitle;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getJour_value() {
        return jour_value;
    }

    public void setJour_value(String jour_value) {
        this.jour_value = jour_value;
    }

    public String getUser_account_id() {
        return user_account_id;
    }

    public void setUser_account_id(String user_account_id) {
        this.user_account_id = user_account_id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getOuter_tid() {
        return outer_tid;
    }

    public void setOuter_tid(String outer_tid) {
        this.outer_tid = outer_tid;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }
}
