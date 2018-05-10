package com.merrichat.net.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
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
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.TimeUtil;
import com.merrichat.net.view.CircularProgressView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 添加音乐适配器
 */
public class VideoMusicAdapter extends RecyclerView.Adapter<VideoMusicAdapter.ViewHolder> {

    private Context context;
    private List<Song> musicList = new ArrayList<>();

    private ChooseOnCheckListener chooseOnCheckListener;

    public VideoMusicAdapter(Context context, List<Song> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    private int setPlayingPosition = -1;

    public void setSetPlayingPosition(int setPlayingPosition) {
        this.setPlayingPosition = setPlayingPosition;
    }

    public int getPlayingPosition() {
        return setPlayingPosition;
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
            Song song = musicList.get(position);
            holder.simpleDraweeView.setImageURI(song.getImageUrl());
            holder.tvMusicName.setText(song.getFileName());
            holder.tvMusicAuthor.setText(song.getSinger());
            holder.tvMusicTime.setText(TimeUtil.getMusicTime(song.getDuration()));

            if (setPlayingPosition == position) {
                holder.checkboxMusicPlayer.setChecked(true);
            } else {
                holder.checkboxMusicPlayer.setChecked(false);
            }
            holder.progressView.setVisibility(View.GONE);

            /**
             * 使用该音乐的按钮
             */
            holder.btnEmploy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MerriUtils.isFastDoubleClick2()) {
                        return;
                    }
                    int pos = holder.getLayoutPosition();
                    chooseOnCheckListener.chooseOnCheckListener(pos);
                }
            });

            /**
             * item的点击事件（里面操作解决乱序问题）
             */
            holder.relItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    File file = new File(musicList.get(position).getFileUrl());
                    if (file.isFile()){
                        onMusicPlayerClick.onMusicPlayerClicklister(musicList.get(position).getFileUrl(), position);
                    }else {
                        GetToast.showToast(context,"文件不存在，刷新下吧～！");

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setChooseOnCheckListener(ChooseOnCheckListener chooseOnCheckListener) {
        this.chooseOnCheckListener = chooseOnCheckListener;
    }

    public interface ChooseOnCheckListener {
        void chooseOnCheckListener(int position);
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
}

