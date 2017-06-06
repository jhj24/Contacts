package com.jhj.contacts.util;

import com.jhj.contacts.bean.UserBean;

import java.util.Comparator;

/**
 * 排序规则：数组中的元素逐个比较
 * Created by jianhaojie on 2017/6/6.
 */

public class ArraySortUtil implements Comparator<UserBean> {
    @Override
    public int compare(UserBean o1, UserBean o2) {
        return arrayCompare(o1.sortArray, o2.sortArray);
    }

    private int arrayCompare(String arr1[], String arr2[]) {

        if (arr1.length < arr2.length) {
            for (int i = 0; i < arr1.length; i++) {
                if (i == arr1.length - 1) {
                    if (arr1[i].compareTo(arr2[i]) != 0) {
                        return arr1[i].compareTo(arr2[i]);
                    } else {
                        return -1;
                    }
                } else if (arr1[i].compareTo(arr2[i]) != 0) {
                    return arr1[i].compareTo(arr2[i]);
                }
            }
        } else {
            for (int i = 0; i < arr2.length; i++) {
                if (arr2[i].compareTo(arr1[i]) != 0) {
                    return arr1[i].compareTo(arr2[i]);
                }
            }
        }
        return 0;
    }
}
