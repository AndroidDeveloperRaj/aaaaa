package com.merrichat.net.utils;

import java.io.File;

/**
 * Created by amssy on 17/11/2.
 */

public class FilterUtil {

    /**
     * 删除文件夹和文件夹里面的文件
     */
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 修改文件名
     * @param oldfileName
     * @param newFileName
     * @return
     */
    public static boolean renameSDFile(String oldfileName, String newFileName) {
        File oleFile = new File(oldfileName);
        File newFile = new File(newFileName);
        return oleFile.renameTo(newFile);
    }
}
