package com.merrichat.net.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;

import com.faceunity.wrapper.faceunity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by AMSSY1 on 2017/11/15.
 */

public class FaceunityManger {
    private static boolean init = false;
    private static Handler handler;
    private static int faceBeautyItem = 0;
    private static int frame = 0;

    public static void loadItems(final Context context, final String filterName) {
        if (!init) {
            HandlerThread thread = new HandlerThread("FaceunityManger");
            thread.start();

            handler = new Handler(thread.getLooper());
            final byte[] authData = authpack.A();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        faceunity.fuCreateEGLContext();
                        InputStream inputSteam = context.getAssets().open("v3.mp3");
                        byte[] v3data;
                        v3data = new byte[inputSteam.available()];
                        inputSteam.read(v3data);
                        inputSteam.close();
                        faceunity.fuSetup(v3data, null, authData);
                        faceunity.fuSetMaxFaces(4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            init = true;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputSteam = context.getAssets().open("face_beautification.mp3");
                    byte[] itemData = new byte[inputSteam.available()];
                    inputSteam.read(itemData);
                    inputSteam.close();
                    faceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
                    faceunity.fuItemSetParam(faceBeautyItem, "filter_name", filterName);

//                    faceunity.fuItemSetParam(faceBeautyItem, "filter_name", "abao");
//                    faceunity.fuItemSetParam(faceBeautyItem, "blur_level", 6.0);
//                    faceunity.fuItemSetParam(faceBeautyItem, "color_level", 0.2);
//                    faceunity.fuItemSetParam(faceBeautyItem, "red_level", 0.5);
//                    faceunity.fuItemSetParam(faceBeautyItem, "face_shape", 3.0);
//                    faceunity.fuItemSetParam(faceBeautyItem, "face_shape_level", 0.5);
//                    faceunity.fuItemSetParam(faceBeautyItem, "cheek_thinning", 1.0);
//                    faceunity.fuItemSetParam(faceBeautyItem, "eye_enlarging", 0.5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void renderItemsToBitmap(final Context context, final String filter, final Bitmap src, final Callback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                byte[] bytes = new byte[src.getByteCount()];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                src.copyPixelsToBuffer(buffer);

                faceunity.fuRenderToRgbaImage(bytes, src.getWidth(), src.getHeight(), frame++, new int[]{faceBeautyItem});

                Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

                buffer.position(0);
                bitmap.copyPixelsFromBuffer(buffer);

                callback.invoke(bitmap);
            }
        });
    }


    public static void destroyItems() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                faceunity.fuDestroyAllItems();
                faceBeautyItem = 0;
                faceunity.fuOnDeviceLost();
            }
        });


    }

    public interface Callback {
        void invoke(Bitmap bitmap);
    }
}
