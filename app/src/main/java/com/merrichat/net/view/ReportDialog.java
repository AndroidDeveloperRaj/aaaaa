package com.merrichat.net.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.adapter.QueryReportingAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.QueryReportingTypesModel;
import com.merrichat.net.rxjava.BaseSubscribe;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amssy on 17/12/12.
 * 举报弹框
 */
public class ReportDialog extends Dialog implements View.OnClickListener {

    private Context context;

    /**
     * 取消
     */
    private LinearLayout ll_cencel;

    private ListView mReportingType;  //列表显示举报列表
    private QueryReportingAdapter reportingAdapter;
    public List<QueryReportingTypesModel.ReportingTypesItem> dataItems = new ArrayList<>();      //列表总共的消息条数


    private ReportOnclick reportOnclick;


    public ReportDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_report, null);
        ll_cencel = (LinearLayout) view.findViewById(R.id.ll_cencel);
        mReportingType = (ListView) view.findViewById(R.id.reporting_type);
        ll_cencel.setOnClickListener(this);
        getWalletInfo();
        setContentView(view);

        mReportingType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reportOnclick.report(dataItems.get(position).number + "",dataItems.get(position).typeName);
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_cencel://取消
                dismiss();
                break;
        }
    }

    public interface ReportOnclick {
        void report(String type,String typeName);
    }

    public void setOnclickInterReport(ReportOnclick report) {
        this.reportOnclick = report;
    }

    /**
     * 获取举报列表
     */
    private void getWalletInfo() {
        ApiManager.getApiManager().getService(WebApiService.class).queryReportingTypes("1", "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryReportingTypesModel>() {
                    @Override
                    public void onNext(QueryReportingTypesModel reportingTypesModel) {
                        if (reportingTypesModel.success) {
                            dataItems = reportingTypesModel.data;
                            reportingAdapter = new QueryReportingAdapter(context, dataItems);
                            mReportingType.setAdapter(reportingAdapter);
                        } else {
                            Toast.makeText(context, "服务器异常，请求失败", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }
                });
    }


}
