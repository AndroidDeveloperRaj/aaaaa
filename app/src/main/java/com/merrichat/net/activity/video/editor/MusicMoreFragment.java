package com.merrichat.net.activity.video.editor;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.VideoMoreMusicAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CircleModel;
import com.merrichat.net.model.MoreMusicModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/12/28.
 * 更多音乐
 */

public class MusicMoreFragment extends BaseFragment implements VideoMoreMusicAdapter.ChooseOnCheckListener, VideoMoreMusicAdapter.onMusicPlayerClick, OnLoadmoreListener, OnRefreshListener {
    @BindView(R.id.recycler_view_music)
    RecyclerView recyclerViewMusic;
    Unbinder unbinder1;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private View view;
    private Unbinder unbinder;
    private VideoMoreMusicAdapter adapter;
    private List<MoreMusicModel.DataBean.MusicBean> musicList;
    private LoadingDialog loadingDialog;
    private ChooseMusicMore chooseMusic;
    private int pageSize = 10;
    private int currentPage = 1;
    private String name = "";
    private int REFRESHORLOADMORE = 1;
    private int newsPosition = -1;

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        musicList = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMusic.setLayoutManager(linearLayoutManager);
        adapter = new VideoMoreMusicAdapter(getActivity(), musicList);
        recyclerViewMusic.setAdapter(adapter);
        adapter.setChooseOnCheckListener(this);
        adapter.setOnMusicPlayerClicklister(this);

        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (musicList == null || musicList.size() == 0) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                //加载更多音乐
                queryMusic();
            }
        } else {
            if (mediaPlayer != null && adapter != null) {
                //mediaPlayer.pause();
                //mediaPlayer.stop();
                mediaPlayer.release();
                adapter.setSetPlayingPosition(-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void queryMusic() {
        OkGo.<String>get(Urls.QUERY_MUSIC)
                .tag(this)
                .params("name", name)
                .params("option", 1)
                .params("pageSize", pageSize)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            if (REFRESHORLOADMORE == 1) {
                                musicList.clear();
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadmore();
                            }
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    MoreMusicModel moreMusicModel = JSON.parseObject(response.body(), MoreMusicModel.class);
                                    musicList.addAll(moreMusicModel.getData().getMusic());
                                    if (moreMusicModel.getData().getMusic() == null || moreMusicModel.getData().getMusic().size() == 0) {
                                        refreshLayout.setLoadmoreFinished(true);
                                    }
                                    if (musicList == null || musicList.size() == 0) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tvEmpty.setText("暂无数据哦～");
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                        adapter.setMusicPlayerChange(musicList.size());
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        if (tvEmpty != null) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText("网络请求失败，请重试～");
                        }
                        if (refreshLayout != null) {
                            if (REFRESHORLOADMORE == 1) {
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadmore();
                            }
                        }
                    }
                });
    }

    /**
     * 使用音乐的点击事件
     *
     * @param musicUrl
     */
    @Override
    public void chooseOnCheckListener(String musicUrl, String fileName) {
        if (chooseMusic != null)
            chooseMusic.chooseMusicMore(musicUrl, fileName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            adapter.setSetPlayingPosition(-1);
            adapter.notifyDataSetChanged();
        }
    }

    public void setChooseMusicMore(ChooseMusicMore chooseMusic) {
        this.chooseMusic = chooseMusic;
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        REFRESHORLOADMORE = 0;
        currentPage++;
        queryMusic();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {

        }
        REFRESHORLOADMORE = 1;
        currentPage = 1;
        queryMusic();
    }

    /**
     * 播放音乐的点击事件
     *
     * @param MusicPath
     * @param position
     */
    @Override
    public void onMusicPlayerClicklister(final String MusicPath, final int position) {
        new Thread() {
            public void run() {
                //播放音乐路径
                setPlayerMusic(MusicPath, position);
            }
        }.start();
    }

    /**
     * 选择这首音乐的点击事件
     */
    public interface ChooseMusicMore {
        void chooseMusicMore(String fileUrl, String fileName);
    }

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String mediaPlayPath = "";

    /**
     * @param musicUrl 播放文件路径
     * @param position adapter 点击位置
     */
    private void setPlayerMusic(final String musicUrl, int position) {

        if (musicUrl.equals(mediaPlayPath) && newsPosition == position) {  //两次路径相等
            try {
                if (mediaPlayer.isPlaying()) {  //判断当前文件是否正在播放 if （true） 执行暂停  if(false) 播放
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                    adapter.setSetPlayingPosition(position);
                }
            } catch (Exception e) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(musicUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(true);//是否循环播放
                    mediaPlayer.start();
                    adapter.setSetPlayingPosition(position);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        } else { //第二次路径不相等  初始化mediaPlayer 从新播放
            mediaPlayPath = musicUrl;
            newsPosition = position;

            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);//是否循环播放
                mediaPlayer.start();
                adapter.setSetPlayingPosition(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayPath = "";
                mediaPlayer.stop();
                mediaPlayer.release();
                adapter.setSetPlayingPosition(-1);

            }
        });

        handlerCR.sendEmptyMessage(0x123);
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerCR = new Handler() {
        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 0x123) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_MYACTIVITY) {
            if (mediaPlayer != null || mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                adapter.setSetPlayingPosition(-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
