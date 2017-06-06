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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jhj.contacts.R;
import com.jhj.contacts.bean.UserBean;
import com.jhj.contacts.util.ArraySortUtil;
import com.jhj.contacts.util.CharacterUtil;
import com.jhj.contacts.util.FilterUtil;
import com.jhj.contacts.util.ReadUtil;
import com.jhj.contacts.view.TitleItemDecoration_01;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jianhaojie on 2017/5/16.
 */

public class ThirdListActivity extends Activity {

    private RecyclerView recyclerView;
    private EditText etSearch;

    private CharacterUtil character;
    private InputMethodManager inputManager;
    public ThirdListAdapter adapter;
    private TitleItemDecoration_01 itemDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        etSearch = (EditText) findViewById(R.id.et_search);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        character = CharacterUtil.getInstance(this);
        setKeyboardVisibility();
        initData();

    }


    private void initData() {
        try {
            String s = ReadUtil.getAssetsInfo(this, "constants.json");
            List<UserBean> beanList = new Gson().fromJson(s, new TypeToken<List<UserBean>>() {
            }.getType());


            if (beanList != null && beanList.size() > 0) {
                for (UserBean data : beanList) {
                    data.alpha = getAlpha(character.getFirstAlpha(data.name));
                    data.filterArray = character.getStringArray(data.name);
                    data.sortArray = getArray(data.filterArray);
                }
                Collections.sort(beanList, new ArraySortUtil());
                adapter = new ThirdListAdapter(this, beanList);
                itemDirection = new TitleItemDecoration_01(this, beanList);
                recyclerView.addItemDecoration(itemDirection);
                recyclerView.setAdapter(adapter);
                searchData(beanList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对参数判断，如果是[A-Z],返回当前字符，其他的返回“#”
     *
     * @param string string
     * @return string
     */
    private String getAlpha(String string) {
        if (string.toUpperCase().matches("[A-Z]")) {
            return string.toUpperCase();
        } else {
            return "#";
        }
    }

    /**
     * <h3/>
     * 对当前数组进行编译，生成新的数组
     * <p>
     * 当前数组长度加1，每个元素位置后移一位（即下标加1）<p>
     * 根据原数组第一个元素是数字、字符、字母，新数组的第一个元素变成指定的值
     *
     * @param array array
     * @return 新数组
     */
    private String[] getArray(String[] array) {
        String start = array[0].trim().substring(0, 1).toUpperCase();
        String[] strings = new String[array.length + 1];
        if (start.matches("[A-Z]")) {
            strings[0] = "3";
        } else if (start.matches("[0-9]")) {
            strings[0] = "2";
        } else {
            strings[0] = "1";
        }
        System.arraycopy(array, 0, strings, 1, array.length);
        return strings;
    }

    /**
     * 搜索
     *
     * @param beanList
     */
    private void searchData(final List<UserBean> beanList) {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<UserBean> dataList;
                recyclerView.removeItemDecoration(itemDirection);
                if (TextUtils.isEmpty(etSearch.getText())) {
                    dataList = beanList;
                } else {
                    dataList = new ArrayList<>();
                    String str = etSearch.getText().toString();
                    for (UserBean bean : beanList) {
                        if (FilterUtil.isFilter(str, bean.filterArray,bean.name)) {
                            dataList.add(bean);
                        }
                    }
                }
                adapter = new ThirdListAdapter(ThirdListActivity.this, dataList);
                itemDirection = new TitleItemDecoration_01(ThirdListActivity.this, dataList);
                if (dataList.size() > 0) {
                    recyclerView.addItemDecoration(itemDirection);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    /**
     * 键盘可见性
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
