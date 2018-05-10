package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 2018/2/26.
 */

public class GroupRedDetialModel implements Serializable {

    /**
     * data : {"total":3,"surTotal":1,"red":[{"collectHeadImgUrl":"http://cas.okdit.net/nfs_data/mob/head/328185256656896.jpg?time=1518244894345","collectMoney":2.27,"collectName":"yop","collectTime":1519700774385,"tid":165619350232903680},{"collectHeadImgUrl":"http://cas.okdit.net/nfs_data/mob/head/316069722243072.jpg","collectMoney":0.42,"collectName":"阿尔法","collectTime":1519700792594,"tid":165619350232903680}]}
     * success : true
     */

    private DataBean data;
    private boolean success;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * total : 3
         * surTotal : 1
         * red : [{"collectHeadImgUrl":"http://cas.okdit.net/nfs_data/mob/head/328185256656896.jpg?time=1518244894345","collectMoney":2.27,"collectName":"yop","collectTime":1519700774385,"tid":165619350232903680},{"collectHeadImgUrl":"http://cas.okdit.net/nfs_data/mob/head/316069722243072.jpg","collectMoney":0.42,"collectName":"阿尔法","collectTime":1519700792594,"tid":165619350232903680}]
         */

        private int total;
        private int surTotal;
        private List<RedBean> red;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSurTotal() {
            return surTotal;
        }

        public void setSurTotal(int surTotal) {
            this.surTotal = surTotal;
        }

        public List<RedBean> getRed() {
            return red;
        }

        public void setRed(List<RedBean> red) {
            this.red = red;
        }

        public static class RedBean {
            /**
             * collectHeadImgUrl : http://cas.okdit.net/nfs_data/mob/head/328185256656896.jpg?time=1518244894345
             * collectMoney : 2.27
             * collectName : yop
             * collectTime : 1519700774385
             * tid : 165619350232903680
             */

            private String collectHeadImgUrl;
            private double collectMoney;
            private String collectName;
            private long collectTime;
            private long tid;

            public String getCollectHeadImgUrl() {
                return collectHeadImgUrl;
            }

            public void setCollectHeadImgUrl(String collectHeadImgUrl) {
                this.collectHeadImgUrl = collectHeadImgUrl;
            }

            public double getCollectMoney() {
                return collectMoney;
            }

            public void setCollectMoney(double collectMoney) {
                this.collectMoney = collectMoney;
            }

            public String getCollectName() {
                return collectName;
            }

            public void setCollectName(String collectName) {
                this.collectName = collectName;
            }

            public long getCollectTime() {
                return collectTime;
            }

            public void setCollectTime(long collectTime) {
                this.collectTime = collectTime;
            }

            public long getTid() {
                return tid;
            }

            public void setTid(long tid) {
                this.tid = tid;
            }
        }
    }
}
