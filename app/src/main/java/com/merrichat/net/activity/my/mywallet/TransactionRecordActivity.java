package com.merrichat.net.activity.my.mywallet;

import android.content.Intent;
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
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.MyStickyAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.TradeItemOfMonthModel;
import com.merrichat.net.model.TransactionHistoryModel;
import com.merrichat.net.model.UserModel;
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
 * Created by amssy on 17/7/3.
 * 钱包交易记录
 */
public class TransactionRecordActivity extends BaseActivity implements MyStickyAdapter.OnMyItemClickListener, OnLoadmoreListener, OnRefreshListener {

    /**
     * 返回按钮
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;

    /**
     * 标题文本
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;

    /**
     * 讯美币
     */
    @BindView(R.id.tv_money_left)
    TextView tvMoneyLeft;

    /**
     * 现金
     */
    @BindView(R.id.tv_money_in)
    TextView tvMoneyIn;

    /**
     * 通话费
     */
    @BindView(R.id.tv_money_right)
    TextView tvMoneyRight;

    @BindView(R.id.stickyListHeadersListView)
    StickyListHeadersListView stickyListHeadersListView;
    /**
     * 有数据时候的布局
     */
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.lin_no_data)
    LinearLayout linNoData;
    @BindView(R.id.tv_year_month)
    TextView tvYearMonth;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.iv_tag)
    ImageView ivTag;


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


    /**
     * 选中条目
     * 0 余额
     * 1 金币
     * 2 通话费
     * 默认为1
     */
    private int checkedMoney = 1;


    /**
     * 账户余额
     */
    private String cashBalance = "";

    /**
     * 讯美币余额
     */
    private String giftBalance = "";

    /**
     * 通讯余额
     */
    private String couponBalance = "";

    private String giveMonth = "";

    private MyStickyAdapter adapter;
    private List<TradeItemOfMonthModel> list;
    private int REFRESHORLOADMORE = 1;
    private TimePickerView pvTime;

    /**
     * 没有数据时候的布局
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);
        ButterKnife.bind(this);
        initTitleBar();
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            cashBalance = intent.getStringExtra("cashBalance");
            giftBalance = intent.getStringExtra("giftBalance");
            couponBalance = intent.getStringExtra("couponBalance");
        }
        checkedMoney = 0;
        CURRENT_NUM = 1;
        if (checkedMoney == 0) {//余额
            tvBalance.setText("现金余额: ￥" + cashBalance);
        } else if (checkedMoney == 1) {//金币
            tvBalance.setText("讯美币余额: " + giftBalance);
        }

        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);

        giveMonth = getDateToString();
        tvYearMonth.setText("" + giveMonth);

        list = new ArrayList<>();
        adapter = new MyStickyAdapter(TransactionRecordActivity.this, list, cashBalance, giftBalance, couponBalance);
        stickyListHeadersListView.setAdapter(adapter);
        adapter.setOnMyItemClickListener(TransactionRecordActivity.this);

        queryTradebytypePage(false);
    }

    private void initTitleBar() {
        tvTitleText.setText("钱包交易记录");
    }


    /**
     * 查询钱包交易记录
     * accountType   账号类型 0：按照余额查询 1：按照金币查询 2.按照通讯费查询
     */
    private void queryTradebytypePage(final boolean isRefresh) {
        OkGo.<String>post(Urls.queryTradebytypePage)
                .params("page", CURRENT_NUM + "")
                .params("rows", currentPageNum + "")
                .params("accountId", UserModel.getUserModel().getAccountId())
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
                                    list.clear();
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
                                            list.add(tradeItemOfMonthModel);
                                        }
                                    }
                                }

                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
                                }
                            }


                            CURRENTPAGR += list.size();
                            //setScrollDisAbled();
                            if (null != list && !list.isEmpty()) {
                                if (tvEmpty != null) {
                                    tvEmpty.setVisibility(View.GONE);
                                    linNoData.setVisibility(View.GONE);
                                }
                                adapter.setCheckedMoney(checkedMoney);
                                adapter.notifyDataSetChanged();
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

    @OnClick({R.id.iv_back, R.id.tv_money_left, R.id.tv_money_in, R.id.tv_money_right, R.id.tv_empty, R.id.iv_tag})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_money_left://讯美币
                if (checkedMoney == 1) {
                    return;
                }
                CURRENT_NUM = 1;
                checkedMoney = 1;

                if (checkedMoney == 0) {//余额
                    tvBalance.setText("现金余额: ￥" + cashBalance);
                } else if (checkedMoney == 1) {//金币
                    tvBalance.setText("讯美币余额: " + giftBalance);
                }

                tvMoneyLeft.setBackgroundResource(R.drawable.shape_wallet_right_checked);
                tvMoneyLeft.setTextColor(cnt.getResources().getColor(R.color.white));

                //tvMoneyIn.setBackgroundResource(R.drawable.shape_wallet_in);
                tvMoneyIn.setBackgroundResource(R.drawable.shape_wallet_left);
                tvMoneyIn.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));

                tvMoneyRight.setBackgroundResource(R.drawable.shape_wallet_right);
                tvMoneyRight.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));

                list.clear();
                giveMonth = getDateToString();
                tvYearMonth.setText("" + giveMonth);
                queryTradebytypePage(false);
                break;
            case R.id.tv_money_in://现金
                if (checkedMoney == 0) {
                    return;
                }
                checkedMoney = 0;
                CURRENT_NUM = 1;

                if (checkedMoney == 0) {//余额
                    tvBalance.setText("现金余额: ￥" + cashBalance);
                } else if (checkedMoney == 1) {//金币
                    tvBalance.setText("讯美币余额: " + giftBalance);
                }

                tvMoneyLeft.setBackgroundResource(R.drawable.shape_wallet_right);
                tvMoneyLeft.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));

                //tvMoneyIn.setBackgroundResource(R.drawable.shape_wallet_in_checked);
                tvMoneyIn.setBackgroundResource(R.drawable.shape_wallet_left_checked);
                tvMoneyIn.setTextColor(cnt.getResources().getColor(R.color.white));

                tvMoneyRight.setBackgroundResource(R.drawable.shape_wallet_right);
                tvMoneyRight.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));

                list.clear();
                giveMonth = getDateToString();
                tvYearMonth.setText("" + giveMonth);
                queryTradebytypePage(false);

                break;
            case R.id.tv_money_right://通话费
                if (checkedMoney == 2) {
                    return;
                }
                checkedMoney = 2;
                CURRENT_NUM = 1;
                tvMoneyLeft.setBackgroundResource(R.drawable.shape_wallet_left);
                tvMoneyLeft.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));

                tvMoneyIn.setBackgroundResource(R.drawable.shape_wallet_in);
                tvMoneyIn.setTextColor(cnt.getResources().getColor(R.color.tex_6D6D6D));

                tvMoneyRight.setBackgroundResource(R.drawable.shape_wallet_right_checked);
                tvMoneyRight.setTextColor(cnt.getResources().getColor(R.color.white));

                list.clear();
                giveMonth = getDateToString();
                queryTradebytypePage(false);
                break;
            case R.id.tv_empty:

                break;
            case R.id.iv_tag:
                initTimePicker();
                pvTime.show();
                break;
        }
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

                list.clear();
                REFRESHORLOADMORE = 1;
                CURRENT_NUM = 1;
                queryTradebytypePage(false);
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(R.color.base_E2E5E7)
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

    /*时间戳转换成字符窜*/
    private String getDateToString1(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    /**
     * 选择日期
     *
     * @param position
     */
    @Override
    public void onMyItemTitleClick(int position) {
        //RxToast.showToast("选择日期");
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
        list.clear();
        queryTradebytypePage(false);
    }
}
