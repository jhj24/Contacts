package com.jhj.contacts.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jianhaojie on 2017/5/9.
 */

public class InfoBean implements Serializable {
    public List<Data> data;
    public String result;
    public String msg;

    public class Data implements Serializable {
        public String id;
        public String name;
        public String number;
        public String allLetter;
        public String letter;
        public String alpha;
        public String[] array;
        public String[] filterArray;
        public String[] sortArray;
    }
}
