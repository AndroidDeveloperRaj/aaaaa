package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by AMSSY1 on 2017/11/20.
 */

public class PhotoVideoModel implements Serializable {
    public String text;
    public String url;
    public String type;//0图片 1视频
    public int flag;
    public int width;
    public int height;

    public PhotoVideoModel() {
        super();
    }

    public PhotoVideoModel(String textContent) {
        this.text = textContent;
    }

    public PhotoVideoModel(String url, String type, int flag, int width, int height) {
        this.width = width;
        this.height = height;
        this.url = url;
        this.flag = flag;
        this.type = type;

    }

    public PhotoVideoModel(String type, int flag) {
        this.flag = flag;
        this.type = type;
    }

    public PhotoVideoModel(String type, int flag, String text) {
        this.flag = flag;
        this.type = type;
        this.text = text;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
