package com.merrichat.net.activity.message.storage;

import java.util.ArrayList;

/**
 * 保存选择的照片路径
 *
 * @author amssy
 */
public class PhotoCount {


    //存储路径
    public static ArrayList<String> photoPaths = new ArrayList<String>();

    public ArrayList<String> getPhotosPaths() {
        return photoPaths;
    }
}
