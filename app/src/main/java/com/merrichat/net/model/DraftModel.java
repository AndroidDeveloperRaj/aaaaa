package com.merrichat.net.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2017/11/9.
 *
 * 草稿列表
 */

public class DraftModel implements Serializable {

    public static final int itemType = 2;
    private String imgUrl;
    private String name;
    private String date;
    private int type;
    private String title;
    private String cover;
    private String contenText;//想说的话
    private String classifystr;//格式为 "1,2,3"  标签id
    private String tabText;//标签text
    private int jurisdiction = 1;//查看权限 0私密 1公开 2 私密 3陌生
    private ArrayList<PhotoVideoModel> content;//图文Json

    public static int getItemType() {
        return itemType;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public ArrayList<PhotoVideoModel> getContent() {
        return content;
    }

    public void setContent(ArrayList<PhotoVideoModel> content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContenText() {
        return contenText;
    }

    public void setContenText(String contenText) {
        this.contenText = contenText;
    }

    public String getClassifystr() {
        return classifystr;
    }

    public void setClassifystr(String classifystr) {
        this.classifystr = classifystr;
    }

    public String getTabText() {
        return tabText;
    }

    public void setTabText(String tabText) {
        this.tabText = tabText;
    }

    public int getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(int jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

}
