package com.merrichat.net.activity.message.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.message.storage.PhotoCheck;
import com.merrichat.net.activity.message.storage.PhotoCount;
import com.merrichat.net.activity.message.util.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * 预览显示的界面
 *
 * @author amssy
 */

public class PhotopreviewActivity extends BaseActivity implements OnPageChangeListener, OnClickListener, OnCheckedChangeListener {


    protected int current;
    protected boolean isUp;
    private ViewPager mViewPager;
    private RelativeLayout layoutTop;
    private ArrayList<String> photoChecks = new PhotoCount().getPhotosPaths();
    private ArrayList<PhotoCheck> photoCounts = new ArrayList<PhotoCheck>();
    /**
     * 是否选择这张照片
     */
    private ImageView iv_check_photo;
//    private TitleBar titleBar;
    private int activityId;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (photoCounts == null) {
                return 0;
            } else {
                return photoCounts.size();
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            ImageView photoPreview = new ImageView(PhotopreviewActivity.this);
            ((ViewPager) container).addView(photoPreview);
            /*Bitmap bitmap = BitmapFactory.decodeFile(photoCounts.get(position).getPath());
            photoPreview.setImageBitmap(bitmap);*/

            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(
                    photoCounts.get(position).getPath(), photoPreview);
            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopreview);
//        initTitleBar();
        mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
        iv_check_photo = (ImageView) findViewById(R.id.iv_check_photo);

        mViewPager.setOnPageChangeListener(this);
        initCheckData();
        bindData();
        iv_check_photo.setOnClickListener(this);


    }

//    private void initTitleBar() {
//
//        //获取TtileBar
//        titleBar = getTitleBar();
//        titleBar.setTitle("1/" + new PhotoCount().getPhotosPaths().size());
//        titleBar.setTitleColor(getResources().getColor(R.color.black_new_two));
//        //设置左边按钮
//        titleBar.setLeftImageResource(R.mipmap.public_back_btn_down);
//
//        titleBar.setActionTextColor(getResources().getColor(R.color.black_new_two));
//        titleBar.setLeftClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                MyEventBusModel model = new MyEventBusModel();
//                model.REFRESH_PHOTO_LIST = true;
//                EventBus.getDefault().post(model);
//                finish();
//
//            }
//        });
//
//        //设置右边按钮
//        titleBar.addAction(new TitleBar.TextAction("完成") {
//            @Override
//            public void performAction(View view) {
//                int size = new PhotoCount().getPhotosPaths().size();
//                if (size == 0) {
//                    Toast.makeText(PhotopreviewActivity.this, "请选择一张图片", Toast.LENGTH_SHORT)
//                            .show();
//                } else {
//                    MyEventBusModel model = new MyEventBusModel();
//                    model.CLOSE_PHOTO_LIST = true;
//                    model.REFRESH_PHOTO_LIST_ACTIVITYID = activityId;
//                    EventBus.getDefault().post(model);
//                    finish();
//
//                }
//            }
//        });
//    }

    private void initCheckData() {
        // TODO Auto-generated method stub

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> imgUrlses = intent.getStringArrayListExtra("imgUrls");
            activityId = intent.getIntExtra("activityId", 0);
            if (imgUrlses != null && imgUrlses.size() > 0) {
                photoChecks.clear();
                photoChecks.addAll(imgUrlses);
//                titleBar.setTitle("1/" + photoChecks.size());
            }
        }

        for (int i = 0; i < photoChecks.size(); i++) {
            PhotoCheck photoCheck = new PhotoCheck();
            photoCheck.setPath(photoChecks.get(i));
            photoCheck.setCheck(true);
            photoCounts.add(photoCheck);
        }
        iv_check_photo.setSelected(true);
    }

    /**
     * 缁戝畾鏁版嵁锛屾洿鏂扮晫闈�
     */
    protected void bindData() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_check_photo) {
            photoCounts.get(current).setCheck(!photoCounts.get(current).isCheck());
            iv_check_photo.setSelected(photoCounts.get(current).isCheck());

            if (photoCounts.get(current).isCheck()) {
                if (!photoChecks.contains(photoCounts.get(current).getPath())) {
                    photoChecks.add(photoCounts.get(current).getPath());
                }
            } else {
                photoChecks.remove(photoCounts.get(current).getPath());
            }
        }
    }

    protected void updateRadioCheck(int position) {
        // TODO Auto-generated method stub
        boolean check = photoCounts.get(position).isCheck();
        iv_check_photo.setSelected(check);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        current = arg0;
        updatePercent();
        updateRadioCheck(arg0);

    }

    protected void updatePercent() {

//        titleBar.setTitle((current + 1) + "/" + photoCounts.size());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

    }

}
