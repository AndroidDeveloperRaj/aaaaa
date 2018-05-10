package com.merrichat.net.activity.message.storage;


public class PhotoCheck {

    //照片的路径
    private String path;
    //是否选择了  在预览页面是否按了不选择
    private boolean check;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}

