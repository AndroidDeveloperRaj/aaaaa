package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.activity.circlefriend.TuWenAlbumAty;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.adapter.MyAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.DraftDaoModel;
import com.merrichat.net.model.DraftModel;
import com.merrichat.net.model.MemberDraftModel;
import com.merrichat.net.model.MyMovieModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;
import com.merrichat.net.view.SpaceItemDecorationsOfHisHome;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/12/2.
 * <p>
 * 我的动态
 */

public class MyDynamicsAty extends BaseActivity implements MyAdapter.DianZanOnCheckListener, OnRefreshListener, OnLoadmoreListener, BaseQuickAdapter.OnItemChildClickListener {

    /**
     * 0 影集
     * 1 收藏
     * 2 草稿
     */
    public final static int YINGJI_FLAG = 0;
    public final static int DRAFT_FLAG = 2;
    public final static int activityId = MiscUtil.getActivityId();
    public final static int COLLECTION_FLAG = 1;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.sg_tablayout)
    SegmentTabLayout sgTablayout;
    @BindView(R.id.dong_tai_recyclerview)
    RecyclerView dongTaiRecyclerview;
    @BindView(R.id.shou_cang_recyclerview)
    RecyclerView shouCangRecyclerview;
    @BindView(R.id.cao_gao_recyclerview)
    RecyclerView caoGaoRecyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    private String[] mTitles = {"动态", "收藏", "草稿"};
    private ArrayList<Object> list;
    private MyAdapter myYingJiAdapter;
    private int SPAN_NUM = 2;
    private ArrayList<MyMovieModel.DataBean.MovieListBean> movieList = new ArrayList<>();
    //    private ArrayList<MyMovieModel.DataBean.MovieListBean> collectList = new ArrayList<>();
    private OSSClient oss;
    private String imgUrl;
    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 0x123) {
                RxToast.showToast(imgUrl);
            }
        }
    };
    private CheckBox checkCollect;
    private int REC_TPYE = 0;
    private int pageSize = 15;
    private int currentPage = 1;
    private MyAdapter myCaoGaoAdapter;
    private int refreshOrLoadMore = -1;//5 刷新，6 加载更多
    private MyAdapter myCollectAdapter;
    private List<MyMovieModel.DataBean.MovieListBean> movieListBeans = new ArrayList<>();
    private List<MyMovieModel.DataBean.MovieListBean> collectListBeans = new ArrayList<>();
    private ArrayList<DraftModel> draftModelList = new ArrayList<>();
    private int activityIdFrom = -1;
    private int SPACE_NUM = 40;//item间距
    private int clickPosition = -1;//记录点击item的位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamics);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //使用自己的获取STSToken的类
        initView();
        initData();

    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.FRESH_LIST_DATA) {
            MyMovieModel.DataBean.MovieListBean listBean = movieList.get(clickPosition);
            if (!RxDataTool.isNullString(myEventBusModel.income)) {
                listBean.setRMBSign(myEventBusModel.income);
                listBean.setLikeCounts(myEventBusModel.likeCounts);
                listBean.setIsLike(myEventBusModel.isLike ? 1 : 0);
//                    movieList.remove(clickPosition);
//                    movieList.add(clickPosition, listBean);
                movieList.set(clickPosition, listBean);
                myYingJiAdapter.notifyItemChanged(clickPosition);
            } else {//删除和取消收藏帖子
                myYingJiAdapter.remove(clickPosition);
            }
        }
        if (myEventBusModel.FRESH_MY_COLLECTION) {//收藏帖子
            movieList.clear();
            myMovieInfo(COLLECTION_FLAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        activityIdFrom = getIntent().getIntExtra("activityId", -1);
        if (activityIdFrom == ReleaseGraphicAlbumAty.activityId) {
            REC_TPYE = DRAFT_FLAG;
            sgTablayout.setCurrentTab(2);
        }
        if (REC_TPYE != DRAFT_FLAG) {
            dongTaiRecyclerview.setVisibility(View.VISIBLE);
            myMovieInfo(REC_TPYE);
        } else if (REC_TPYE == DRAFT_FLAG) {
            setRecyclerViewVisible(caoGaoRecyclerview, DRAFT_FLAG);
        }
    }

    private void initView() {
        tvTitleText.setText("我的动态");
        list = new ArrayList<>();
//        setRecyclerViewVisible(dongTaiRecyclerview, YINGJI_FLAG);
        SpaceItemDecorationsOfHisHome spaceItemDecoration = new SpaceItemDecorationsOfHisHome(SPACE_NUM, 3);
        dongTaiRecyclerview.setLayoutManager(new GridLayoutManager(cnt, 3));
//        dongTaiRecyclerview.setPadding(5, 5, 5, 5);
        myYingJiAdapter = new MyAdapter(MyDynamicsAty.this, YINGJI_FLAG, R.layout.item_mine_yingji, movieList);
        dongTaiRecyclerview.setAdapter(myYingJiAdapter);
        dongTaiRecyclerview.addItemDecoration(spaceItemDecoration);
        myYingJiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                clickPosition = position;
                Bundle bundle = new Bundle();
                bundle.putString("toMemberId", movieList.get(position).getMemberId() + "");
                bundle.putString("contentId", movieList.get(position).getId() + "");
                bundle.putInt("activityId", activityId);
                bundle.putInt("tab_item", 5);
                if (movieList.get(position).getFlag() == 1) {//1照片 2视频
                    RxActivityTool.skipActivity(MyDynamicsAty.this, TuWenAlbumAty.class, bundle);
                } else {
                    RxActivityTool.skipActivity(MyDynamicsAty.this, CircleVideoActivity.class, bundle);
                }
            }
        });
        myYingJiAdapter.setDianZanOnCheckListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        sgTablayout.setTabData(mTitles);
        sgTablayout.setOnTabSelectListener(new OnTabSelectListener() {//Tab 监听
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        currentPage = 1;
                        REC_TPYE = YINGJI_FLAG;
                        setRecyclerViewVisible(dongTaiRecyclerview, REC_TPYE);
                        break;
                    case 1:
                        currentPage = 1;
                        REC_TPYE = COLLECTION_FLAG;
                        setRecyclerViewVisible(dongTaiRecyclerview, REC_TPYE);
                        break;
                    case 2:
                        currentPage = 1;
                        REC_TPYE = DRAFT_FLAG;
                        setRecyclerViewVisible(caoGaoRecyclerview, REC_TPYE);

                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void setRecyclerViewVisible(RecyclerView recyclerView, int rec_type) {
        dongTaiRecyclerview.setVisibility(View.GONE);
        shouCangRecyclerview.setVisibility(View.GONE);
        caoGaoRecyclerview.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        initRecyclerViewAndTab(rec_type);


    }

    /**
     * 查询我的动态
     *
     * @param status 0 动态 1 收藏
     */
    private void myMovieInfo(final int status) {
        OkGo.<String>get(Urls.MY_MOVIEINFO)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("status", status)
                .params("pageSize", pageSize)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (refreshOrLoadMore == 5) {
                            movieList.clear();
//                            if (status == YINGJI_FLAG) movieList.clear();
//                            if (status == COLLECTION_FLAG) collectList.clear();
                            refreshLayout.finishRefresh();
                        } else if (refreshOrLoadMore == 6) {
                            refreshLayout.finishLoadmore();
                        }
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    Gson gson = new Gson();
                                    MyMovieModel myMoveModel = gson.fromJson(response.body(), MyMovieModel.class);
                                    if (status == YINGJI_FLAG) {
                                        movieListBeans = myMoveModel.getData().getMovieList();
                                        if (movieListBeans != null) {
                                            movieList.addAll(movieListBeans);
                                            myYingJiAdapter.setType(YINGJI_FLAG);
                                        }
                                    } else if (status == COLLECTION_FLAG) {
                                        collectListBeans = myMoveModel.getData().getCollectList();
                                        if (collectListBeans != null) {
                                            movieList.addAll(collectListBeans);
                                            myYingJiAdapter.setType(COLLECTION_FLAG);
                                        }
                                    }
                                    if (movieList.size() > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        RxToast.showToast("暂无数据哦~");
                                    }
                                    myYingJiAdapter.notifyDataSetChanged();

                                } else {
                                    if (refreshOrLoadMore == 5) {
                                        refreshLayout.finishRefresh();
                                    } else if (refreshOrLoadMore == 6) {
                                        refreshLayout.finishLoadmore();
                                    }
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    RxToast.showToast("请求失败，请重试！");
                                }
                            } catch (JSONException e) {
                                if (refreshOrLoadMore == 5) {
                                    refreshLayout.finishRefresh();
                                } else if (refreshOrLoadMore == 6) {
                                    refreshLayout.finishLoadmore();
                                }
                                tvEmpty.setVisibility(View.VISIBLE);

                                e.printStackTrace();

                            }


                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (refreshOrLoadMore == 5) {
                            refreshLayout.finishRefresh();
                        } else if (refreshOrLoadMore == 6) {
                            refreshLayout.finishLoadmore();
                        }
                        tvEmpty.setVisibility(View.VISIBLE);

                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    private void initRecyclerViewAndTab(int flag) {
        if (flag != DRAFT_FLAG) {
            refreshLayout.setEnableLoadmore(true);
            refreshLayout.setEnableRefresh(true);
            movieList.clear();
            myMovieInfo(flag);
           /* if (flag == YINGJI_FLAG) {

                if (movieListBeans != null && movieListBeans.size() > 0) {
                    tvEmpty.setVisibility(View.GONE);
                    movieList.addAll(movieListBeans);
                    myYingJiAdapter.setType(YINGJI_FLAG);
                    myYingJiAdapter.notifyDataSetChanged();
                } else {
                    myMovieInfo(flag);
                }
            } else if (flag == COLLECTION_FLAG) {

                if (movieListBeans != null && collectListBeans.size() > 0) {
                    tvEmpty.setVisibility(View.GONE);
                    movieList.addAll(collectListBeans);
                    myYingJiAdapter.setType(COLLECTION_FLAG);
                    myYingJiAdapter.notifyDataSetChanged();
                } else {
                    myMovieInfo(flag);
                }
            }*/
        } else if (flag == DRAFT_FLAG) {
            refreshLayout.setEnableLoadmore(false);
            refreshLayout.setEnableRefresh(false);
            DraftDaoModel draftDaoModel = DraftDaoModel.getDraftDaoModel();
            String tuWenDraftJson = draftDaoModel.getTuWenDraft();
            ArrayList<MemberDraftModel> memberDraftModels = (ArrayList<MemberDraftModel>) JSON.parseArray(tuWenDraftJson, MemberDraftModel.class);
            if (memberDraftModels != null && memberDraftModels.size() > 0) {
                for (MemberDraftModel memberDraftModel :
                        memberDraftModels) {
                    if (memberDraftModel.getMemberId().equals(UserModel.getUserModel().getMemberId())) {
                        draftModelList = memberDraftModel.getMemberDraftList();
                    }
                }
            }
            if (draftModelList == null) {
                draftModelList = new ArrayList<>();
            }
            caoGaoRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            myCaoGaoAdapter = new MyAdapter(MyDynamicsAty.this, DRAFT_FLAG, R.layout.item_draft, draftModelList);
            caoGaoRecyclerview.setAdapter(myCaoGaoAdapter);
            myCaoGaoAdapter.setOnItemChildClickListener(this);
        }
    }

    /**
     * 点赞、取消点赞
     *
     * @param isChecked
     * @param flag
     * @param position
     */
    private void updateLikes(final boolean isChecked, final int flag, final int position) {
        long id = 0;
        long memberId = 0;
        id = movieList.get(position).getId();
        memberId = movieList.get(position).getMemberId();
//        if (REC_TPYE == YINGJI_FLAG) {
//            id = movieListBeans.get(position).getId();
//            memberId = movieListBeans.get(position).getMemberId();
//
//        } else if (REC_TPYE == COLLECTION_FLAG) {
//            id = collectListBeans.get(position).getId();
//            memberId = collectListBeans.get(position).getMemberId();
//        }
        OkGo.<String>get(Urls.UPDATE_LIKES)//
                .tag(this)//
                .params("logId", id)
                .params("personUrl", UserModel.getUserModel().getImgUrl())
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", memberId)
                .params("flag", flag)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data.optBoolean("result")) {
                                    int count = data.optInt("count");
                                    if (flag == 0) {
                                        RxToast.showToast("已点赞！");
                                        movieList.get(position).setIsLike(1);

                                    } else {
                                        RxToast.showToast("已取消点赞！");
                                        movieList.get(position).setIsLike(0);
                                    }
                                    movieList.get(position).setLikeCounts(count);
                                    myYingJiAdapter.notifyItemChanged(position);
                                } else {
                                    RxToast.showToast(data.optString("msg"));
                                }

                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });

    }

    private void getAliyunUrl(final String videoName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    imgUrl = oss.presignConstrainedObjectURL(Config.bucket, videoName, 30 * 60);
                    handler.sendEmptyMessage(0x123);
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @OnClick({R.id.iv_back, R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_empty:
                switch (REC_TPYE) {
                    case YINGJI_FLAG:
                        myMovieInfo(YINGJI_FLAG);
                        break;
                    case COLLECTION_FLAG:
                        myMovieInfo(COLLECTION_FLAG);
                        break;

                }
                break;
        }
    }

    @Override
    public void dianZanOnCheckListener(boolean isChecked, int position) {
        Logger.e("isChecked", isChecked + "");
        if (!isChecked) {//是否点赞 0点赞，1为取消点赞
            updateLikes(isChecked, 0, position);
        } else {
            updateLikes(isChecked, 1, position);
        }

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshOrLoadMore = 5;
        currentPage = 1;
        switch (REC_TPYE) {
            case YINGJI_FLAG:
                myMovieInfo(YINGJI_FLAG);
                break;
            case COLLECTION_FLAG:
                myMovieInfo(COLLECTION_FLAG);
                break;

        }

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        currentPage++;
        refreshOrLoadMore = 6;
        switch (REC_TPYE) {
            case YINGJI_FLAG:
                myMovieInfo(YINGJI_FLAG);
                break;
            case COLLECTION_FLAG:
                myMovieInfo(COLLECTION_FLAG);
                break;

        }
    }

    /**
     * 收藏 childView onClick
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_draft_share://分享
                Bundle bundle = new Bundle();
                bundle.putSerializable("draftContent", draftModelList.get(position));
                bundle.putInt("activityId", activityId);
                bundle.putInt("editDraftOfPosition", position);
                RxActivityTool.skipActivity(MyDynamicsAty.this, ReleaseGraphicAlbumAty.class, bundle);
                break;
            case R.id.tv_continue_edit: //继续编辑
                break;
        }
    }
}
