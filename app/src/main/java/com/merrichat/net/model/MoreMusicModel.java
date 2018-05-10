package com.merrichat.net.model;

import java.util.List;

/**
 * Created by amssy on 18/1/13.
 */

public class MoreMusicModel {

    /**
     * data : {"music":[{"author":"123","coprPerson":"123","cover":"http://igomopub.igomot.net/nfs_data/igomo/adImage/322757886599169.jpg","crePersonId":123,"createPerson":"admin","createTime":1515656696103,"dueDate":1533657600000,"id":322757886599169,"musicName":"1231","musicPrice":1232131,"musicSize":5.45,"musicTime":"02:23","musicType":"[\"非常好\",\"古典\"]","musicTypeId":"[317713072119808,317834346225664]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322757886599169.mp3","remark":"123"},{"author":"1","coprPerson":"1","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752595193,"dueDate":"","id":322959001378816,"musicName":"1","musicPrice":"","musicSize":17.18,"musicTime":"00:00","musicType":"[\"非常好\"]","musicTypeId":"[317713072119808]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959001378816.mp3","remark":""},{"author":"2","coprPerson":"2","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752617636,"dueDate":"","id":322959049613312,"musicName":"2","musicPrice":"","musicSize":11.13,"musicTime":"04:52","musicType":"[\"非常可以\"]","musicTypeId":"[317713141325824]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959049613312.mp3","remark":""},{"author":"3","coprPerson":"3","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752635240,"dueDate":"","id":322959087362049,"musicName":"3","musicPrice":"","musicSize":4.24,"musicTime":"01:52","musicType":"[\"非常流行\"]","musicTypeId":"[317694598307840]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959087362049.mp3","remark":""},{"author":"4","coprPerson":"4","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752652077,"dueDate":"","id":322959123013632,"musicName":"4","musicPrice":"","musicSize":8.45,"musicTime":"03:42","musicType":"[\"古典\"]","musicTypeId":"[317834346225664]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959123013632.mp3","remark":""},{"author":"5","coprPerson":"5","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752670709,"dueDate":"","id":322959160762369,"musicName":"5","musicPrice":"","musicSize":7.17,"musicTime":"03:08","musicType":"[\"非常流行\"]","musicTypeId":"[317694598307840]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959160762369.mp3","remark":""}],"total":6,"result":true}
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

    public class DataBean {
        /**
         * music : [{"author":"123","coprPerson":"123","cover":"http://igomopub.igomot.net/nfs_data/igomo/adImage/322757886599169.jpg","crePersonId":123,"createPerson":"admin","createTime":1515656696103,"dueDate":1533657600000,"id":322757886599169,"musicName":"1231","musicPrice":1232131,"musicSize":5.45,"musicTime":"02:23","musicType":"[\"非常好\",\"古典\"]","musicTypeId":"[317713072119808,317834346225664]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322757886599169.mp3","remark":"123"},{"author":"1","coprPerson":"1","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752595193,"dueDate":"","id":322959001378816,"musicName":"1","musicPrice":"","musicSize":17.18,"musicTime":"00:00","musicType":"[\"非常好\"]","musicTypeId":"[317713072119808]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959001378816.mp3","remark":""},{"author":"2","coprPerson":"2","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752617636,"dueDate":"","id":322959049613312,"musicName":"2","musicPrice":"","musicSize":11.13,"musicTime":"04:52","musicType":"[\"非常可以\"]","musicTypeId":"[317713141325824]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959049613312.mp3","remark":""},{"author":"3","coprPerson":"3","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752635240,"dueDate":"","id":322959087362049,"musicName":"3","musicPrice":"","musicSize":4.24,"musicTime":"01:52","musicType":"[\"非常流行\"]","musicTypeId":"[317694598307840]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959087362049.mp3","remark":""},{"author":"4","coprPerson":"4","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752652077,"dueDate":"","id":322959123013632,"musicName":"4","musicPrice":"","musicSize":8.45,"musicTime":"03:42","musicType":"[\"古典\"]","musicTypeId":"[317834346225664]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959123013632.mp3","remark":""},{"author":"5","coprPerson":"5","cover":"","crePersonId":123,"createPerson":"admin","createTime":1515752670709,"dueDate":"","id":322959160762369,"musicName":"5","musicPrice":"","musicSize":7.17,"musicTime":"03:08","musicType":"[\"非常流行\"]","musicTypeId":"[317694598307840]","musicUrl":"http://igomopub.igomot.net/nfs_data/igomo/musicFile/322959160762369.mp3","remark":""}]
         * total : 6
         * result : true
         */

        private int total;
        private boolean result;
        private List<MusicBean> music;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public List<MusicBean> getMusic() {
            return music;
        }

        public void setMusic(List<MusicBean> music) {
            this.music = music;
        }

        public class MusicBean {
            /**
             * author : 123
             * coprPerson : 123
             * cover : http://igomopub.igomot.net/nfs_data/igomo/adImage/322757886599169.jpg
             * crePersonId : 123
             * createPerson : admin
             * createTime : 1515656696103
             * dueDate : 1533657600000
             * id : 322757886599169
             * musicName : 1231
             * musicPrice : 1232131
             * musicSize : 5.45
             * musicTime : 02:23
             * musicType : ["非常好","古典"]
             * musicTypeId : [317713072119808,317834346225664]
             * musicUrl : http://igomopub.igomot.net/nfs_data/igomo/musicFile/322757886599169.mp3
             * remark : 123
             */

            private String author;
            private String coprPerson;
            private String cover;
            private int crePersonId;
            private String createPerson;
            private long createTime;
            private long dueDate;
            private long id;
            private String musicName;
            private int musicPrice;
            private double musicSize;
            private String musicTime;
            private String musicType;
            private String musicTypeId;
            private String musicUrl;
            private String remark;

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getCoprPerson() {
                return coprPerson;
            }

            public void setCoprPerson(String coprPerson) {
                this.coprPerson = coprPerson;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public int getCrePersonId() {
                return crePersonId;
            }

            public void setCrePersonId(int crePersonId) {
                this.crePersonId = crePersonId;
            }

            public String getCreatePerson() {
                return createPerson;
            }

            public void setCreatePerson(String createPerson) {
                this.createPerson = createPerson;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public long getDueDate() {
                return dueDate;
            }

            public void setDueDate(long dueDate) {
                this.dueDate = dueDate;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getMusicName() {
                return musicName;
            }

            public void setMusicName(String musicName) {
                this.musicName = musicName;
            }

            public int getMusicPrice() {
                return musicPrice;
            }

            public void setMusicPrice(int musicPrice) {
                this.musicPrice = musicPrice;
            }

            public double getMusicSize() {
                return musicSize;
            }

            public void setMusicSize(double musicSize) {
                this.musicSize = musicSize;
            }

            public String getMusicTime() {
                return musicTime;
            }

            public void setMusicTime(String musicTime) {
                this.musicTime = musicTime;
            }

            public String getMusicType() {
                return musicType;
            }

            public void setMusicType(String musicType) {
                this.musicType = musicType;
            }

            public String getMusicTypeId() {
                return musicTypeId;
            }

            public void setMusicTypeId(String musicTypeId) {
                this.musicTypeId = musicTypeId;
            }

            public String getMusicUrl() {
                return musicUrl;
            }

            public void setMusicUrl(String musicUrl) {
                this.musicUrl = musicUrl;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }
    }
}
