package com.merrichat.net.activity.meiyu;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;

import org.webrtc.GlRectDrawer;
import org.webrtc.GlUtil;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by amssy on 18/1/5.
 */

public class VideoRendererGui implements GLSurfaceView.Renderer {
    private static VideoRendererGui instance = null;
    private static Runnable eglContextReady = null;
    private static final String TAG = "VideoRendererGui";
    private GLSurfaceView surface;
    private static EGLContext eglContext = null;
    private boolean onSurfaceCreatedCalled;
    private int screenWidth;
    private int screenHeight;
    private final ArrayList<VideoRendererGui.YuvImageRenderer> yuvImageRenderers;
    private GlRectDrawer drawer;
    private static final int EGL14_SDK_VERSION = 17;
    private static final int CURRENT_SDK_VERSION;


    public  static boolean isStunSunccess = false;

    private VideoRendererGui(GLSurfaceView surface) {
        this.surface = surface;
        surface.setPreserveEGLContextOnPause(true);
        surface.setEGLContextClientVersion(2);
        surface.setRenderer(this);
        surface.setRenderMode(0);
        this.yuvImageRenderers = new ArrayList();
    }

    public static synchronized void setView(GLSurfaceView surface, Runnable eglContextReadyCallback) {
        Log.d("VideoRendererGui", "VideoRendererGui.setView");
        instance = new VideoRendererGui(surface);
        eglContextReady = eglContextReadyCallback;
    }

    public static synchronized EGLContext getEGLContext() {
        return eglContext;
    }

    public static synchronized void dispose() {
        if(instance != null) {
            Log.d("VideoRendererGui", "VideoRendererGui.dispose");
            ArrayList var0 = instance.yuvImageRenderers;
            synchronized(instance.yuvImageRenderers) {
                Iterator i$ = instance.yuvImageRenderers.iterator();

                while(i$.hasNext()) {
                    VideoRendererGui.YuvImageRenderer yuvImageRenderer = (VideoRendererGui.YuvImageRenderer)i$.next();
                    yuvImageRenderer.release();
                }

                instance.yuvImageRenderers.clear();
                if(instance.drawer != null) {
                    instance.drawer.release();
                }
            }

            instance.surface = null;
            eglContext = null;
            eglContextReady = null;
            instance = null;
        }
    }

    public static VideoRenderer createGui(int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror) throws Exception {
        VideoRendererGui.YuvImageRenderer javaGuiRenderer = create(x, y, width, height, scalingType, mirror);
        return new VideoRenderer(javaGuiRenderer);
    }

    public static VideoRenderer.Callbacks createGuiRenderer(int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror) {
        return create(x, y, width, height, scalingType, mirror);
    }

    public static synchronized VideoRendererGui.YuvImageRenderer create(int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror) {
        if(x >= 0 && x <= 100 && y >= 0 && y <= 100 && width >= 0 && width <= 100 && height >= 0 && height <= 100 && x + width <= 100 && y + height <= 100) {
            if(instance == null) {
                throw new RuntimeException("Attempt to create yuv renderer before setting GLSurfaceView");
            } else {
                final VideoRendererGui.YuvImageRenderer yuvImageRenderer = new VideoRendererGui.YuvImageRenderer(instance.surface, instance.yuvImageRenderers.size(), x, y, width, height, scalingType, mirror);
                ArrayList var7 = instance.yuvImageRenderers;
                synchronized(instance.yuvImageRenderers) {
                    if(instance.onSurfaceCreatedCalled) {
                        final CountDownLatch countDownLatch = new CountDownLatch(1);
                        instance.surface.queueEvent(new Runnable() {
                            public void run() {
                                yuvImageRenderer.createTextures();
                                yuvImageRenderer.setScreenSize(VideoRendererGui.instance.screenWidth, VideoRendererGui.instance.screenHeight);
                                countDownLatch.countDown();
                            }
                        });

                        try {
                            countDownLatch.await();
                        } catch (InterruptedException var11) {
                            throw new RuntimeException(var11);
                        }
                    }

                    instance.yuvImageRenderers.add(yuvImageRenderer);
                    return yuvImageRenderer;
                }
            }
        } else {
            throw new RuntimeException("Incorrect window parameters.");
        }
    }

    public static synchronized void update(VideoRenderer.Callbacks renderer, int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror) {
        Log.d("VideoRendererGui", "VideoRendererGui.update");
        if(instance == null) {
            throw new RuntimeException("Attempt to update yuv renderer before setting GLSurfaceView");
        } else {
            ArrayList var7 = instance.yuvImageRenderers;
            synchronized(instance.yuvImageRenderers) {
                Iterator i$ = instance.yuvImageRenderers.iterator();

                while(i$.hasNext()) {
                    VideoRendererGui.YuvImageRenderer yuvImageRenderer = (VideoRendererGui.YuvImageRenderer)i$.next();
                    if(yuvImageRenderer == renderer) {
                        yuvImageRenderer.setPosition(x, y, width, height, scalingType, mirror);
                    }
                }

            }
        }
    }

    public static synchronized void setRendererEvents(VideoRenderer.Callbacks renderer, RendererCommon.RendererEvents rendererEvents) {
        Log.d("VideoRendererGui", "VideoRendererGui.setRendererEvents");
        if(instance == null) {
            throw new RuntimeException("Attempt to set renderer events before setting GLSurfaceView");
        } else {
            ArrayList var2 = instance.yuvImageRenderers;
            synchronized(instance.yuvImageRenderers) {
                Iterator i$ = instance.yuvImageRenderers.iterator();

                while(i$.hasNext()) {
                    VideoRendererGui.YuvImageRenderer yuvImageRenderer = (VideoRendererGui.YuvImageRenderer)i$.next();
                    if(yuvImageRenderer == renderer) {
                        yuvImageRenderer.rendererEvents = rendererEvents;
                    }
                }

            }
        }
    }

    public static synchronized void remove(VideoRenderer.Callbacks renderer) {
        Log.d("VideoRendererGui", "VideoRendererGui.remove");
        if(instance == null) {
            throw new RuntimeException("Attempt to remove yuv renderer before setting GLSurfaceView");
        } else {
            ArrayList var1 = instance.yuvImageRenderers;
            synchronized(instance.yuvImageRenderers) {
                int index = instance.yuvImageRenderers.indexOf(renderer);
                if(index == -1) {
                    Log.w("VideoRendererGui", "Couldn't remove renderer (not present in current list)");
                } else {
                    ((VideoRendererGui.YuvImageRenderer)instance.yuvImageRenderers.remove(index)).release();
                }

            }
        }
    }

    @SuppressLint({"NewApi"})
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Log.d("VideoRendererGui", "VideoRendererGui.onSurfaceCreated");
        Class var3;
        if(CURRENT_SDK_VERSION >= 17) {
            var3 = VideoRendererGui.class;
            synchronized(VideoRendererGui.class) {
                eglContext = EGL14.eglGetCurrentContext();
                Log.d("VideoRendererGui", "VideoRendererGui EGL Context: " + eglContext);
            }
        }

        ArrayList var11 = this.yuvImageRenderers;
        synchronized(this.yuvImageRenderers) {
            this.drawer = new GlRectDrawer();
            Iterator i$ = this.yuvImageRenderers.iterator();

            while(true) {
                if(!i$.hasNext()) {
                    this.onSurfaceCreatedCalled = true;
                    break;
                }

                VideoRendererGui.YuvImageRenderer yuvImageRenderer = (VideoRendererGui.YuvImageRenderer)i$.next();
                yuvImageRenderer.createTextures();
            }
        }

        GlUtil.checkNoGLES2Error("onSurfaceCreated done");
        GLES20.glPixelStorei(3317, 1);
        GLES20.glClearColor(0.15F, 0.15F, 0.15F, 1.0F);
        var3 = VideoRendererGui.class;
        synchronized(VideoRendererGui.class) {
            if(eglContextReady != null) {
                eglContextReady.run();
            }

        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.d("VideoRendererGui", "VideoRendererGui.onSurfaceChanged: " + width + " x " + height + "  ");
        this.screenWidth = width;
        this.screenHeight = height;
        ArrayList var4 = this.yuvImageRenderers;
        synchronized(this.yuvImageRenderers) {
            Iterator i$ = this.yuvImageRenderers.iterator();

            while(i$.hasNext()) {
                VideoRendererGui.YuvImageRenderer yuvImageRenderer = (VideoRendererGui.YuvImageRenderer)i$.next();
                yuvImageRenderer.setScreenSize(this.screenWidth, this.screenHeight);
            }

        }
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glViewport(0, 0, this.screenWidth, this.screenHeight);
        GLES20.glClear(16384);
        ArrayList var2 = this.yuvImageRenderers;
        synchronized(this.yuvImageRenderers) {
            Iterator i$ = this.yuvImageRenderers.iterator();

            while(i$.hasNext()) {
                VideoRendererGui.YuvImageRenderer yuvImageRenderer = (VideoRendererGui.YuvImageRenderer)i$.next();
                yuvImageRenderer.draw(this.drawer);
            }

        }
    }

    static {
        CURRENT_SDK_VERSION = Build.VERSION.SDK_INT;
    }

    private static class YuvImageRenderer implements VideoRenderer.Callbacks {
        private GLSurfaceView surface;
        private int id;
        private int[] yuvTextures;
        private int oesTexture;
        private final LinkedBlockingQueue<VideoRenderer.I420Frame> frameToRenderQueue;
        private VideoRenderer.I420Frame yuvFrameToRender;
        private VideoRenderer.I420Frame textureFrameToRender;
        private VideoRendererGui.YuvImageRenderer.RendererType rendererType;
        private RendererCommon.ScalingType scalingType;
        private boolean mirror;
        private RendererCommon.RendererEvents rendererEvents;
        boolean seenFrame;
        private int framesReceived;
        private int framesDropped;
        private int framesRendered;
        private long startTimeNs;
        private long drawTimeNs;
        private long copyTimeNs;
        private final Rect layoutInPercentage;
        private final Rect displayLayout;
        private final float[] texMatrix;
        private boolean updateTextureProperties;
        private final Object updateTextureLock;
        private int screenWidth;
        private int screenHeight;
        private int videoWidth;
        private int videoHeight;
        private int rotationDegree;

        private YuvImageRenderer(GLSurfaceView surface, int id, int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror) {
            this.yuvTextures = new int[]{-1, -1, -1};
            this.oesTexture = -1;
            this.startTimeNs = -1L;
            this.displayLayout = new Rect();
            this.texMatrix = new float[16];
            this.updateTextureLock = new Object();
            Log.d("VideoRendererGui", "YuvImageRenderer.Create id: " + id);
            this.surface = surface;
            this.id = id;
            this.scalingType = scalingType;
            this.mirror = mirror;
            this.frameToRenderQueue = new LinkedBlockingQueue(1);
            this.layoutInPercentage = new Rect(x, y, Math.min(100, x + width), Math.min(100, y + height));
            this.updateTextureProperties = false;
            this.rotationDegree = 0;
        }

        private synchronized void release() {
            this.surface = null;
            LinkedBlockingQueue var1 = this.frameToRenderQueue;
            synchronized(this.frameToRenderQueue) {
                this.frameToRenderQueue.clear();
                this.yuvFrameToRender = null;
                this.textureFrameToRender = null;
            }
        }

        private void createTextures() {
            Log.d("VideoRendererGui", "  YuvImageRenderer.createTextures " + this.id + " on GL thread:" + Thread.currentThread().getId());
            GLES20.glGenTextures(3, this.yuvTextures, 0);

            for(int i = 0; i < 3; ++i) {
                GLES20.glActiveTexture('蓀' + i);
                GLES20.glBindTexture(3553, this.yuvTextures[i]);
                GLES20.glTexParameterf(3553, 10241, 9729.0F);
                GLES20.glTexParameterf(3553, 10240, 9729.0F);
                GLES20.glTexParameterf(3553, 10242, 33071.0F);
                GLES20.glTexParameterf(3553, 10243, 33071.0F);
            }

            GlUtil.checkNoGLES2Error("y/u/v glGenTextures");
        }

        private void checkAdjustTextureCoords() {
            Object var1 = this.updateTextureLock;
            synchronized(this.updateTextureLock) {
                if(this.updateTextureProperties) {
                    this.displayLayout.set((this.screenWidth * this.layoutInPercentage.left + 99) / 100, (this.screenHeight * this.layoutInPercentage.top + 99) / 100, this.screenWidth * this.layoutInPercentage.right / 100, this.screenHeight * this.layoutInPercentage.bottom / 100);
                    Log.d("VideoRendererGui", "ID: " + this.id + ". AdjustTextureCoords. Allowed display size: " + this.displayLayout.width() + " x " + this.displayLayout.height() + ". Video: " + this.videoWidth + " x " + this.videoHeight + ". Rotation: " + this.rotationDegree + ". Mirror: " + this.mirror);
                    float videoAspectRatio = this.rotationDegree % 180 == 0?(float)this.videoWidth / (float)this.videoHeight:(float)this.videoHeight / (float)this.videoWidth;
                    Point displaySize = RendererCommon.getDisplaySize(this.scalingType, videoAspectRatio, this.displayLayout.width(), this.displayLayout.height());
                    this.displayLayout.inset((this.displayLayout.width() - displaySize.x) / 2, (this.displayLayout.height() - displaySize.y) / 2);
                    Log.d("VideoRendererGui", "  Adjusted display size: " + this.displayLayout.width() + " x " + this.displayLayout.height());
                    RendererCommon.getTextureMatrix(this.texMatrix, (float)this.rotationDegree, this.mirror, videoAspectRatio, (float)this.displayLayout.width() / (float)this.displayLayout.height());
                    this.updateTextureProperties = false;
                    Log.d("VideoRendererGui", "  AdjustTextureCoords done");
                }
            }
        }

        private void draw(GlRectDrawer drawer) {
            if(this.seenFrame) {
                long now = System.nanoTime();
                GLES20.glViewport(this.displayLayout.left, this.screenHeight - this.displayLayout.bottom, this.displayLayout.width(), this.displayLayout.height());
                LinkedBlockingQueue var5 = this.frameToRenderQueue;
                VideoRenderer.I420Frame frameFromQueue;
                synchronized(this.frameToRenderQueue) {
                    this.checkAdjustTextureCoords();
                    frameFromQueue = (VideoRenderer.I420Frame)this.frameToRenderQueue.peek();
                    if(frameFromQueue != null && this.startTimeNs == -1L) {
                        this.startTimeNs = now;
                    }

                    if(frameFromQueue != null) {
                        if(frameFromQueue.yuvFrame) {
//                            Log.e("@@@","穿透------11111111111111111");
                            for(int i = 0; i < 3; ++i) {
                                GLES20.glActiveTexture('蓀' + i);
                                GLES20.glBindTexture(3553, this.yuvTextures[i]);
                                int w = i == 0?frameFromQueue.width:frameFromQueue.width / 2;
                                int h = i == 0?frameFromQueue.height:frameFromQueue.height / 2;
                                GLES20.glTexImage2D(3553, 0, 6409, w, h, 0, 6409, 5121, frameFromQueue.yuvPlanes[i]);
                            }
                        } else {
                                isStunSunccess = true;
//                            Log.e("@@@","穿透------22222222222222222222");
                            this.oesTexture = frameFromQueue.textureId;
                            if(frameFromQueue.textureObject instanceof SurfaceTexture) {
                                SurfaceTexture surfaceTexture = (SurfaceTexture)frameFromQueue.textureObject;
                                surfaceTexture.updateTexImage();
                            }
                        }

                        this.frameToRenderQueue.poll();
                    }
                }

                if(this.rendererType == VideoRendererGui.YuvImageRenderer.RendererType.RENDERER_YUV) {
                    drawer.drawYuv(this.videoWidth, this.videoHeight, this.yuvTextures, this.texMatrix);
                } else {
                    drawer.drawOes(this.oesTexture, this.texMatrix);
                }

                if(frameFromQueue != null) {
                    ++this.framesRendered;
                    this.drawTimeNs += System.nanoTime() - now;
                    if(this.framesRendered % 300 == 0) {
                        this.logStatistics();
                    }
                }

            }
        }

        private void logStatistics() {
            long timeSinceFirstFrameNs = System.nanoTime() - this.startTimeNs;
            Log.d("VideoRendererGui", "ID: " + this.id + ". Type: " + this.rendererType + ". Frames received: " + this.framesReceived + ". Dropped: " + this.framesDropped + ". Rendered: " + this.framesRendered);
            if(this.framesReceived > 0 && this.framesRendered > 0) {
                Log.d("VideoRendererGui", "Duration: " + (int)((double)timeSinceFirstFrameNs / 1000000.0D) + " ms. FPS: " + (double)((float)this.framesRendered) * 1.0E9D / (double)timeSinceFirstFrameNs);
                Log.d("VideoRendererGui", "Draw time: " + (int)(this.drawTimeNs / (long)(1000 * this.framesRendered)) + " us. Copy time: " + (int)(this.copyTimeNs / (long)(1000 * this.framesReceived)) + " us");
            }

        }

        public void setScreenSize(int screenWidth, int screenHeight) {
            Object var3 = this.updateTextureLock;
            synchronized(this.updateTextureLock) {
                if(screenWidth != this.screenWidth || screenHeight != this.screenHeight) {
                    Log.d("VideoRendererGui", "ID: " + this.id + ". YuvImageRenderer.setScreenSize: " + screenWidth + " x " + screenHeight);
                    this.screenWidth = screenWidth;
                    this.screenHeight = screenHeight;
                    this.updateTextureProperties = true;
                }
            }
        }

        public void setPosition(int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror) {
            Rect layoutInPercentage = new Rect(x, y, Math.min(100, x + width), Math.min(100, y + height));
            Object var8 = this.updateTextureLock;
            synchronized(this.updateTextureLock) {
                if(!layoutInPercentage.equals(this.layoutInPercentage) || scalingType != this.scalingType || mirror != this.mirror) {
                    Log.d("VideoRendererGui", "ID: " + this.id + ". YuvImageRenderer.setPosition: (" + x + ", " + y + ") " + width + " x " + height + ". Scaling: " + scalingType + ". Mirror: " + mirror);
                    this.layoutInPercentage.set(layoutInPercentage);
                    this.scalingType = scalingType;
                    this.mirror = mirror;
                    this.updateTextureProperties = true;
                }
            }
        }

        private void setSize(int videoWidth, int videoHeight, int rotation) {
            if(videoWidth != this.videoWidth || videoHeight != this.videoHeight || rotation != this.rotationDegree) {
                if(this.rendererEvents != null) {
                    Log.d("VideoRendererGui", "ID: " + this.id + ". Reporting frame resolution changed to " + videoWidth + " x " + videoHeight);
                    this.rendererEvents.onFrameResolutionChanged(videoWidth, videoHeight, rotation);
                }

                LinkedBlockingQueue var4 = this.frameToRenderQueue;
                synchronized(this.frameToRenderQueue) {
                    Log.d("VideoRendererGui", "ID: " + this.id + ". YuvImageRenderer.setSize: " + videoWidth + " x " + videoHeight + " rotation " + rotation);
                    this.videoWidth = videoWidth;
                    this.videoHeight = videoHeight;
                    this.rotationDegree = rotation;
                    int[] strides = new int[]{videoWidth, videoWidth / 2, videoWidth / 2};
                    this.frameToRenderQueue.poll();
                    this.yuvFrameToRender = new VideoRenderer.I420Frame(videoWidth, videoHeight, this.rotationDegree, strides, (ByteBuffer[])null);
                    this.textureFrameToRender = new VideoRenderer.I420Frame(videoWidth, videoHeight, this.rotationDegree, (Object)null, -1);
                    this.updateTextureProperties = true;
                    Log.d("VideoRendererGui", "  YuvImageRenderer.setSize done.");
                }
            }
        }

        public synchronized void renderFrame(VideoRenderer.I420Frame frame) {
            if(this.surface != null) {
                if(!this.seenFrame && this.rendererEvents != null) {
                    Log.d("VideoRendererGui", "ID: " + this.id + ". Reporting first rendered frame.");
                    this.rendererEvents.onFirstFrameRendered();
                }

                this.setSize(frame.width, frame.height, frame.rotationDegree);
                long now = System.nanoTime();
                ++this.framesReceived;
                LinkedBlockingQueue var4 = this.frameToRenderQueue;
                synchronized(this.frameToRenderQueue) {
                    if(this.yuvFrameToRender == null || this.textureFrameToRender == null) {
                        ++this.framesDropped;
                        return;
                    }

                    if(frame.yuvFrame) {
                        if(frame.yuvStrides[0] < frame.width || frame.yuvStrides[1] < frame.width / 2 || frame.yuvStrides[2] < frame.width / 2) {
                            Log.e("VideoRendererGui", "Incorrect strides " + frame.yuvStrides[0] + ", " + frame.yuvStrides[1] + ", " + frame.yuvStrides[2]);
                            return;
                        }

                        if(frame.width != this.yuvFrameToRender.width || frame.height != this.yuvFrameToRender.height) {
                            throw new RuntimeException("Wrong frame size " + frame.width + " x " + frame.height);
                        }
                    }

                    if(this.frameToRenderQueue.size() > 0) {
                        ++this.framesDropped;
                        return;
                    }

                    if(frame.yuvFrame) {
                        this.yuvFrameToRender.copyFrom(frame);
                        this.rendererType = VideoRendererGui.YuvImageRenderer.RendererType.RENDERER_YUV;
                        this.frameToRenderQueue.offer(this.yuvFrameToRender);
                    } else {
                        this.textureFrameToRender.copyFrom(frame);
                        this.rendererType = VideoRendererGui.YuvImageRenderer.RendererType.RENDERER_TEXTURE;
                        this.frameToRenderQueue.offer(this.textureFrameToRender);
                    }
                }

                this.copyTimeNs += System.nanoTime() - now;
                this.seenFrame = true;
                this.surface.requestRender();
            }
        }

        private static enum RendererType {
            RENDERER_YUV,
            RENDERER_TEXTURE;

            private RendererType() {
            }
        }
    }
}
