package com.jhj.contacts.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhj.contacts.R;
import com.jhj.contacts.bean.InfoBean;
import com.jhj.contacts.util.CharacterUtil;
import com.jhj.contacts.util.ReadUtil;
import com.jhj.contacts.util.SortUtil;
import com.jhj.contacts.view.SideBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通讯录
 * Created by jianhaojie on 2017/5/9.
 */

public class FirstListActivity extends Activity {
    private EditText etSearch;
    private RecyclerView recyclerView;
    private CharacterUtil character;
    private FirstListAdapter adapter;
    InputMethodManager inputManager = null;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        etSearch = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        character = CharacterUtil.getInstance(this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        initData();
        setSideBar();
        setKeyboardVisibility();
    }

    private void initData() {
        try {
            String string = ReadUtil.getAssetsInfo(this, "constant.json");
            InfoBean infoBean = new Gson().fromJson(string, InfoBean.class);
            if (infoBean != null && infoBean.data != null) {
                for (InfoBean.Data bean : infoBean.data) {
                    bean.alpha = character.getFirstAlpha(bean.name);
                    bean.array = character.getStringArray(bean.name);
                }
                Collections.sort(infoBean.data, new SortUtil());
                adapter = new FirstListAdapter(this, infoBean.data);
                recyclerView.setAdapter(adapter);
                searchData(infoBean.data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchData(final List<InfoBean.Data> data) {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<InfoBean.Data> dataList = new ArrayList<>();
                if (TextUtils.isEmpty(etSearch.getText())) {
                    dataList = data;
                } else {
                    dataList.clear();
                    String str = etSearch.getText().toString();
                    for (InfoBean.Data bean : data) {
                        if (bean.name.contains(str)) {
                            dataList.add(bean);
                        }
                    }
                }
                adapter = new FirstListAdapter(FirstListActivity.this, dataList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setSideBar() {
        SideBar sideBar = (SideBar) findViewById(R.id.sidebar);
        TextView tvCenter = (TextView) findViewById(R.id.tv_center);
        sideBar.setTextView(tvCenter);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionFromSection(s.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
    }

    /**
     * 设置键盘是否隐藏，点击搜索框，显示键盘，点击RecyclerView键盘消失
     */
    private void setKeyboardVisibility() {
        final RelativeLayout layoutSearch = (RelativeLayout) findViewById(R.id.layout_search);
        layoutSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutSearch.setVisibility(View.GONE);
                if (inputManager == null) {
                    inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(etSearch, 0);
                }
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        inputManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                        if (TextUtils.isEmpty(etSearch.getText())) {
                            layoutSearch.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }

}
