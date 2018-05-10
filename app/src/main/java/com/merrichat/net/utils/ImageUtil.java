package com.merrichat.net.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by amssy on 17/11/10.
 */

public class ImageUtil {

    public static void saveBitmapToFile(final Context context, final Bitmap bitmap, final int width, final int height, final SavePhotoCompletedCallBack completedCallBack) {
        File dir = new File(FileUtils.photoRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileName = FileUtils.createPhotoCacheFileName(context);
        Logger.e("file : " + fileName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(fileName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                    newOpts.inJustDecodeBounds = true;
                    Bitmap bitmap1;
                    newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
                    // Decode bitmap with inSampleSize set
                    newOpts.inJustDecodeBounds = false;

                    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                    isBm = new ByteArrayInputStream(baos.toByteArray());
                    bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);

                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();
                    baos.flush();
                    baos.close();
                    isBm.close();
                    //bitmap.recycle();
                    bitmap1.recycle();

                    completedCallBack.onCompleted(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void saveBitmapToFile(final Bitmap bitmap, final int width, final int height, final SavePhotoCompletedCallBack completedCallBack) {
        File dir = new File(FileUtils.photoRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileName = FileUtils.createVideoCoverFileName();
        Logger.e("file : " + fileName);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(fileName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                    newOpts.inJustDecodeBounds = true;
                    Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
                    newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
                    // Decode bitmap with inSampleSize set
                    newOpts.inJustDecodeBounds = false;

                    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                    isBm = new ByteArrayInputStream(baos.toByteArray());
                    bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();
                    baos.flush();
                    baos.close();
                    isBm.close();
                    //bitmap.recycle();
                    bitmap1.recycle();

                    completedCallBack.onCompleted(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public interface SavePhotoCompletedCallBack {
        void onCompleted(String path);
    }
}
