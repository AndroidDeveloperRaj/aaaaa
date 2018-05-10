package com.merrichat.net.activity.video.editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.merrifunction.MerriCameraFunctionAty;
import com.merrichat.net.activity.merrifunction.RecordVideoAty;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.ListSongModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MiscUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加音乐
 * Created by amssy on 17/11/8.
 */
public class VideoMusicActivity extends BaseActivity implements MusicFragment.ChooseMusic, MusicMoreFragment.ChooseMusicMore {

    public static int activityId = MiscUtil.getActivityId();
    public static int type = 0;//本地音乐  更多音乐
    /**
     * 左边按钮
     */
    @BindView(R.id.tv_left)
    TextView tvLeft;
    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitle;
    /**
     * 右边按钮
     */
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.viewPager_music)
    ViewPager viewPagerMusic;
    @BindView(R.id.btn_card)
    Button btnCard;
    @BindView(R.id.btn_more)
    Button btnMore;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.view_left)
    View viewLeft;
    @BindView(R.id.view_right)
    View viewRight;
    private ArrayList<Fragment> list;
    private MusicFragment musicFragment;
    private MusicMoreFragment musicMoreFragment;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_music);
        ButterKnife.bind(this);
        //设置TITLE
        initTitle();
        initView();
    }

    private void initView() {
        musicFragment = new MusicFragment();
        musicMoreFragment = new MusicMoreFragment();
        list = new ArrayList<>();
        list.add(musicFragment);
        list.add(musicMoreFragment);
        musicFragment.setChooseMusic(this);
        musicMoreFragment.setChooseMusicMore(this);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        };
        viewPagerMusic.setAdapter(adapter);
        viewPagerMusic.setCurrentItem(0);
        viewPagerMusic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerMusic.setCurrentItem(position);
                if (position == 0) {
                    btnCard.setTextColor(getResources().getColor(R.color.normal_red));
                    btnMore.setTextColor(getResources().getColor(R.color.normal_gray));
                    viewLeft.setVisibility(View.VISIBLE);
                    viewRight.setVisibility(View.GONE);
                } else {
                    btnCard.setTextColor(getResources().getColor(R.color.normal_gray));
                    btnMore.setTextColor(getResources().getColor(R.color.normal_red));
                    viewLeft.setVisibility(View.GONE);
                    viewRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        EventBus.getDefault().register(this);
    }

    /**
     * 设置TITLE
     */
    private void initTitle() {
        tvLeft.setText("取消");
        if (getIntent().getIntExtra("activityId", 0) == MerriCameraFunctionAty.activityId) {
            tvRight.setText("直接开拍");
        } else {
            tvRight.setText("");
        }
        tvTitle.setText("添加音乐");

    }

    @OnClick({R.id.iv_back,R.id.tv_left, R.id.tv_right, R.id.btn_card, R.id.btn_more, R.id.rel_search})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                MyEventBusModel eventBusModel1 = new MyEventBusModel();
                eventBusModel1.CANCEL_CHOOSE_MUSIC = true;
                EventBus.getDefault().post(eventBusModel1);

                finish();
                break;
            case R.id.tv_left://取消
                MyEventBusModel eventBusModel = new MyEventBusModel();
                eventBusModel.CANCEL_CHOOSE_MUSIC = true;
                EventBus.getDefault().post(eventBusModel);

                finish();
                break;
            case R.id.tv_right://直接开拍
                startActivity(new Intent(VideoMusicActivity.this, RecordVideoAty.class).putExtra("isHasMusic", false).putExtra("activityId", activityId));
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                finish();
                break;
            case R.id.tv_cancle:
                finish();
                break;
            case R.id.btn_card://本地音乐
                viewPagerMusic.setCurrentItem(0);
                btnCard.setTextColor(getResources().getColor(R.color.normal_red));
                btnMore.setTextColor(getResources().getColor(R.color.normal_gray));
                viewLeft.setVisibility(View.VISIBLE);
                viewRight.setVisibility(View.GONE);
                break;
            case R.id.btn_more://更多音乐
                viewPagerMusic.setCurrentItem(1);
                btnMore.setTextColor(getResources().getColor(R.color.normal_red));
                btnCard.setTextColor(getResources().getColor(R.color.normal_gray));
                viewLeft.setVisibility(View.GONE);
                viewRight.setVisibility(View.VISIBLE);
                break;
            case R.id.rel_search://搜索音乐
                ListSongModel listSongModel = new ListSongModel();
                if (viewPagerMusic.getCurrentItem() == 0) {
                    listSongModel.songs = musicFragment.musicList;
                    if (musicFragment.musicList == null) {
                        GetToast.showToast(VideoMusicActivity.this, "手机音乐列表为空");
                        return;
                    }
                }
                Intent intent = new Intent(VideoMusicActivity.this, MusicSeachActivity.class);
                intent.putExtra("activityId", getIntent().getIntExtra("activityId", 0));
                intent.putExtra("fragmentId", viewPagerMusic.getCurrentItem() + "");
                intent.putExtra("listSong", gson.toJson(listSongModel, ListSongModel.class));
                startActivityForResult(intent, RESULT_FIRST_USER);
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                break;
        }
    }

    /**
     * 已选音乐(本地音乐)
     *
     * @param fileUrl
     */
    @Override
    public void chooseMusic(String fileUrl, String fileName) {
        if (getIntent().getIntExtra("activityId", 0) == MerriCameraFunctionAty.activityId) {
//            Log.e("----->>>", "chooseMusic:" + 1 + "");
            //RxToast.showToast("使用这首音乐拍摄");
            Intent intent = new Intent(VideoMusicActivity.this, RecordVideoAty.class);
            intent.putExtra("VideoMusic", fileUrl);
            intent.putExtra("isHasMusic", true);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        } else if (getIntent().getIntExtra("activityId", 0) == ReleaseGraphicAlbumAty.activityId) {
//            Log.e("----->>>", "chooseMusic:" + 2 + "");
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", fileUrl);
            intent.putExtra("fileName", fileName);
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        } else if (getIntent().getIntExtra("activityId", 0) == RecordVideoAty.activityId) {
//            Log.e("----->>>", "chooseMusic:" + 2 + "");
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", fileUrl);
            intent.putExtra("fileName", fileName);
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        } else {
            //Log.e("----->>>", "chooseMusic:" + 3 + "");
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", fileUrl);
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        }

    }

    /**
     * 已选音乐(更多音乐)
     *
     * @param fileUrl
     */
    @Override
    public void chooseMusicMore(String fileUrl, String fileName) {
        if (getIntent().getIntExtra("activityId", 0) == MerriCameraFunctionAty.activityId) {
            //RxToast.showToast("使用这首音乐拍摄");
            Logger.e("===" + fileUrl);
            Intent intent = new Intent(VideoMusicActivity.this, RecordVideoAty.class);
            intent.putExtra("VideoMusic", fileUrl);
            intent.putExtra("isHasMusic", true);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        } else if (getIntent().getIntExtra("activityId", 0) == ReleaseGraphicAlbumAty.activityId) {
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", fileUrl);
            intent.putExtra("fileName", fileName);
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        } else if (getIntent().getIntExtra("activityId", 0) == RecordVideoAty.activityId) {
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", fileUrl);
            intent.putExtra("fileName", fileName);
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("VideoMusic", fileUrl);
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER) {
            if (resultCode == RESULT_OK && data != null) {
                Intent intent = new Intent();
                intent.putExtra("VideoMusic", data.getStringExtra("VideoMusic"));
                intent.putExtra("fileName", data.getStringExtra("fileName"));
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.CANCEL_CHOOSE_MUSIC = true;
            EventBus.getDefault().post(eventBusModel);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_VIDEO_ACTIVITY) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
