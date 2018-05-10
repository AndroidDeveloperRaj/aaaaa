package com.merrichat.net.activity.groupmanage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.merrichat.net.R;
import com.merrichat.net.activity.groupmanage.sortlistview.CharacterParser;
import com.merrichat.net.activity.groupmanage.sortlistview.PinyinComparator;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.SortAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.ExpressApiService;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.CompanyData;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.weidget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SelectExpressCompanyActivity extends MerriActionBarActivity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;

    private CharacterParser characterParser;
    private List<CompanyData> SourceDateList;

    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_expres_compan);
        setLeftBack();
        setTitle("选择快递公司");
        initViews();


        getBeReport();
    }

    private void initViews() {
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("expressInfoModel", ((CompanyData) adapter.getItem(position)));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        SourceDateList = new ArrayList<>();
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString().trim());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterData(String filterStr) {
        List<CompanyData> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (CompanyData sortModel : SourceDateList) {
                String name = sortModel.netName;
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    /**
     * 获取快递公司列表
     */
    public void getBeReport() {
        ApiManager.getApiManager().getService(WebApiService.class).getNets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<GetNetsModel>() {
                    @Override
                    public void onNext(GetNetsModel getNetsModel) {
                        if (getNetsModel.success) {
                            SourceDateList.clear();
                            SourceDateList.addAll(getNetsModel.data.A);
                            SourceDateList.addAll(getNetsModel.data.B);
                            SourceDateList.addAll(getNetsModel.data.C);
                            SourceDateList.addAll(getNetsModel.data.D);
                            SourceDateList.addAll(getNetsModel.data.E);
                            SourceDateList.addAll(getNetsModel.data.F);
                            SourceDateList.addAll(getNetsModel.data.G);
                            SourceDateList.addAll(getNetsModel.data.H);
                            SourceDateList.addAll(getNetsModel.data.J);
                            SourceDateList.addAll(getNetsModel.data.K);
                            SourceDateList.addAll(getNetsModel.data.L);
                            SourceDateList.addAll(getNetsModel.data.M);
                            SourceDateList.addAll(getNetsModel.data.N);
                            SourceDateList.addAll(getNetsModel.data.P);
                            SourceDateList.addAll(getNetsModel.data.Q);
                            SourceDateList.addAll(getNetsModel.data.R);
                            SourceDateList.addAll(getNetsModel.data.S);
                            SourceDateList.addAll(getNetsModel.data.T);
                            SourceDateList.addAll(getNetsModel.data.U);
                            SourceDateList.addAll(getNetsModel.data.W);
                            SourceDateList.addAll(getNetsModel.data.X);
                            SourceDateList.addAll(getNetsModel.data.Y);
                            SourceDateList.addAll(getNetsModel.data.Z);

                            Collections.sort(SourceDateList, pinyinComparator);
                            adapter.updateListView(SourceDateList);

                        } else {
                            Toast.makeText(SelectExpressCompanyActivity.this, "请求失败～！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(SelectExpressCompanyActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }
}