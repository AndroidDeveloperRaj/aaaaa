package com.merrichat.net.model;

import java.util.List;

/**
 * 打赏记录Model
 * Created by amssy on 17/12/30.
 */

public class DashModel {

    /**
     * data : [{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/317870888501248.jpg","rewardMemberId":317870888501248,"tieId":"319869336543232","tradeId":320593889976320}]
     * success : true
     */

    private boolean success;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * imgUrl : http://cas.okdit.net/nfs_data/mob/head/317870888501248.jpg
         * rewardMemberId : 317870888501248
         * tieId : 319869336543232
         * tradeId : 320593889976320
         */
        private String amount;
        private String createTime;
        private String imgUrl;
        private long rewardMemberId;
        private String tieId;
        private long tradeId;
        private String name;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public long getRewardMemberId() {
            return rewardMemberId;
        }

        public void setRewardMemberId(long rewardMemberId) {
            this.rewardMemberId = rewardMemberId;
        }

        public String getTieId() {
            return tieId;
        }

        public void setTieId(String tieId) {
            this.tieId = tieId;
        }

        public long getTradeId() {
            return tradeId;
        }

        public void setTradeId(long tradeId) {
            this.tradeId = tradeId;
        }
    }
}
