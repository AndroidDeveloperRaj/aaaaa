package com.merrichat.net.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.MoreMusicModel;
import com.merrichat.net.model.Song;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.HttpDownloader;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CircularProgressView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.merrichat.net.utils.HttpDownloader.FILE_DOWNLOAD_FAILED;

public class LocalFileMusicAdapter extends RecyclerView.Adapter<LocalFileMusicAdapter.ViewHolder> {

    private Context context;
    private List<Song> musicList = new ArrayList<>();

    private ChooseOnCheckListener chooseOnCheckListener;
    private String musicUrl;
    private int numMusic = -1;//记录是否播放同一首音乐

    private int setPlayingPosition = -1;


    public LocalFileMusicAdapter(Context context, List<Song> musicList) {
        this.context = context;
        this.musicList = musicList;
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
            final Song song = musicList.get(position);
            holder.simpleDraweeView.setImageURI(song.getImageUrl());
            holder.tvMusicName.setText(song.getFileName());
            holder.tvMusicAuthor.setText(song.getSinger());
            holder.tvMusicTime.setText(DateTimeUtil.format9(new Date(song.getDuration())));

            holder.progressView.setVisibility(View.GONE);

            holder.tvDownload.setVisibility(View.GONE);
            holder.progressViewDownLoad.setVisibility(View.GONE);

            if (setPlayingPosition == position) {
                holder.checkboxMusicPlayer.setChecked(true);
            } else {
                holder.checkboxMusicPlayer.setChecked(false);
            }

            /**
             * 使用该音乐的按钮
             */
            holder.btnEmploy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseOnCheckListener.chooseOnCheckListener(song.getFileUrl(), song.getFileName(), 1, position);
                }
            });

            /**
             * item的点击事件（里面操作解决乱序问题）
             */
            holder.relItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseOnCheckListener.chooseOnCheckListener(song.getFileUrl(), song.getFileName(), 0, position);
                }
            });
        }
    }

    public int getSetPlayingPosition() {
        return setPlayingPosition;
    }

    public void setSetPlayingPosition(int setPlayingPosition) {
        this.setPlayingPosition = setPlayingPosition;
    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public void setChooseOnCheckListener(ChooseOnCheckListener chooseOnCheckListener) {
        this.chooseOnCheckListener = chooseOnCheckListener;
    }


    public interface ChooseOnCheckListener {
        /**
         * @param musicUrl  图片的地址
         * @param musicName 图片的名称
         * @param state     点击的时候状态，判断当前点击的view  0是点击的播放按钮   1是点击使用按钮
         * @param musicName 点击的位置
         */
        void chooseOnCheckListener(String musicUrl, String musicName, int state, int position);
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


}
