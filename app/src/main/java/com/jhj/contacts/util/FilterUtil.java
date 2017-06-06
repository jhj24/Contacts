package com.jhj.contacts.util;

import java.util.Arrays;
import java.util.List;

/**
 * 筛选工具类
 * Created by jianhaojie on 2017/5/17.
 */

public class FilterUtil {
    /**
     * <h3/>对输入的拼音进行筛选<p/>
     * 例如：刘德华[liu][de][hua]，利用for循环根根据indexOf()方法得到每个元素的位置，
     * 然后将数组分别变成liudehua，dehua，hua三个字符串，逐一判断字符串是否以参数filter开始，如果是返回true，结束该方法。
     *
     * @param filter 输入的文字
     * @param array  要筛选的数组
     * @return boolean
     */
    public static boolean isFilter(String filter, String[] array) {
        List<String> list = Arrays.asList(array);
        for (String s : array) {
            String string = "";
            int pos = list.indexOf(s);
            for (int i = pos; i < array.length; i++) {
                string += array[i];
            }
            if (string.toUpperCase().startsWith(filter.toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 对汉字进行筛选
     *
     * @param filter filter
     * @param name   name
     * @return boolean
     */
    public static boolean isFilter(String filter, String name) {
        return name.contains(filter);
    }

    /**
     * 汉字和拼音都进行筛选
     *
     * @param filter filter
     * @param array  array
     * @param name   name
     * @return boolean
     */
    public static boolean isFilter(String filter, String[] array, String name) {
        return isFilter(filter, array) || isFilter(filter, name);
    }
}
