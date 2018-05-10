package com.merrichat.net.activity.message.camera;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.message.storage.FolderBean;
import com.merrichat.net.activity.message.storage.ImageAdapter;
import com.merrichat.net.activity.message.storage.PhotoCount;
import com.merrichat.net.activity.message.util.ListImageDirPoppupWindow;
import com.merrichat.net.app.MyEventBusModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 显示相册所有的照片和选择了某个文件夹内包含的照片
 *
 * @author amssy
 */
public class PhotoListActivity extends BaseActivity implements ListImageDirPoppupWindow.OnDirSelectedListener {

    /**
     * 进度条
     */
    private ProgressDialog mProgressDialog;

    @BindView(R.id.id_gridView)
    GridView mGridView;
    @BindView(R.id.id_bottom_ly)
    RelativeLayout mBottomLy;
    @BindView(R.id.id_choose_dir)
    TextView mDirName;/*
    @Bind(R.id.id_total_count)
	private TextView mDirCount;*/
    @BindView(R.id.txt_preview)
    TextView txt_preview;

    private List<String> mImgs;

    private File mCurrentDir;
    private int mMaxCOunt;

    private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();

    private ImageAdapter mImgAdapter;


    private ListImageDirPoppupWindow mDirPopupWindow;


    private BroadcastReceiver mReceiver;
//    private TitleBar titleBar;
    private int activityId;
    //可选的照片的数量默认4张
    private int photoCountNum = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        ButterKnife.bind(this);
//        initTitleBar();
        initDatas();
        initEvent();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_PHOTO_LIST) {//刷新数据
            finish();
        } else if (myEventBusModel.REFRESH_PHOTO_LIST) {
            if (mImgAdapter != null) {
                mImgAdapter.notifyDataSetChanged();
            }
        }
    }

//    private void initTitleBar() {
//        //获取TtileBar
//        titleBar = getTitleBar();
//        titleBar.setTitle("图片");
//        titleBar.setTitleColor(getResources().getColor(R.color.black_new_two));
//        //设置左边按钮
//        titleBar.setLeftImageResource(R.mipmap.public_back_btn_down);
//
//        titleBar.setActionTextColor(getResources().getColor(R.color.black_new_two));
//        titleBar.setLeftClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new PhotoCount().getPhotosPaths().clear();
//                finish();
//            }
//        });
//
//        //设置右边按钮
//        titleBar.addAction(new TitleBar.TextAction("完成") {
//            @Override
//            public void performAction(View view) {
//                int size = new PhotoCount().getPhotosPaths().size();
//                if (size == 0) {
//                    Toast.makeText(PhotoListActivity.this, "请选择一张图片", Toast.LENGTH_SHORT)
//                            .show();
//                } else {
//                    MyEventBusModel model = new MyEventBusModel();
//                    model.PHOTO_LIST_CLICK_COMPLETE = true;
//                    model.PHOTO_LIST_CLICK_COMPLETE_ACTIVITYID = activityId;
//                    EventBus.getDefault().post(model);
//                    finish();
//                }
//            }
//        });
//    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 绑定数据到view中  为gridview设置数据
            data2View();
            //
            initListDirPopupWindw();
        }
    };

    /**
     * 绑定数据到view中  为gridview设置数据
     */
    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(getApplicationContext(), "未扫描到任何图片",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //gridview的shujhuji
        mImgs = Arrays.asList(mCurrentDir.list());
        Collections.reverse(mImgs);
        mImgAdapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath(), photoCountNum);
        mGridView.setAdapter(mImgAdapter);

		/*mDirCount.setText(mMaxCOunt + "张");*/
        mDirName.setText(mCurrentDir.getName());

    }

    protected void initListDirPopupWindw() {
        // TODO Auto-generated method stub
        mDirPopupWindow = new ListImageDirPoppupWindow(this, mFolderBeans);
        mDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {

                lightOn();
            }
        });

        mDirPopupWindow.setmListener(this);
    }

    /**
     * 内容区域变亮
     */
    protected void lightOn() {
        // TODO Auto-generated method stub
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    protected void lightOff() {
        // TODO Auto-generated method stub
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    private void initEvent() {
        // TODO Auto-generated method stub


        mDirName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                mDirPopupWindow.showAsDropDown(mDirName, 0, 0);
                mDirPopupWindow.setmCurrentDir(mCurrentDir.getName());
                lightOff();
            }
        });


        txt_preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (new PhotoCount().getPhotosPaths().size() > 0) {
                    Intent intent = new Intent(PhotoListActivity.this, PhotopreviewActivity.class);
                    intent.putExtra("activityId", activityId);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * @param @param  context
     * @param @param  dpValue
     * @param @return 设定文件
     * @return int 返回类型
     * @throws
     * @Title: dip2px
     * @Description: TODO 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initDatas() {
        // TODO Auto-generated method stub


        Intent intent = getIntent();
        if (intent != null) {
            activityId = intent.getIntExtra("activityId", 0);
            photoCountNum = intent.getIntExtra("photoCountNum", 4);
        }
        //外部存储卡
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        // 进度条对话框
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        //开启线程扫描手机图片
        new Thread(new Runnable() {
            @Override
            public void run() {


                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = PhotoListActivity.this
                        .getContentResolver();

                // 游标
                Cursor mCursor = cr.query(mImgUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Log.e("TAG", mCursor.getCount() + "");

                Set<String> mDirPaths = new HashSet<String>();
                while (mCursor.moveToNext()) {
                    //每一张图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    Log.e("TAG", path);

                    // 文件
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    //得到parentfile的path
                    String dirPath = parentFile.getAbsolutePath();
                    //包含图片的文件夹
                    FolderBean floderBean = null;
                    // 防止重复扫描
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化文件夹
                        floderBean = new FolderBean();
                        //设置文件夹路径
                        floderBean.setDir(dirPath);
                        floderBean.setFirstImagePath(path);
                    }

                    if (parentFile.list() == null) {
                        continue;
                    }
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    //totalCount += picSize;

                    floderBean.setCount(picSize);
                    mFolderBeans.add(floderBean);

                    if (picSize > mMaxCOunt) {
                        //当前activity底部文件夹的名称和数量
                        mMaxCOunt = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                mCursor.close();

                //扫描完成释放零时变量的内存
                mDirPaths = null;

                //利用handler 扫描图片完成通知ui线程
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    /**
     * 当popupwindow的文件夹被选中
     */
    @Override
    public void onSelected(FolderBean folderBean) {
        // TODO Auto-generated method stub
        mCurrentDir = new File(folderBean.getDir());
        mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                // TODO Auto-generated method stub
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".jpeg")
                        || filename.endsWith(".png")
                        ) {
                    return true;
                }
                return false;
            }
        }));
        Collections.reverse(mImgs);
        mDirPopupWindow.dismiss();
        mImgAdapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath(), photoCountNum);
        mGridView.setAdapter(mImgAdapter);

		/*mDirCount.setText(mMaxCOunt + "张");*/
        mDirName.setText(mCurrentDir.getName());
    }
}