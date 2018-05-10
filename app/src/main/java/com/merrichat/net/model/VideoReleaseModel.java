package com.merrichat.net.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 发布视频model
 * Created by amssy on 18/1/10.
 */

public class VideoReleaseModel implements Serializable {
    //上传到阿里云视频路径
    String videoCover;//视频封面
    String videoPath;//视频路径
    //上传数据到服务器参数
    String title;//标题
    String content;//内容
    String phone;//手机号
    String cover;//封面
    String classifystr;//标签
    String memberId;
    String gender;
    String memberName;
    String address;
    String memberImage;
    String musicUrl;
    String videoUrl;
    String longitude;
    String latitude;
    int jurisdiction;
    int flag;
    int isBlack;
    int isDelete;
    String pictureUrls;
    int width;//封面图宽
    int height;//封面图高
    String text;
    int shareTo;//分享至哪
    ArrayList<String> urls;//图片路径集合
    ArrayList<PhotoVideoModel> photoVideoList;//图文专辑内容
    private String videoMusicPath;

    /**
     * 上传图文
     *
     * @param videoCover
     * @param videoPath
     * @param title
     * @param content
     * @param phone
     * @param cover
     * @param classifystr
     * @param memberId
     * @param gender
     * @param memberName
     * @param address
     * @param memberImage
     * @param musicUrl
     * @param videoUrl
     * @param longitude
     * @param latitude
     * @param jurisdiction
     * @param flag
     * @param isBlack
     * @param isDelete
     * @param pictureUrls
     * @param width
     * @param height
     * @param text
     * @param shareTo
     * @param urls
     * @param photoVideoList
     */
    public VideoReleaseModel(String videoCover, String videoPath, String title, String content, String phone, String cover, String classifystr
            , String memberId, String gender, String memberName, String address, String memberImage, String musicUrl, String videoUrl, String longitude
            , String latitude, int jurisdiction, int flag, int isBlack, int isDelete, String pictureUrls, int width, int height, String text, int shareTo
            , ArrayList<String> urls, ArrayList<PhotoVideoModel> photoVideoList, String videoMusicPath) {
        this.videoCover = videoCover;//视频封面
        this.videoPath = videoPath;//视频路径
        //上传数据到服务器参数
        this.title = title;//标题
        this.content = content;//内容
        this.phone = phone;//手机号
        this.cover = cover;//封面
        this.classifystr = classifystr;//标签
        this.memberId = memberId;
        this.gender = gender;
        this.memberName = memberName;
        this.address = address;
        this.memberImage = memberImage;
        this.musicUrl = musicUrl;
        this.videoUrl = videoUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.jurisdiction = jurisdiction;
        this.flag = flag;
        this.isBlack = isBlack;
        this.isDelete = isDelete;
        this.pictureUrls = pictureUrls;
        this.width = width;
        this.height = height;
        this.text = text;
        this.shareTo = shareTo;
        this.urls = urls;
        this.photoVideoList = photoVideoList;
        this.videoMusicPath = videoMusicPath;
    }

    /**
     * 上传视频
     *
     * @param videoCover
     * @param videoPath
     * @param title
     * @param content
     * @param phone
     * @param cover
     * @param classifystr
     * @param memberId
     * @param gender
     * @param memberName
     * @param address
     * @param memberImage
     * @param musicUrl
     * @param videoUrl
     * @param longitude
     * @param latitude
     * @param jurisdiction
     * @param flag
     * @param isBlack
     * @param isDelete
     * @param pictureUrls
     * @param width
     * @param height
     * @param text
     * @param shareTo
     */
    public VideoReleaseModel(String videoCover, String videoPath, String title, String content, String phone, String cover, String classifystr
            , String memberId, String gender, String memberName, String address, String memberImage, String musicUrl, String videoUrl, String longitude
            , String latitude, int jurisdiction, int flag, int isBlack, int isDelete, String pictureUrls, int width, int height, String text, int shareTo) {
        this.videoCover = videoCover;//视频封面
        this.videoPath = videoPath;//视频路径
        //上传数据到服务器参数
        this.title = title;//标题
        this.content = content;//内容
        this.phone = phone;//手机号
        this.cover = cover;//封面
        this.classifystr = classifystr;//标签
        this.memberId = memberId;
        this.gender = gender;
        this.memberName = memberName;
        this.address = address;
        this.memberImage = memberImage;
        this.musicUrl = musicUrl;
        this.videoUrl = videoUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.jurisdiction = jurisdiction;
        this.flag = flag;
        this.isBlack = isBlack;
        this.isDelete = isDelete;
        this.pictureUrls = pictureUrls;
        this.width = width;
        this.height = height;
        this.text = text;
        this.shareTo = shareTo;
    }

    public String getVideoMusicPath() {
        return videoMusicPath;
    }

    public void setVideoMusicPath(String videoMusicPath) {
        this.videoMusicPath = videoMusicPath;
    }

    public ArrayList<PhotoVideoModel> getPhotoVideoList() {
        return photoVideoList;
    }

    public void setPhotoVideoList(ArrayList<PhotoVideoModel> photoVideoList) {
        this.photoVideoList = photoVideoList;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public int getShareTo() {
        return shareTo;
    }

    public void setShareTo(int shareTo) {
        this.shareTo = shareTo;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getClassifystr() {
        return classifystr;
    }

    public void setClassifystr(String classifystr) {
        this.classifystr = classifystr;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMemberImage() {
        return memberImage;
    }

    public void setMemberImage(String memberImage) {
        this.memberImage = memberImage;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(int jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getIsBlack() {
        return isBlack;
    }

    public void setIsBlack(int isBlack) {
        this.isBlack = isBlack;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getPictureUrls() {
        return pictureUrls;
    }

    public void setPictureUrls(String pictureUrls) {
        this.pictureUrls = pictureUrls;
    }
}
