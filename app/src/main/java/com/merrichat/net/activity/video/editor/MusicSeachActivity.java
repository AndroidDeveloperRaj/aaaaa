package com.merrichat.net.activity.video.editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.merrifunction.MerriCameraFunctionAty;
import com.merrichat.net.activity.merrifunction.RecordVideoAty;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.adapter.LocalFileMusicAdapter;
import com.merrichat.net.adapter.VideoMoreMusicAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ListSongModel;
import com.merrichat.net.model.MoreMusicModel;
import com.merrichat.net.model.Song;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.DrawableCenterTextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索音乐
 * Created by amssy on 18/1/11.
 */

public class MusicSeachActivity extends BaseActivity implements VideoMoreMusicAdapter.ChooseOnCheckListener, VideoMoreMusicAdapter.onMusicPlayerClick {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.editText)
    ClearEditText editText;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private VideoMoreMusicAdapter adapter;
    private List<MoreMusicModel.DataBean.MusicBean> musicList;
    private int pageSize = 10;
    private int currentPage = 1;


    private ListSongModel listSongModel;  //本地音乐
    private ListSongModel SearchlistSong;  //本地音乐
    private String mFromFragment;  //判断当前从哪里来的fragment  0 表示本地 1表示服务器
    private Gson gson = new Gson();
    private LocalFileMusicAdapter localFileMusicAdapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String mediaPlayPath = "";
    private boolean startActivity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_seach);
        ButterKnife.bind(this);

        ivBack.setVisibility(View.GONE);
        tvTitleText.setText("选择音乐");


        mFromFragment = getIntent().getStringExtra("fragmentId");
        if (mFromFragment.equals("0")) {   //表示本地
            SearchlistSong = gson.fromJson(getIntent().getStringExtra("listSong"), ListSongModel.class);
            listSongModel = gson.fromJson(getIntent().getStringExtra("listSong"), ListSongModel.class);
            if (listSongModel.songs.size() > 0) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            initLocalView();
            initLocalRecyclerView();
        } else {  //表示从服务器

            initView();
            initRecyclerView();
        }


    }

    private void initView() {
        //搜索按钮监听
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //搜索时如果音乐在播放就要关闭音乐
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    queryMusic(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.tv_cancle})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancle://取消
                finish();
                break;

        }
    }


    private void initLocalView() {
        //搜索按钮监听
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.e("---->>>", "22222222");
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        if (mFromFragment.equals("0")) {
                            localFileMusicAdapter.setSetPlayingPosition(-1);
                            localFileMusicAdapter.notifyDataSetChanged();
                        } else {
                            adapter.setSetPlayingPosition(-1);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    //搜索时如果音乐在播放就要关闭音乐
                    queryLocalMusic(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }


    private void initLocalRecyclerView() {
        musicList = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        localFileMusicAdapter = new LocalFileMusicAdapter(this, listSongModel.songs);
        recyclerView.setAdapter(localFileMusicAdapter);
        localFileMusicAdapter.setChooseOnCheckListener(new LocalFileMusicAdapter.ChooseOnCheckListener() {
            @Override
            public void chooseOnCheckListener(String musicUrl, String musicName, int state, int position) {
                Log.e("---->>>", musicUrl + " " + musicName + "  " + state);
                if (state == 0) {
                    setPlayerMusic(musicUrl, state, position);
                } else {
                    if (getIntent().getIntExtra("activityId", 0) == MerriCameraFunctionAty.activityId) {
                        //RxToast.showToast("使用这首音乐拍摄");
                        Intent intent = new Intent(MusicSeachActivity.this, RecordVideoAty.class);
                        intent.putExtra("VideoMusic", musicUrl);
                        intent.putExtra("isHasMusic", true);
                        startActivity(intent);
                        finish();
                    } else if (getIntent().getIntExtra("activityId", 0) == ReleaseGraphicAlbumAty.activityId) {
                        Intent intent = new Intent();
                        intent.putExtra("VideoMusic", musicUrl);
                        intent.putExtra("fileName", musicName);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (getIntent().getIntExtra("activityId", 0) == RecordVideoAty.activityId) {
                        Intent intent = new Intent();
                        intent.putExtra("VideoMusic", musicUrl);
                        intent.putExtra("fileName", musicName);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("VideoMusic", musicUrl);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

    }


    /**
     * @param musicUrl 播放文件路径
     * @param state    文件名称
     * @param position adapter 点击位置
     */
    private void setPlayerMusic(final String musicUrl, final int state, int position) {

        if (musicUrl.equals(mediaPlayPath)) {  //两次路径相等
            if (mediaPlayer.isPlaying()) {  //判断当前文件是否正在播放 if （true） 执行暂停  if(false) 播放
                mediaPlayer.pause();
                localFileMusicAdapter.setSetPlayingPosition(-1);
            } else {
                mediaPlayer.start();
                localFileMusicAdapter.setSetPlayingPosition(position);
            }

        } else { //第二次路径不相等  初始化mediaPlayer 从新播放
            mediaPlayPath = musicUrl;

            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(false);//是否循环播放
                mediaPlayer.start();
                localFileMusicAdapter.setSetPlayingPosition(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("---->>>", "视屏播放完毕");
                mediaPlayPath = "";
                mediaPlayer.stop();
                mediaPlayer.release();
                localFileMusicAdapter.setSetPlayingPosition(-1);

            }
        });

        localFileMusicAdapter.notifyDataSetChanged();

    }


    private void queryLocalMusic(String name) {  //本地文件搜索
        localFileMusicAdapter.setSetPlayingPosition(-1);
        if (name.equals("") || name.length() == 0) {
            listSongModel.songs.clear();
            listSongModel.songs.addAll(SearchlistSong.songs);
            localFileMusicAdapter.notifyDataSetChanged();
        } else {
            listSongModel.songs.clear();
            for (int i = 0; i < SearchlistSong.songs.size(); i++) {
                if (SearchlistSong.songs.get(i).getFileName().indexOf(name) != -1) {
                    listSongModel.songs.add(SearchlistSong.songs.get(i));
                }
            }
            localFileMusicAdapter.notifyDataSetChanged();
        }
    }


    private void initRecyclerView() {
        musicList = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new VideoMoreMusicAdapter(this, musicList);
        recyclerView.setAdapter(adapter);
        adapter.setChooseOnCheckListener(this);
        adapter.setOnMusicPlayerClicklister(this);
    }

    private void queryMusic(String name) {
        OkGo.<String>get(Urls.QUERY_MUSIC)
                .tag(this)
                .params("name", name)
                .params("option", 1)
                .params("pageSize", pageSize)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    MoreMusicModel moreMusicModel = JSON.parseObject(response.body(), MoreMusicModel.class);
                                    musicList.clear();
                                    musicList.addAll(moreMusicModel.getData().getMusic());
                                    if (musicList.size() == 0) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tvEmpty.setText("未搜索到相关数据哦～");
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                    }
                                    adapter.setMusicPlayerChange(musicList.size());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    RxToast.showToast("" + jsonObjectEx.getString("msg"));
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
                    }
                });
    }

    /**
     * 使用音乐的点击事件
     *
     * @param musicUrl
     */
    @Override
    public void chooseOnCheckListener(String musicUrl, String musicName) {
        startActivity = true;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (getIntent().getIntExtra("activityId", 0) == MerriCameraFunctionAty.activityId) {
            //RxToast.showToast("使用这首音乐拍摄");
            Intent intent = new Intent(MusicSeachActivity.this, RecordVideoAty.class);
            intent.putExtra("VideoMusic", musicUrl);
            intent.putExtra("isHasMusic", true);
            startActivity(intent);
            finish();
        } else if (getIntent().getIntExtra("activityId", 0) == ReleaseGraphicAlbumAty.activityId) {
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", musicUrl);
            intent.putExtra("fileName", musicName);
            setResult(RESULT_OK, intent);
            finish();
        } else if (getIntent().getIntExtra("activityId", 0) == RecordVideoAty.activityId) {
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", musicUrl);
            intent.putExtra("fileName", musicName);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", musicUrl);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (startActivity){
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (startActivity){
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            if (mFromFragment.equals("0")) {
                localFileMusicAdapter.setSetPlayingPosition(-1);
                localFileMusicAdapter.notifyDataSetChanged();
            } else {
                adapter.setSetPlayingPosition(-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 网络音乐播放
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

            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
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
}
