package com.merrichat.net.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.MoreMusicModel;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.HttpDownloader;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CircularProgressView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.merrichat.net.utils.HttpDownloader.FILE_DOWNLOAD_FAILED;

/**
 * 添加音乐适配器
 */
public class VideoMoreMusicAdapter extends RecyclerView.Adapter<VideoMoreMusicAdapter.ViewHolder> {

    /**
     * 存CheckBox的状态
     */
    private static Map<Integer, Boolean> playerMap;
    private Context context;
    private List<MoreMusicModel.DataBean.MusicBean> musicList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private ChooseOnCheckListener chooseOnCheckListener;
    private String musicUrl;

    private int setPlayingPosition = -1;

    public VideoMoreMusicAdapter(Context context, List<MoreMusicModel.DataBean.MusicBean> musicList) {
        this.context = context;
        this.musicList = musicList;

        playerMap = new HashMap<>();
        for (int i = 0; i < musicList.size(); i++) {
            playerMap.put(i, false);
        }

    }

    public void setMusicPlayerChange(int size) {
        for (int i = 0; i < size; i++) {
            playerMap.put(i, false);
        }
    }

    public void setSetPlayingPosition(int setPlayingPosition) {
        this.setPlayingPosition = setPlayingPosition;
        if (setPlayingPosition == -1) {
            for (int i = 0; i < musicList.size(); i++) {
                playerMap.put(i, false);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_music, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        if (musicList != null && musicList.size() > 0) {
            final MoreMusicModel.DataBean.MusicBean song = musicList.get(position);
            //如果数据一样就不再加载
            if (!TextUtils.equals((String) holder.simpleDraweeView.getTag(), song.getCover())) {
                holder.simpleDraweeView.setImageURI(song.getCover());
            }
            holder.simpleDraweeView.setTag(song.getCover());

            holder.tvMusicName.setText(song.getMusicName());
            holder.tvMusicAuthor.setText(song.getAuthor());
            holder.tvMusicTime.setText(song.getMusicTime());

            if (playerMap.get(position)) {
                holder.checkboxMusicPlayer.setChecked(true);
            } else {
                holder.checkboxMusicPlayer.setChecked(false);
                holder.progressView.setVisibility(View.GONE);
            }

            if (setPlayingPosition == position) {
                holder.progressView.setVisibility(View.GONE);
            }
            holder.tvDownload.setVisibility(View.GONE);
            holder.progressViewDownLoad.setVisibility(View.GONE);

            /**
             * 使用该音乐的按钮
             */
            holder.btnEmploy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetUtils.isNetworkAvailable(context)) {
                        int pos = holder.getLayoutPosition();
                        holder.tvDownload.setVisibility(View.VISIBLE);
                        holder.progressViewDownLoad.setVisibility(View.VISIBLE);
                        holder.btnEmploy.setVisibility(View.GONE);

                        Thread t = new Thread(new DownloadThread(song, holder));
                        t.start();
                    } else {
                        RxToast.showToast("请检查网络连接");
                    }
                }
            });

            /**
             * item的点击事件（里面操作解决乱序问题）
             */
            holder.relItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MerriUtils.isFastDoubleClick2()) {
                        return;
                    }
                    if (NetUtils.isNetworkAvailable(context)) {
                        int pos = holder.getLayoutPosition();
                        //音乐加载进度条
                        holder.progressView.setVisibility(View.VISIBLE);
                        if (holder.checkboxMusicPlayer.isChecked()) {
                            holder.checkboxMusicPlayer.setChecked(false);
                            for (int i = 0; i < musicList.size(); i++) {
                                if (i == pos) {
                                    playerMap.put(pos, false);
                                } else {
                                    playerMap.put(i, false);
                                }
                            }
                        } else {
                            holder.checkboxMusicPlayer.setChecked(true);
                            for (int i = 0; i < musicList.size(); i++) {
                                if (i == pos) {
                                    playerMap.put(pos, true);
                                } else {
                                    playerMap.put(i, false);
                                }
                            }
                        }
                        notifyDataSetChanged();
                        onMusicPlayerClick.onMusicPlayerClicklister(musicList.get(position).getMusicUrl(), pos);
                    } else {
                        RxToast.showToast("请检查网络连接");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    /**
     * 播放音乐回调
     */
    public interface onMusicPlayerClick {
        void onMusicPlayerClicklister(String MusicPath, int position);
    }

    public onMusicPlayerClick onMusicPlayerClick;

    public void setOnMusicPlayerClicklister(onMusicPlayerClick onMusicPlayerClick) {
        this.onMusicPlayerClick = onMusicPlayerClick;
    }

    public void setChooseOnCheckListener(ChooseOnCheckListener chooseOnCheckListener) {
        this.chooseOnCheckListener = chooseOnCheckListener;
    }

    /**
     * 下载mp3相关的文件
     */
    private int downloadMp3Files(MoreMusicModel.DataBean.MusicBean mp3Info) {
        int result = FILE_DOWNLOAD_FAILED;

        //根据mp3文件的名称，生成下载地址，如"http://192.168.1.102:8081/mp3_server/1.mp3"，需要解析中文URL
        String mp3UrlString = null;
        try {
            mp3UrlString = HttpDownloader.parseURL(mp3Info.getMusicUrl());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //下载文件到SD卡并反馈结果
        result = HttpDownloader.downloadFile(mp3UrlString, FileUtils.musicRootPath, mp3Info.getMusicName() + ".mp3");

        return result;
    }

    public interface ChooseOnCheckListener {
        void chooseOnCheckListener(String musicUrl, String musicName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * item点击布局
         */
        @BindView(R.id.rel_item)
        LinearLayout relItem;
        /**
         * 音乐图片
         */
        @BindView(R.id.simpleDraweeView)
        SimpleDraweeView simpleDraweeView;
        /**
         * 播放音乐按钮
         */
        @BindView(R.id.checkbox_music_player)
        CheckBox checkboxMusicPlayer;
        /**
         * 音乐名称
         */
        @BindView(R.id.tv_music_name)
        TextView tvMusicName;
        /**
         * 音乐作者
         */
        @BindView(R.id.tv_music_author)
        TextView tvMusicAuthor;
        /**
         * 音乐时长
         */
        @BindView(R.id.tv_music_time)
        TextView tvMusicTime;
        /**
         * 使用该音乐
         */
        @BindView(R.id.btn_employ)
        Button btnEmploy;
        /**
         * 加载进度条
         */
        @BindView(R.id.progressView)
        CircularProgressView progressView;
        /**
         * 下载进度条
         */
        @BindView(R.id.progressView_downLoad)
        CircularProgressView progressViewDownLoad;

        @BindView(R.id.tv_download)
        TextView tvDownload;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 启动线程下载音乐
     */
    class DownloadThread implements Runnable {
        ViewHolder holder;
        private MoreMusicModel.DataBean.MusicBean mp3Info = null;

        public DownloadThread(MoreMusicModel.DataBean.MusicBean mp3Info, ViewHolder holder) {
            this.mp3Info = mp3Info;
            this.holder = holder;
        }

        @Override
        public void run() {
            //提示开始下载
//            Logger.e("开始下载");
//            final int result = downloadMp3Files(mp3Info);
//
//            //使用Notification提示用户下载结果
//            if (FILE_DOWNLOAD_SUCCESS == result) {
//                Logger.e("下载成功");
//                musicUrl = FileUtils.musicRootPath + mp3Info.getMusicName() + ".mp3";
//            } else if (FILE_DOWNLOAD_EXIST == result) {
//                musicUrl = FileUtils.musicRootPath + mp3Info.getMusicName() + ".mp3";
//            } else if (FILE_DOWNLOAD_FAILED == result) {
//                Logger.e("下载失败");
//            }
//            ((Activity) context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                        holder.tvDownload.setVisibility(View.GONE);
//                        holder.progressViewDownLoad.setVisibility(View.GONE);
//                        holder.btnEmploy.setVisibility(View.VISIBLE);
//                        if (FILE_DOWNLOAD_FAILED != result) {
//                            chooseOnCheckListener.chooseOnCheckListener(musicUrl, mp3Info.getMusicName() + ".mp3");
//                        } else {
//                            RxToast.showToast("下载音乐失败,请检查网络");
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            //这里的下载线程暂时设置为固定的数值3，有兴趣的可以改成手动选择线程数量
            DownloadMusicUtil downloadUtil = new DownloadMusicUtil(mp3Info.getMusicUrl(), FileUtils.musicRootPath + mp3Info.getMusicName() + ".mp3", 1, context, mp3Info, holder);
            downloadUtil.download();
        }
    }

    public class DownloadMusicUtil {
        private String TAG = "DownloadUtil";
        private String targetUrl = null;
        private String targetFile = null;
        //多线程下载的线程数量
        private int threadNum;
        private Context mContext;
        //下载文件的大小
        private int fileSize;
        //下载任务是否是暂停状态
        private boolean pause;
        //下载任务是否被删除了
        private boolean delete;
        //下载完成的线程数，当所有线程下载完成时该值等于threadNum
        private int downloadSuccessThread;
        //下载的线程数组
        private DownloadThread[] downloadThreads;
        ViewHolder holder;
        private MoreMusicModel.DataBean.MusicBean mp3Info = null;

        /**
         * @param targetUrl  音乐路径
         * @param targetFile 输出音乐路径
         * @param threadNum  线程池的数量
         * @param context
         */
        public DownloadMusicUtil(String targetUrl, String targetFile, int threadNum, Context context, MoreMusicModel.DataBean.MusicBean mp3Info, ViewHolder holder) {
            this.targetUrl = targetUrl;
            this.targetFile = targetFile;
            this.threadNum = threadNum;
            downloadThreads = new DownloadThread[threadNum];
            mContext = context;
            this.mp3Info = mp3Info;
            this.holder = holder;
        }

        public void download() {
            try {
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                //获取下载文件的大小
                fileSize = conn.getContentLength();
                conn.disconnect();
                Log.d(TAG, "download：fileSize = " + fileSize);
                File file = new File(targetFile);
                if (!file.exists()) {
                    file.createNewFile();
                    Log.d(TAG, "create file:" + file);
                }
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.setLength(fileSize);
                raf.close();
                //划分每个线程的下载长度
                int currentFileSize = fileSize / threadNum == 0 ? fileSize / threadNum : fileSize / threadNum + 1;
                for (int i = 0; i < threadNum; i++) {
                    downloadThreads[i] = new DownloadThread(targetUrl, targetFile, currentFileSize * i, currentFileSize);
                    downloadThreads[i].start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //获取线程进度
        public int getDownloadProgress() {
            int downloadLength = 0;
            for (DownloadThread thread : downloadThreads) {
                if (thread != null) downloadLength += thread.length;
            }
            Log.d(TAG, "fileSize = " + fileSize + " downloadLength = " + downloadLength);
            return fileSize > 0 ? downloadLength * 100 / fileSize : 0;
        }

        public boolean isPause() {
            return pause;
        }

        public void setPause(boolean pause) {
            this.pause = pause;
        }

        public boolean isDelete() {
            return delete;
        }

        public void setDelete(boolean delete) {
            this.delete = delete;
        }

        //下载线程
        private class DownloadThread extends Thread {
            //当前线程下载的长度
            public int length;
            //下载的资源链接
            private String downloadUrl = null;
            //下载内容到file文件中
            private String file;
            //下载的起始位置
            private int startPos;
            //需要下载的长度
            private int currentFileSize;
            //读取网络音乐数据的缓存
            private BufferedInputStream bis;

            public DownloadThread(String downloadUrl, String file, int startPos, int currentFileSize) {
                this.downloadUrl = downloadUrl;
                this.file = file;
                this.startPos = startPos;
                this.currentFileSize = currentFileSize;
            }

            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    InputStream is = conn.getInputStream();
                    //获取的输入流数据跳过指定字节数
                    is.skip(startPos);
                    File downloadFile = new File(file);
                    RandomAccessFile raf = new RandomAccessFile(downloadFile, "rwd");
                    //对应文件写入位置也跳过指定字节数
                    raf.seek(startPos);
                    bis = new BufferedInputStream(is);
                    int hasRead = 0;
                    byte[] buff = new byte[1024 * 4];
                    while (length < currentFileSize && (hasRead = bis.read(buff)) > 0 && !delete) {
                        while (pause) {
                            Log.d(TAG, "DownloadUtil pause!");
                        }
                        if (!pause) {
                            //字节读入对应文件
                            raf.write(buff, 0, hasRead);
                            length += hasRead;
                            Log.d(TAG, "read " + hasRead + " bytes");
                        }
                    }
                    Log.d(TAG, "download success? " + (fileSize == length));
                    //关闭资源和链接
                    raf.close();
                    bis.close();
                    if (delete) {
                        boolean isDeleted = downloadFile.delete();
                        Log.d(TAG, "delete file success ? " + isDeleted);
                    } else {
                        Log.d(TAG, "run:currentFileSize = " + currentFileSize + " downloadLength = " + length);
                        //当前线程下载完成时，DownloadUtil的downloadSuccessThread值+1
                        downloadSuccessThread++;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (downloadSuccessThread == threadNum) {
                                    Log.d(TAG, "download success");
                                    Intent notifyIntent = new Intent("action_download_success");
                                    notifyIntent.putExtra("url", targetUrl);
                                    //给DownloadFragment发送下载完成的广播
                                    mContext.sendBroadcast(notifyIntent);
                                    scanFileToMedia(targetFile);

                                    musicUrl = FileUtils.musicRootPath + mp3Info.getMusicName() + ".mp3";
                                    holder.tvDownload.setVisibility(View.GONE);
                                    holder.progressViewDownLoad.setVisibility(View.GONE);
                                    holder.btnEmploy.setVisibility(View.VISIBLE);
                                    chooseOnCheckListener.chooseOnCheckListener(musicUrl, mp3Info.getMusicName() + ".mp3");
                                } else {
                                    RxToast.showToast("下载音乐失败,请检查网络");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        //将新下载的歌曲扫描到媒体库中，为后面MusicListFragment添加下拉刷新做铺垫
        public void scanFileToMedia(final String url) {
            new Thread(new Runnable() {
                public void run() {
                    MediaScannerConnection.scanFile(context, new String[]{url}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.d(TAG, "scan completed : file = " + url);
                                }
                            });
                }

            }).start();
        }
    }
}

