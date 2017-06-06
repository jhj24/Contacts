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
import com.jhj.contacts.util.FilterUtil;
import com.jhj.contacts.util.ReadUtil;
import com.jhj.contacts.util.SortUtil;
import com.jhj.contacts.view.SideBar;
import com.jhj.contacts.view.TitleItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通讯录--自定义分割线
 * Created by jianhaojie on 2017/5/9.
 */

public class SecondListActivity extends Activity {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private CharacterUtil character;
    public SecondListAdapter adapter;
    InputMethodManager inputManager = null;
    private LinearLayoutManager manager;
    private TitleItemDecoration itemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        etSearch = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        character = CharacterUtil.getInstance(this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        setKeyboardVisibility();
        initData();
        setSideBar();

    }

    private void initData() {
        try {
            String string = ReadUtil.getAssetsInfo(this, "constant.json");
            InfoBean gson = new Gson().fromJson(string, InfoBean.class);

            if (gson != null && gson.data != null) {
                List<InfoBean.Data> list = gson.data;
                for (InfoBean.Data data1 : list) {
                    data1.array = character.getStringArray(data1.name);
                    data1.alpha = getAlpha(character.getFirstAlpha(data1.name));
                }
                Collections.sort(list, new SortUtil());
                adapter = new SecondListAdapter(SecondListActivity.this, list);
                itemDecoration = new TitleItemDecoration(SecondListActivity.this, list);
                recyclerView.addItemDecoration(itemDecoration);
                recyclerView.setAdapter(adapter);
                searchData(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void searchData(final List<InfoBean.Data> beanList) {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<InfoBean.Data> dataList;
                recyclerView.removeItemDecoration(itemDecoration);
                if (TextUtils.isEmpty(etSearch.getText())) {
                    dataList = beanList;
                } else {
                    dataList = new ArrayList<>();
                    String str = etSearch.getText().toString();
                    for (InfoBean.Data bean : beanList) {
                        /*if (bean.name.contains(str)) {
                            dataList.add(bean);
                        }*/
                        if (FilterUtil.isFilter(str, bean.array, bean.name)) {
                            dataList.add(bean);
                        }
                    }

                }
                adapter = new SecondListAdapter(SecondListActivity.this, dataList);
                itemDecoration = new TitleItemDecoration(SecondListActivity.this, dataList);
                if (dataList.size() > 0) {
                    recyclerView.addItemDecoration(itemDecoration);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void setSideBar() {
        TextView tvCenter = (TextView) findViewById(R.id.tv_center);
        SideBar sideBar = (SideBar) findViewById(R.id.sidebar);
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
     * 对于首字母不[A-Z]的用"#"代替
     */
    private String getAlpha(String string) {
        if (string.toUpperCase().matches("[A-Z]")) {
            return string.toUpperCase();
        } else {
            return "#";
        }
    }

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
