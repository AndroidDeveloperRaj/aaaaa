package com.merrichat.net.activity.video.editor;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merrichat.net.R;
import com.merrichat.net.adapter.VideoMusicAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.model.Song;
import com.merrichat.net.utils.MusicUtil;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/12/28.
 * 本地音乐
 */

public class MusicFragment extends BaseFragment implements VideoMusicAdapter.ChooseOnCheckListener, OnRefreshListener, VideoMusicAdapter.onMusicPlayerClick {
    public List<Song> musicList = new ArrayList<>();
    @BindView(R.id.recycler_view_music)
    RecyclerView recyclerViewMusic;
    Unbinder unbinder1;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout swipeRefreshLayout;
    private View view;
    private Unbinder unbinder;
    private VideoMusicAdapter adapter;
    private ChooseMusic chooseMusic;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String mediaPlayPath = "";

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
        musicList = MusicUtil.getMusicData();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMusic.setLayoutManager(linearLayoutManager);
        adapter = new VideoMusicAdapter(getActivity(), musicList);
        recyclerViewMusic.setAdapter(adapter);
        adapter.setChooseOnCheckListener(this);
        adapter.setOnMusicPlayerClicklister(this);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        musicList = MusicUtil.getMusicData();
        if (null == musicList || musicList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        initView();
        EventBus.getDefault().register(this);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (musicList == null || musicList.size() == 0) {

            }
        } else {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                adapter.setSetPlayingPosition(-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 使用音乐的点击事件
     *
     * @param position
     */
    @Override
    public void chooseOnCheckListener(int position) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            adapter.setSetPlayingPosition(-1);
            adapter.notifyDataSetChanged();
        }
        if (chooseMusic != null)
            chooseMusic.chooseMusic(musicList.get(position).getFileUrl(), musicList.get(position).getFileName());
    }

    /**
     * 音乐播放
     *
     * @param MusicPath
     * @param position
     */
    @Override
    public void onMusicPlayerClicklister(String MusicPath, int position) {
        setPlayerMusic(MusicPath, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            adapter.setSetPlayingPosition(-1);
            adapter.notifyDataSetChanged();
        }
    }

    public void setChooseMusic(ChooseMusic chooseMusic) {
        this.chooseMusic = chooseMusic;
    }

    /**
     * @param musicUrl 播放文件路径
     * @param position adapter 点击位置
     */
    private void setPlayerMusic(final String musicUrl, int position) {

        if (musicUrl.equals(mediaPlayPath)) {  //两次路径相等
            if (mediaPlayer.isPlaying()) {  //判断当前文件是否正在播放 if （true） 执行暂停  if(false) 播放
                mediaPlayer.pause();
                adapter.setSetPlayingPosition(-1);
            } else {
                mediaPlayer.start();
                adapter.setSetPlayingPosition(position);
            }

        } else { //第二次路径不相等  初始化mediaPlayer 从新播放
            mediaPlayPath = musicUrl;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(false);//是否循环播放
                mediaPlayer.start();
                adapter.setSetPlayingPosition(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("---->>>", "视屏播放完毕");
                try {
                    mediaPlayPath = "";
                    adapter.setSetPlayingPosition(-1);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        adapter.notifyDataSetChanged();

    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_MYACTIVITY) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                adapter.setSetPlayingPosition(-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 刷新  上拉
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        initView();
        swipeRefreshLayout.finishRefresh();
    }


    public interface ChooseMusic {
        void chooseMusic(String fileUrl, String fileName);
    }


}
