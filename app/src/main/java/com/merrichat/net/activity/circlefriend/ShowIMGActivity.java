package com.merrichat.net.activity.circlefriend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.interfaces.GlideImageDownLoadCallBack;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.service.GlideDownLoadImageService;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CommomDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

/**
 * 图文详情查看大图
 * Created by amssy on 18/4/11.
 */

public class ShowIMGActivity extends Activity {

    /**
     * 单线程列队执行
     */
    private static ExecutorService singleExecutor = null;
    @BindView(R.id.viewPager_show_img)
    ViewPager viewPagerShowImg;
    private List<PhotoVideoModel> list = new ArrayList<>();//影集内容集合
    private PhotoView[] simpleDraweeViews;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 255);
        setContentView(R.layout.activity_show_img);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<PhotoVideoModel> list1 = (ArrayList<PhotoVideoModel>) intent.getSerializableExtra("list");
            position = intent.getIntExtra("position", 0);
            list.addAll(list1);
        }
        simpleDraweeViews = new PhotoView[list.size()];
        viewPagerShowImg.setAdapter(new MyPagerAdpter());
        viewPagerShowImg.setCurrentItem(position);
    }

    /**
     * 启动图片下载线程
     */
    private void onDownLoad(String imgUrl) {
        GlideDownLoadImageService service = new GlideDownLoadImageService(this, imgUrl,
                new GlideImageDownLoadCallBack() {
                    @Override
                    public void onDownLoadSuccess(File file) {
                        // 在这里执行图片保存方法
                        // 其次把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(ShowIMGActivity.this.getContentResolver(), file.getAbsolutePath(), file.getName() + "", null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // 最后通知图库更新
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        RxToast.showToast("图片保存成功");
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败

                        RxToast.showToast("图片保存失败");

                    }
                });
        //启动图片下载线程
        runOnQueue(service);
    }

    /**
     * 执行单线程列队执行
     */
    public void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    /**
     * 返回方法
     */
    private void Finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Finish();
        }
        return false;
    }

    private class MyPagerAdpter extends PagerAdapter {

        private CommomDialog dialog;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(simpleDraweeViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(ShowIMGActivity.this).inflate(R.layout.simple_drawee_view, null);
            final PhotoView scaleView = (PhotoView) view.findViewById(R.id.simple_detail);
            final RelativeLayout relGroup = (RelativeLayout) view.findViewById(R.id.rel_group);

            final String imgUrl = list.get(position).getUrl();
            Glide.with(ShowIMGActivity.this).load(imgUrl).into(scaleView);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scaleView.getLayoutParams();
            params.height = (int) (list.get(position).getHeight() / ((float) list.get(position).getWidth() / StringUtil.getWidths(ShowIMGActivity.this)));
            ;
            scaleView.setLayoutParams(params);

            relGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Finish();
                }
            });
            scaleView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //弹出提示框

                        dialog = new CommomDialog(ShowIMGActivity.this, R.style.dialog, "是否保存图片到图库？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();
                                    onDownLoad(imgUrl);

                                }
                            }
                        }).setTitle("保存图片");
                        dialog.show();

                    return true;
                }
            });
            simpleDraweeViews[position] = scaleView;
            container.addView(relGroup);
            return relGroup;
        }
    }

}
