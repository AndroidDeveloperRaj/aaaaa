package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2017/12/4.
 */

public class HisZiLiaoSettingModel {

    /**
     * data : {"IseeHim":0,"heSeeMe":0}
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
         * IseeHim : 0
         * heSeeMe : 0
         */

        private int IseeHim;
        private int heSeeMe;

        public int getIseeHim() {
            return IseeHim;
        }

        public void setIseeHim(int IseeHim) {
            this.IseeHim = IseeHim;
        }

        public int getHeSeeMe() {
            return heSeeMe;
        }

        public void setHeSeeMe(int heSeeMe) {
            this.heSeeMe = heSeeMe;
        }
    }
}
