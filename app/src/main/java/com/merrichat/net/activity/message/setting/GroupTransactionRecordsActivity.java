package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.GroupTransactionRecordsAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.TradeItemOfMonthModel;
import com.merrichat.net.model.TransactionHistoryModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by amssy on 18/1/19.
 * 群主钱包交易记录
 */

public class GroupTransactionRecordsActivity extends MerriActionBarActivity implements GroupTransactionRecordsAdapter.OnMyItemClickListener, OnLoadmoreListener, OnRefreshListener {
    @BindView(R.id.tv_group_cash)
    TextView tvGroupCash;
    @BindView(R.id.tv_group_gold)
    TextView tvGroupGold;


    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.stickyListHeadersListView)
    StickyListHeadersListView stickyListHeadersListView;


    /**
     * 加载的页数: 默认为第一页
     */
    private int CURRENT_NUM = 1;
    /**
     * 当前返回的条目数
     */
    private int CURRENTPAGR = 0;
    /**
     * 每一页数的条数
     */
    private int currentPageNum = 20;


    private List<TradeItemOfMonthModel> transactionList;
    private GroupTransactionRecordsAdapter adapter;

    /**
     * 选中条目
     * 0 现金
     * 1 讯美币
     * 默认为0
     */
    private int checkedMoney = 0;


    /**
     * 账户余额
     */
    private String cashBalance = "";

    /**
     * 讯美币余额
     */
    private String giftBalance = "";


    /**
     * 群钱包id
     */
    private String communityAccountId;

    private String giveMonth = "";


    private int REFRESHORLOADMORE = 1;
    private TimePickerView pvTime;

    /**
     * 没有数据时候的布局
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    @BindView(R.id.lin_no_data)
    LinearLayout linNoData;
    @BindView(R.id.tv_year_month)
    TextView tvYearMonth;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.iv_tag)
    ImageView ivTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transaction_records);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        setTitle("群主钱包交易记录");
        setLeftBack();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            cashBalance = intent.getStringExtra("cashBalance");
            giftBalance = intent.getStringExtra("giftBalance");
            communityAccountId = intent.getStringExtra("communityAccountId");
        }

        giveMonth = getDateToString();

        if (checkedMoney == 0) {//余额
            tvBalance.setText("现金余额: ￥" + cashBalance);
        } else if (checkedMoney == 1) {//金币
            tvBalance.setText("讯美币余额: " + giftBalance);
        }
        tvYearMonth.setText("" + giveMonth);

        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);

        transactionList = new ArrayList<TradeItemOfMonthModel>();
        adapter = new GroupTransactionRecordsAdapter(GroupTransactionRecordsActivity.this, transactionList, checkedMoney, cashBalance, giftBalance);
        stickyListHeadersListView.setAdapter(adapter);
        adapter.setOnMyItemClickListener(GroupTransactionRecordsActivity.this);
        queryTradebytypePage(false);
    }


    /**
     * 查询钱包交易记录
     * accountType   账号类型 0：按照余额查询 1：按照金币查询 2.按照通讯费查询
     */
    private void queryTradebytypePage(final boolean isRefresh) {
        OkGo.<String>post(Urls.queryTradebytypePage)
                .params("page", CURRENT_NUM + "")
                .params("rows", currentPageNum + "")
                .params("accountId", communityAccountId)
                .params("month", giveMonth)
                .params("platformId", "100005")
                .params("outerTradeType", "")
                .params("accountType", checkedMoney)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            if (refreshLayout != null) {
                                if (REFRESHORLOADMORE == 1) {
                                    transactionList.clear();
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishLoadmore();
                                }
                            }

                            JSONObject jsonObject = new JSONObject(response.body());
                            CURRENTPAGR = 0;
                            if (jsonObject.optBoolean("success")) {

                                JSONObject data = jsonObject.optJSONObject("data");
                                JSONArray jsonArray = data.optJSONArray("monthKey");
                                JSONObject itemsKey = data.optJSONObject("itemsKey");
                                Gson gson = new Gson();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    TransactionHistoryModel model = new TransactionHistoryModel();
                                    model.setTrademonth(jsonArray.get(i).toString());
                                    if (!itemsKey.isNull(model.getTrademonth())) {
                                        JSONArray jsonArray2 = itemsKey.optJSONArray(model.getTrademonth());
                                        for (int j = 0; j < jsonArray2.length(); j++) {
                                            TradeItemOfMonthModel tradeItemOfMonthModel = gson.fromJson(jsonArray2.get(j).toString(), TradeItemOfMonthModel.class);
                                            String titleId = model.getTrademonth().replace("-", "") + i;
                                            tradeItemOfMonthModel.setTitleId(Integer.parseInt(titleId));
                                            tradeItemOfMonthModel.setMonthTitle(model.getTrademonth());
                                            transactionList.add(tradeItemOfMonthModel);
                                        }
                                    }
                                }

                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
                                }
                            }


                            CURRENTPAGR += transactionList.size();
                            //setScrollDisAbled();
                            if (null != transactionList && !transactionList.isEmpty()) {
                                if (tvEmpty != null) {
                                    tvEmpty.setVisibility(View.GONE);
                                    linNoData.setVisibility(View.GONE);
                                }
                                adapter.notifyChange(checkedMoney);
                                //refreshLayout.setVisibility(View.VISIBLE);
                            } else {
                                // 当前标签为刷新数据请求时，显示
                                if (tvEmpty != null) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    linNoData.setVisibility(View.VISIBLE);
                                }
                                //refreshLayout.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            if (refreshLayout != null) {
                                if (REFRESHORLOADMORE == 1) {
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishLoadmore();
                                }
                            }
                        }
                    }
                });
    }


    @OnClick({R.id.tv_group_cash, R.id.tv_group_gold,R.id.iv_tag})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_group_cash://现金
                checkedMoney = 0;
                tvGroupCash.setBackgroundResource(R.drawable.shape_wallet_left_checked);
                tvGroupCash.setTextColor(cnt.getResources().getColor(R.color.white));
                tvGroupGold.setBackgroundResource(R.drawable.shape_wallet_right);
                tvGroupGold.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));
                transactionList.clear();
                tvBalance.setText("现金余额: ￥" + cashBalance);
                giveMonth = getDateToString();
                tvYearMonth.setText("" + giveMonth);
                queryTradebytypePage(false);
                break;
            case R.id.tv_group_gold://金币
                checkedMoney = 1;
                tvBalance.setText("讯美币余额: " + giftBalance);
                giveMonth = getDateToString();
                tvYearMonth.setText("" + giveMonth);
                tvGroupCash.setBackgroundResource(R.drawable.shape_wallet_left);
                tvGroupCash.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));
                tvGroupGold.setBackgroundResource(R.drawable.shape_wallet_right_checked);
                tvGroupGold.setTextColor(cnt.getResources().getColor(R.color.white));
                transactionList.clear();
                queryTradebytypePage(false);
                break;
            case R.id.iv_tag:
                initTimePicker();
                pvTime.show();
                break;
        }
    }


    /**
     * 选择日期
     *
     * @param position
     */
    @Override
    public void onMyItemTitleClick(int position) {
        initTimePicker();
        pvTime.show();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        CURRENT_NUM++;
        queryTradebytypePage(false);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        giveMonth = getDateToString();
        tvYearMonth.setText("" + giveMonth);

        REFRESHORLOADMORE = 1;
        CURRENT_NUM = 1;
        transactionList.clear();
        queryTradebytypePage(false);
    }


    /**
     * 时间选择器
     */
    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
//        String[] splitDate1 = getDateToString1(Long.parseLong(UserModel.getUserModel().getRegistTime())).split("-");
//        String year1 = splitDate1[0];
//        String month1 = splitDate1[1];
//        String day1 = splitDate1[2];
        startDate.set(2018, 2, 1);
        Calendar endDate = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        String date = RxTimeTool.getDate(currentTimeMillis + "", "yyyy-MM-dd");
        if (date != null) {
            String[] splitDate = date.split("-");
            String year = splitDate[0];
            String month = splitDate[1];
            String day = splitDate[2];
            endDate.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        }
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                giveMonth = getTime(date);
                tvYearMonth.setText("" + giveMonth);

                transactionList.clear();
                REFRESHORLOADMORE = 1;
                CURRENT_NUM = 1;
                queryTradebytypePage(false);
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }


    /*时间戳转换成字符窜*/
    private String getDateToString() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        giveMonth = year + "-" + month;
        return giveMonth;
    }
}
