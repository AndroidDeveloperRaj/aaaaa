package com.merrichat.net.model;

import com.google.gson.annotations.SerializedName;
import com.merrichat.net.ossfile.STSGetter;

import java.util.List;

/**
 * Created by amssy on 18/1/2.
 */

public class AboutLogModel extends Response {


    /**
     * data : {"total":55,"beautyLogList":[{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"3264\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"3264\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg\",\"text\":\"\"}","createTimes":1520943668917,"describe":"","flag":1,"gender":2,"id":333845474697216,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":322366488231936,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/322366488231936.jpg","memberName":"17610360316","msg":"","musicUrl":"","phone":"17610360316","position":[0,0],"recommendValue":4.6624249266498207E-4,"shareCounts":0,"title":"在家了","updateTimes":1521522784382,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1517037118917.jpg\",\"type\":\"0\",\"height\":820,\"flag\":1,\"width\":820}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1517037118917.jpg\",\"type\":\"0\",\"height\":820,\"flag\":1,\"width\":820}","createTimes":1520927735622,"describe":"涂抹","flag":1,"gender":1,"id":333812060774400,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1520927630153","memberName":"锦衣卫","msg":"","musicUrl":"","phone":"13167547773","position":[0,0],"recommendValue":0,"shareCounts":0,"title":"咯屠龙","updateTimes":1520927735622,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":1,\"height\":240,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1515843618673.jpg\",\"width\":240}]","cover":"{\"flag\":1,\"height\":240,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1515843618673.jpg\",\"width\":240}","createTimes":1520913396116,"describe":"hhhhh","flag":1,"gender":1,"id":333781989711872,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":241594280493056,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/241594280493056.jpg","memberName":"15810885234","msg":"","musicUrl":"","phone":"15810885234","position":[0,0],"recommendValue":0,"shareCounts":0,"title":"hhhh","updateTimes":1520913396116,"videoUrl":"","votesCounts":0}},{"income":0.002,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"750\",\"height\":\"1334\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520650532472/3439.jpg\",\"text\":\"\"},{\"flag\":\"1\",\"width\":\"750\",\"height\":\"1334\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520650533284/2924.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"750\",\"height\":\"1334\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520650532472/3439.jpg\",\"text\":\"\"}","createTimes":1520650729072,"describe":"","flag":1,"gender":1,"id":333231137087488,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":1,"longitude":0,"memberId":319711988981760,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/319711988981760.jpg","memberName":"13426115506","msg":"","musicUrl":"","phone":"13426115506","position":[0,0],"recommendValue":0.24489795918367346,"shareCounts":0,"title":"回测22910bug","updateTimes":1520672042883,"videoUrl":"","votesCounts":2}},{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520584724299/1692.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520584724299/1692.jpg\",\"text\":\"\"}","createTimes":1520584890990,"describe":"","flag":1,"gender":2,"id":333093062696960,"isBlack":-1,"isDelete":0,"jurisdiction":2,"latitude":116.317367,"likeCounts":0,"longitude":39.950643,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"","phone":"13426460007","position":[39.950643,116.317367],"recommendValue":0,"shareCounts":0,"title":"dhshshs","updateTimes":1520584890990,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"北京市海淀区紫竹院路","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":1,\"height\":4160,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/20180305_195953.jpg\",\"width\":3120},{\"flag\":0,\"height\":4160,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/20180301_155817.jpg\",\"width\":3120}]","cover":"{\"flag\":1,\"height\":4160,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/20180305_195953.jpg\",\"width\":3120}","createTimes":1520583411175,"describe":"回测22859bug","flag":1,"gender":2,"id":333089961009152,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.950407,"likeCounts":0,"longitude":116.313189,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"http://okdi.oss-cn-beijing.aliyuncs.com/1231.mp3","phone":"13426460007","position":[116.313189,39.950407],"recommendValue":0,"shareCounts":0,"title":"回测22859bug","updateTimes":1520583411175,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578682620/6190.jpg\",\"text\":\"女性大脑\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578682620/6190.jpg\",\"text\":\"女性大脑\"}","createTimes":1520578813658,"describe":"","flag":1,"gender":2,"id":333080318304256,"isBlack":-1,"isDelete":0,"jurisdiction":0,"latitude":116.313827,"likeCounts":0,"longitude":39.950307,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"","phone":"13426460007","position":[39.950307,116.313827],"recommendValue":0,"shareCounts":0,"title":"不晓得你呢","updateTimes":1520578813658,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578449184/2850.jpg\",\"text\":\"测试跳转页面\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578449184/2850.jpg\",\"text\":\"测试跳转页面\"}","createTimes":1520578597907,"describe":"","flag":1,"gender":2,"id":333079865319424,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"","phone":"13426460007","position":[0,0],"recommendValue":0,"shareCounts":0,"title":"家乡的解毒剂的","updateTimes":1520578597907,"videoUrl":"","votesCounts":0}}],"pageSize":8,"pageNum":2}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * total : 55
         * beautyLogList : [{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"3264\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"3264\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg\",\"text\":\"\"}","createTimes":1520943668917,"describe":"","flag":1,"gender":2,"id":333845474697216,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":322366488231936,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/322366488231936.jpg","memberName":"17610360316","msg":"","musicUrl":"","phone":"17610360316","position":[0,0],"recommendValue":4.6624249266498207E-4,"shareCounts":0,"title":"在家了","updateTimes":1521522784382,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1517037118917.jpg\",\"type\":\"0\",\"height\":820,\"flag\":1,\"width\":820}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1517037118917.jpg\",\"type\":\"0\",\"height\":820,\"flag\":1,\"width\":820}","createTimes":1520927735622,"describe":"涂抹","flag":1,"gender":1,"id":333812060774400,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1520927630153","memberName":"锦衣卫","msg":"","musicUrl":"","phone":"13167547773","position":[0,0],"recommendValue":0,"shareCounts":0,"title":"咯屠龙","updateTimes":1520927735622,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":1,\"height\":240,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1515843618673.jpg\",\"width\":240}]","cover":"{\"flag\":1,\"height\":240,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/1515843618673.jpg\",\"width\":240}","createTimes":1520913396116,"describe":"hhhhh","flag":1,"gender":1,"id":333781989711872,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":241594280493056,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/241594280493056.jpg","memberName":"15810885234","msg":"","musicUrl":"","phone":"15810885234","position":[0,0],"recommendValue":0,"shareCounts":0,"title":"hhhh","updateTimes":1520913396116,"videoUrl":"","votesCounts":0}},{"income":0.002,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"750\",\"height\":\"1334\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520650532472/3439.jpg\",\"text\":\"\"},{\"flag\":\"1\",\"width\":\"750\",\"height\":\"1334\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520650533284/2924.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"750\",\"height\":\"1334\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520650532472/3439.jpg\",\"text\":\"\"}","createTimes":1520650729072,"describe":"","flag":1,"gender":1,"id":333231137087488,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":1,"longitude":0,"memberId":319711988981760,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/319711988981760.jpg","memberName":"13426115506","msg":"","musicUrl":"","phone":"13426115506","position":[0,0],"recommendValue":0.24489795918367346,"shareCounts":0,"title":"回测22910bug","updateTimes":1520672042883,"videoUrl":"","votesCounts":2}},{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520584724299/1692.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520584724299/1692.jpg\",\"text\":\"\"}","createTimes":1520584890990,"describe":"","flag":1,"gender":2,"id":333093062696960,"isBlack":-1,"isDelete":0,"jurisdiction":2,"latitude":116.317367,"likeCounts":0,"longitude":39.950643,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"","phone":"13426460007","position":[39.950643,116.317367],"recommendValue":0,"shareCounts":0,"title":"dhshshs","updateTimes":1520584890990,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"北京市海淀区紫竹院路","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":1,\"height\":4160,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/20180305_195953.jpg\",\"width\":3120},{\"flag\":0,\"height\":4160,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/20180301_155817.jpg\",\"width\":3120}]","cover":"{\"flag\":1,\"height\":4160,\"type\":\"0\",\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/20180305_195953.jpg\",\"width\":3120}","createTimes":1520583411175,"describe":"回测22859bug","flag":1,"gender":2,"id":333089961009152,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.950407,"likeCounts":0,"longitude":116.313189,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"http://okdi.oss-cn-beijing.aliyuncs.com/1231.mp3","phone":"13426460007","position":[116.313189,39.950407],"recommendValue":0,"shareCounts":0,"title":"回测22859bug","updateTimes":1520583411175,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578682620/6190.jpg\",\"text\":\"女性大脑\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578682620/6190.jpg\",\"text\":\"女性大脑\"}","createTimes":1520578813658,"describe":"","flag":1,"gender":2,"id":333080318304256,"isBlack":-1,"isDelete":0,"jurisdiction":0,"latitude":116.313827,"likeCounts":0,"longitude":39.950307,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"","phone":"13426460007","position":[39.950307,116.313827],"recommendValue":0,"shareCounts":0,"title":"不晓得你呢","updateTimes":1520578813658,"videoUrl":"","votesCounts":0}},{"income":0,"beautylog":{"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578449184/2850.jpg\",\"text\":\"测试跳转页面\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"2448\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520578449184/2850.jpg\",\"text\":\"测试跳转页面\"}","createTimes":1520578597907,"describe":"","flag":1,"gender":2,"id":333079865319424,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":321318604292097,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/321318604292097.jpg","memberName":"13426460007","msg":"","musicUrl":"","phone":"13426460007","position":[0,0],"recommendValue":0,"shareCounts":0,"title":"家乡的解毒剂的","updateTimes":1520578597907,"videoUrl":"","votesCounts":0}}]
         * pageSize : 8
         * pageNum : 2
         */

        public int total;
        public int pageSize;
        public int pageNum;
        public List<BeautyLogListBean> beautyLogList;

        public static class BeautyLogListBean {
            /**
             * income : 0.0
             * beautylog : {"address":"北京","classifyObjects":[],"classifys":["321495724056576"],"collectCounts":0,"commentCounts":0,"content":"[{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"3264\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg\",\"text\":\"\"}]","cover":"{\"flag\":\"0\",\"width\":\"2448\",\"height\":\"3264\",\"type\":\"0\",\"url\":\"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg\",\"text\":\"\"}","createTimes":1520943668917,"describe":"","flag":1,"gender":2,"id":333845474697216,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":0,"likeCounts":0,"longitude":0,"memberId":322366488231936,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/322366488231936.jpg","memberName":"17610360316","msg":"","musicUrl":"","phone":"17610360316","position":[0,0],"recommendValue":4.6624249266498207E-4,"shareCounts":0,"title":"在家了","updateTimes":1521522784382,"videoUrl":"","votesCounts":0}
             */

            public String income;
            public BeautylogBean beautylog;

            public static class BeautylogBean {
                /**
                 * address : 北京
                 * classifyObjects : []
                 * classifys : ["321495724056576"]
                 * collectCounts : 0
                 * commentCounts : 0
                 * content : [{"flag":"0","width":"2448","height":"3264","type":"0","url":"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg","text":""}]
                 * cover : {"flag":"0","width":"2448","height":"3264","type":"0","url":"https://okdi.oss-cn-beijing.aliyuncs.com/1520943539878/6945.jpg","text":""}
                 * createTimes : 1520943668917
                 * describe :
                 * flag : 1
                 * gender : 2
                 * id : 333845474697216
                 * isBlack : -1
                 * isDelete : 0
                 * jurisdiction : 1
                 * latitude : 0
                 * likeCounts : 0
                 * longitude : 0
                 * memberId : 322366488231936
                 * memberImage : http://cas.okdit.net/nfs_data/mob/head/322366488231936.jpg
                 * memberName : 17610360316
                 * msg :
                 * musicUrl :
                 * phone : 17610360316
                 * position : [0,0]
                 * recommendValue : 4.6624249266498207E-4
                 * shareCounts : 0
                 * title : 在家了
                 * updateTimes : 1521522784382
                 * videoUrl :
                 * votesCounts : 0
                 */

                public String address;
                public int collectCounts;
                public int commentCounts;
                public String content;
                public String cover;
                public long createTimes;
                public String describe;
                public int flag;
                public int gender;
                public long id;
                public int isBlack;
                public int isDelete;
                public int jurisdiction;
                public String latitude;
                public int likeCounts;
                public String longitude;
                public long memberId;
                public String memberImage;
                public String memberName;
                @SerializedName("msg")
                public String msgX;
                public String musicUrl;
                public String phone;
                public double recommendValue;
                public int shareCounts;
                public String title;
                public long updateTimes;
                public String videoUrl;
                public int votesCounts;
                public List<String> classifyObjects;
                public List<String> classifys;
                public List<String> position;
            }
        }
    }
}
