package com.merrichat.net.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


/**
 * Created by amssy on 17/11/10.
 */

public class FileUtils {

    private static int outWidth = 0;//输出bitmap的宽
    private static int outHeight = 0;//输出bitmap的高


    public static final String CameraPictureRootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
            + File.separator + "Camera" + File.separator;
    public static final String photoRootPath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "MerriChat" + File.separator + "photo" + File.separator;
    public static final String videoRootPath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "MerriChat" + File.separator + "video" + File.separator;
    public static final String musicRootPath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "MerriChat" + File.separator + "music" + File.separator;
    public static final String videoCover = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "MerriChat" + File.separator + "cover" + File.separator;
    public static final String videoEditPath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "MerriChat" + File.separator + "video_edit" + File.separator;
    public static final String photoMagicPath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "MerriChat" + File.separator + "magic_model" + File.separator;

    public static String createPhotoFileName() {
        File dir = new File(photoRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return photoRootPath + "MerriChat_Photo_" + getCurrentDate() + ".jpg";
    }

    public static String createMusicFileName(String fileName) {
        File dir = new File(musicRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return musicRootPath + fileName + ".mp3";
    }

    public static String createPhotoCacheFileName(Context context) {
        String photo = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/files/photo/";
        File dir = new File(photo);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return photo + "MerriChat_Photo_" + getCurrentDate() + ".jpg";
    }

    public static String createVideoCoverFileName() {
        File dir = new File(videoCover);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return videoCover + "MerriChat_Cover_" + getCurrentDate() + ".jpg";
    }

    public static String createVideoCoverFileNameGIF() {
        File dir = new File(videoCover);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return videoCover + "MerriChat_Cover_" + getCurrentDate() + ".gif";
    }


    public static String createVideoFileName() {
        File dir = new File(videoRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return videoRootPath + "MerriChat_Video_" + getCurrentDate() + ".mp4";
    }

    public static String createVideoEditFileName(String name) {
        File dir = new File(videoEditPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return videoEditPath + "MerriChat_Video_" + name + getCurrentDate() + ".mp4";
    }

    public static String createVideoMusicFileName(String name) {
        File dir = new File(videoEditPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return videoEditPath + "MerriChat_Video_" + name + getCurrentDate() + ".mp3";
    }


    public static String getCurrentDate() {//年月日时分秒
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return df.format(new Date());
    }

    //计算sampleSize
    private static int caculateSampleSize(String imgFilePath, int rotate) {
        outWidth = 0;
        outHeight = 0;
        int imgWidth = 0;//原始图片的宽
        int imgHeight = 0;//原始图片的高
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(imgFilePath);
            BitmapFactory.decodeStream(inputStream, null, options);//由于options.inJustDecodeBounds位true，所以这里并没有在内存中解码图片，只是为了得到原始图片的大小
            imgWidth = options.outWidth;
            imgHeight = options.outHeight;
            //初始化
            outWidth = imgWidth;
            outHeight = imgHeight;
            //如果旋转的角度是90的奇数倍,则输出的宽和高和原始宽高调换
            if ((rotate / 90) % 2 != 0) {
                outWidth = imgHeight;
                outHeight = imgWidth;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        //计算输出bitmap的sampleSize
        while (imgWidth / sampleSize > outWidth || imgHeight / sampleSize > outHeight) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    public static void doRotateImageAndSaveStrategy2(String filePath) {
        int rotate = readPictureDegree(filePath);
        if (rotate == 0)
            return;
        //得到sampleSize
        int sampleSize = caculateSampleSize(filePath, rotate);
        if (outWidth == 0 || outHeight == 0)
            return;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        //适当调整颜色深度
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            Bitmap srcBitmap = BitmapFactory.decodeStream(inputStream, null, options);//加载原图
            //test
            Bitmap.Config srcConfig = srcBitmap.getConfig();
            int srcMem = srcBitmap.getRowBytes() * srcBitmap.getHeight();//计算bitmap占用的内存大小

            Bitmap destBitmap = rotateImage(srcBitmap, rotate);
            int destMem = srcBitmap.getRowBytes() * srcBitmap.getHeight();
            srcBitmap.recycle();

            //保存bitmap到文件（覆盖原始图片）
            saveBitmap(destBitmap, filePath, 100);
            destBitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //通过img得到旋转rotate角度后的bitmap
    public static Bitmap rotateImage(Bitmap img, int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, false);
        return img;
    }

    /**
     * 将Bitmap保存到本地
     *
     * @param bmp      需要保存的Bitmap对象
     * @param filePath 保存路径
     * @param size     压缩率
     */
    public static void saveBitmap(Bitmap bmp, String filePath, int size) {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, size, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     */
    public static void delFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

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


    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;

    private static final int PADDING_SIZE_MIN = 20; // 最小留白长度, 单位: px


    /**
     * @param @param  str
     * @param @param  widthAndHeight
     * @param @return
     * @param @throws WriterException    设定文件
     * @return Bitmap    返回类型
     * @throws
     * @Title: createQRCode
     * @Description: 根据url生成二维码；并去掉二维码周围的白边
     */
    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (isFirstBlackPoint == false) {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                        Log.d("createQRCode", "x y = " + x + " " + y);
                    }
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        // 剪切中间的二维码区域，减少padding区域
        if (startX <= PADDING_SIZE_MIN) return bitmap;

        int x1 = startX - PADDING_SIZE_MIN;
        int y1 = startY - PADDING_SIZE_MIN;
        if (x1 < 0 || y1 < 0) return bitmap;

        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;

        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);

        return bitmapQR;
    }
}
