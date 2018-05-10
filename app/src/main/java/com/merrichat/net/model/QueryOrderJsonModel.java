package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2018/1/29.
 * <p>
 * 查询订单时需要的参数
 */

public class QueryOrderJsonModel {
    private int orderStatus;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
    private String key;//{sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
    private String groupId;//这个字段表示的是群id,此字段存在就说明是在进行群订单的查询
    private String memberId;//当前人的memberId
    private String orderId;//如果查的是拼团就传serialNumber参数,如果不是就传orderId参数
    private int pageSize;
    private int currentPage;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
