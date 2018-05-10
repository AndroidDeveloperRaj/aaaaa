package com.merrichat.net.model;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/12/14.
 */

public class MyMovieModel {

    /**
     * data : {"movieList":[{"id":317497596043264,"content":[{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}],"title":"他我就是想","cover":{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},"time":"0天前","isLike":0,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":9},{"id":317497870770176,"content":[{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},{"text":"继续取消","height":1766,"flag":0,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}],"title":"金五台子乡经","cover":{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},"time":"0天前","isLike":0,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":-1}],"success":true}
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
         * movieList : [{"id":317497596043264,"content":[{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}],"title":"他我就是想","cover":{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},"time":"0天前","isLike":0,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":9},{"id":317497870770176,"content":[{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},{"text":"继续取消","height":1766,"flag":0,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}],"title":"金五台子乡经","cover":{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},"time":"0天前","isLike":0,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":-1}]
         * success : true
         */

        private boolean success;
        private List<MovieListBean> movieList;
        private List<MovieListBean> collectList;

        public List<MovieListBean> getCollectList() {
            return collectList;
        }

        public void setCollectList(List<MovieListBean> collectList) {
            this.collectList = collectList;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<MovieListBean> getMovieList() {
            return movieList;
        }

        public void setMovieList(List<MovieListBean> movieList) {
            this.movieList = movieList;
        }

        public static class MovieListBean {
            /**
             * id : 317497596043264
             * content : [{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}]
             * title : 他我就是想
             * cover : {"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}
             * time : 0天前
             * isLike : 0
             * commentCounts : 0
             * memberId : 315917552893952
             * memberImage : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg
             * memberName :
             * jurisdiction : 1
             * likeCounts : 9
             */

            private long id;
            private String title;
            private String RMBSign;
            private CoverBean cover;
            private String time;
            private int isLike;
            private int commentCounts;
            private long memberId;
            private String memberImage;
            private String memberName;
            private int jurisdiction;
            private int likeCounts;
            private int flag;
            private List<ContentBean> content;

            public String getRMBSign() {
                return RMBSign;
            }

            public void setRMBSign(String RMBSign) {
                this.RMBSign = RMBSign;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public CoverBean getCover() {
                return cover;
            }

            public void setCover(CoverBean cover) {
                this.cover = cover;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getIsLike() {
                return isLike;
            }

            public void setIsLike(int isLike) {
                this.isLike = isLike;
            }

            public int getCommentCounts() {
                return commentCounts;
            }

            public void setCommentCounts(int commentCounts) {
                this.commentCounts = commentCounts;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getMemberImage() {
                return memberImage;
            }

            public void setMemberImage(String memberImage) {
                this.memberImage = memberImage;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public int getJurisdiction() {
                return jurisdiction;
            }

            public void setJurisdiction(int jurisdiction) {
                this.jurisdiction = jurisdiction;
            }

            public int getLikeCounts() {
                return likeCounts;
            }

            public void setLikeCounts(int likeCounts) {
                this.likeCounts = likeCounts;
            }

            public List<ContentBean> getContent() {
                return content;
            }

            public void setContent(List<ContentBean> content) {
                this.content = content;
            }

            public static class CoverBean {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private int height;
                private int flag;
                private int width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

            public static class ContentBean {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private int height;
                private int flag;
                private int width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }

        public static class CollectListBean {
            /**
             * id : 317497596043264
             * content : [{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}]
             * title : 他我就是想
             * cover : {"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}
             * time : 0天前
             * isLike : 0
             * commentCounts : 0
             * memberId : 315917552893952
             * memberImage : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg
             * memberName :
             * jurisdiction : 1
             * likeCounts : 9
             */

            private long id;
            private String title;
            private CoverBean cover;
            private String time;
            private int isLike;
            private int commentCounts;
            private long memberId;
            private String memberImage;
            private String memberName;
            private int jurisdiction;
            private int likeCounts;
            private List<ContentBean> content;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public CoverBean getCover() {
                return cover;
            }

            public void setCover(CoverBean cover) {
                this.cover = cover;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getIsLike() {
                return isLike;
            }

            public void setIsLike(int isLike) {
                this.isLike = isLike;
            }

            public int getCommentCounts() {
                return commentCounts;
            }

            public void setCommentCounts(int commentCounts) {
                this.commentCounts = commentCounts;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getMemberImage() {
                return memberImage;
            }

            public void setMemberImage(String memberImage) {
                this.memberImage = memberImage;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public int getJurisdiction() {
                return jurisdiction;
            }

            public void setJurisdiction(int jurisdiction) {
                this.jurisdiction = jurisdiction;
            }

            public int getLikeCounts() {
                return likeCounts;
            }

            public void setLikeCounts(int likeCounts) {
                this.likeCounts = likeCounts;
            }

            public List<ContentBean> getContent() {
                return content;
            }

            public void setContent(List<ContentBean> content) {
                this.content = content;
            }

            public static class CoverBean {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private int height;
                private int flag;
                private int width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

            public static class ContentBean {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private int height;
                private int flag;
                private int width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
