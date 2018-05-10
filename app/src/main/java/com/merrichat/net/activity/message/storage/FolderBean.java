package com.merrichat.net.activity.message.storage;

public class FolderBean {
    /**
     * 文件夹路径
     */
    private String dir;

    /**
     * 第一张图片路径
     */
    private String firstImagePath;

    /**
     * 当前文件夹的名称
     */
    private String name;

    /**
     * 当前文件夹的数量
     */
    private int count;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf);
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }


    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
