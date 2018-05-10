package com.merrichat.net.activity.my;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.PersonalInfoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxKeyboardTool;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/18.
 * 编辑工作经历
 */

public class EditWorkInfoAty extends BaseActivity {
    private final int START_TIME = 0x0001;
    private final int END_TIME = 0x0002;
    private final String status = "1";//0:删除教育经历 1:删除工作经历
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_xuexiao)
    EditText tvXuexiao;
    @BindView(R.id.rl_gongsi)
    RelativeLayout rlGongsi;
    @BindView(R.id.tv_zhuanwei)
    EditText tvZhuanwei;
    @BindView(R.id.rl_zhuanwei)
    RelativeLayout rlZhuanwei;
    @BindView(R.id.tv_starttime)
    TextView tvStarttime;
    @BindView(R.id.rl_starttime)
    RelativeLayout rlStarttime;
    @BindView(R.id.tv_endtime)
    TextView tvEndtime;
    @BindView(R.id.rl_endtime)
    RelativeLayout rlEndtime;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_save)
    TextView tvSave;
    private PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean workExperienceBean;
    private String workId;
    private TimePickerView pvTime;


    /**
     * 选中的开始时间
     */
    private long startTime = 0;

    /**
     * 选中的结束时间
     */
    private long endTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editworkinfo);
        ButterKnife.bind(this);
        initTitle();
        initData();
    }

    private void initData() {
        workExperienceBean = (PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean) getIntent().getSerializableExtra("workExperienceBean");
        if (workExperienceBean != null) {
            tvDelete.setVisibility(View.VISIBLE);
            workId = workExperienceBean.getWorkId();
            tvXuexiao.setText(workExperienceBean.getCompany());
            tvZhuanwei.setText(workExperienceBean.getOccupation());
            tvStarttime.setText(RxTimeTool.getDate(workExperienceBean.getStartTime(), "yyyy-MM-dd"));
            tvEndtime.setText(RxTimeTool.getDate(workExperienceBean.getEndTime(), "yyyy-MM-dd"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxKeyboardTool.hideSoftInput(this);

    }

    private void initTitle() {
        tvTitleText.setText("编辑工作经历");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");

    }

    /**
     * 点击空白处 隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            RxKeyboardTool.clickBlankArea2HideSoftInput1(EditWorkInfoAty.this, v, ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.rl_gongsi, R.id.rl_zhuanwei, R.id.rl_starttime, R.id.rl_endtime, R.id.tv_delete, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                toSaveWorkExp();
                break;
            case R.id.rl_gongsi:
                break;
            case R.id.rl_zhuanwei:
                break;
            case R.id.rl_starttime:
                initTimePicker(START_TIME);
                pvTime.show();
                break;
            case R.id.rl_endtime:
                initTimePicker(END_TIME);
                pvTime.show();
                break;
            case R.id.tv_delete:
                if (RxDataTool.isNullString(workId)) {
                    RxToast.showToast("该经历不存在，请添加！");
                    return;
                }
                deleteWorkExp();
                break;
            case R.id.tv_save:
                toSaveWorkExp();
                break;
        }
    }

    /**
     * 保存工作经历
     */
    private void toSaveWorkExp() {
        PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean workExperienceBean = new PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean();
        String xueXiaoString = tvXuexiao.getText().toString().trim();
        String zhiWeiString = tvZhuanwei.getText().toString().trim();
        String startTimeString = tvStarttime.getText().toString().trim();
        String endStringString = tvEndtime.getText().toString().trim();
        if (RxDataTool.isNullString(xueXiaoString) || RxDataTool.isNullString(zhiWeiString)
                || RxDataTool.isNullString(startTimeString) || RxDataTool.isNullString(endStringString)) {
            RxToast.showToast("请将编辑信息填写完整！");
            return;
        }
        workExperienceBean.setCompany(xueXiaoString);
        workExperienceBean.setOccupation(zhiWeiString);
        workExperienceBean.setStartTime(RxTimeTool.string2Timestamp("yyyy-MM-dd", startTimeString));
        workExperienceBean.setEndTime(RxTimeTool.string2Timestamp("yyyy-MM-dd", endStringString));
        workExperienceBean.setWorkId(RxDataTool.isNullString(workId) ? "" : workId);
        Gson gson = new Gson();
        String jsonString = gson.toJson(workExperienceBean);
        saveWorkExp(jsonString, workExperienceBean);
    }

    private void deleteWorkExp() {
        OkGo.<String>get(Urls.deleteExperience)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("id", workId)
                .params("status", status)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            boolean success = jsonObjectEx.getBoolean("success");
                            if (success) {
                                sendEvenBus();
                                RxToast.showToast("已删除！");
                                finish();
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

    private void saveWorkExp(String jsonString, final PersonalInfoModel.DataBean.InfoBean.WorkExperienceBean workExperienceBean) {
        OkGo.<String>get(Urls.INSERT_WORK)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("json", jsonString)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            boolean success = jsonObjectEx.getBoolean("success");
                            if (success) {
                                sendEvenBus();
                                RxToast.showToast("已保存！");
                                finish();
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

    private void sendEvenBus() {
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.REFRESH_PERSON_INFO_EPX = true;
        EventBus.getDefault().post(myEventBusModel);
    }

    /**
     * 时间选择器
     */
    private void initTimePicker(final int type) {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 23);
        final Calendar endDate = Calendar.getInstance();
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
                if (type == START_TIME) {
                    String startTime = getTime(date);
                    String endTime = tvEndtime.getText().toString().trim();
                    if (!RxDataTool.isNullString(endTime)) {
                        boolean bigger = RxTimeTool.isDateOneBigger(startTime, endTime);//比较开始时间和结束时间的大小
                        if (bigger) {
                            RxToast.showToast("结束时间早于开始时间，请重新选择");
                            return;
                        }
                    }
                    tvStarttime.setText(startTime);
                } else {
                    String startTime = tvStarttime.getText().toString().trim();
                    String endTime = getTime(date);
                    if (RxDataTool.isNullString(startTime)) {
                        RxToast.showToast("请先选择开始时间！");
                        return;
                    }
                    boolean bigger = RxTimeTool.isDateOneBigger(startTime, endTime);//比较开始时间和结束时间的大小
                    if (bigger) {
                        RxToast.showToast("结束时间早于开始时间，请重新选择");
                        return;
                    }
                    tvEndtime.setText(endTime);
                }
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
