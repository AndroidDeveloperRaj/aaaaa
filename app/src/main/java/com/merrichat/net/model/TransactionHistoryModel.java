package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjingjing on 17/8/3.
 */

public class TransactionHistoryModel implements Serializable {
    private String trademonth;

    private List<TradeItemOfMonthModel> list_item;

    public String getTrademonth() {
        return trademonth;
    }

    public void setTrademonth(String trademonth) {
        this.trademonth = trademonth;
    }


    public List<TradeItemOfMonthModel> getList_item() {
        return list_item;
    }

    public void setList_item(List<TradeItemOfMonthModel> list_item) {
        this.list_item = list_item;
    }
}
