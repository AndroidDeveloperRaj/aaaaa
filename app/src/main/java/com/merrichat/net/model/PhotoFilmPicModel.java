package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2017/11/10.
 */

public class PhotoFilmPicModel {
    public static final int moBan_itemType = 0;
    public static final int music_itemType = 0;
    public static final int lvJing_itemType = 1;
    public static final int jianCai_itemType = 2;
    public static final int cover_itemType = 3;
    public static final int move_itemType = 4;
    public static final int tiezhi_itemType = 5;
    public static final int zimu_itemType = 6;
    public static final int volume_itemType = 7;
    private int drawId;
    private int type;
    private String imgUrl;
    private int cutIconId;
    private String cutName;
    private String moBanLvJingTypeName;
    private boolean isChecked;

    public PhotoFilmPicModel(int cutIconId, String cutName) {
        this.cutIconId = cutIconId;
        this.cutName = cutName;
    }

    public PhotoFilmPicModel(int type, String moBanLvJingTypeName, String imgUrl, boolean isChecked) {
        this.imgUrl = imgUrl;
        this.moBanLvJingTypeName = moBanLvJingTypeName;
        this.isChecked = isChecked;
        this.type = type;
    }

    public PhotoFilmPicModel() {

    }

    public PhotoFilmPicModel(int drawId) {
        this.drawId = drawId;
    }

    public PhotoFilmPicModel(String cutName) {
        this.cutName = cutName;
    }

    public int getDrawId() {
        return drawId;
    }

    public void setDrawId(int drawId) {
        this.drawId = drawId;
    }

    public String getMoBanLvJingTypeName() {
        return moBanLvJingTypeName;
    }

    public void setMoBanLvJingTypeName(String moBanLvJingTypeName) {
        this.moBanLvJingTypeName = moBanLvJingTypeName;
    }

    public int getCutIconId() {
        return cutIconId;
    }

    public void setCutIconId(int cutIconId) {
        this.cutIconId = cutIconId;
    }

    public String getCutName() {
        return cutName;
    }

    public void setCutName(String cutName) {
        this.cutName = cutName;
    }

    public int getType() {
        return type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
