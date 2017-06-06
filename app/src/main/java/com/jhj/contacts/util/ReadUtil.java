package com.jhj.contacts.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 读取文件工具类
 * Created by jianhaojie on 2017/5/9.
 */

public class ReadUtil {

    /**
     * 读取assets文件夹下的文件
     *
     * @param context context
     * @param file    文件名
     * @return 读取到的文件内容
     * @throws IOException
     */
    public static String getAssetsInfo(Context context, String file) throws IOException {
        InputStream in = context.getAssets().open(file);
        //将字节流转化成字符流
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder buffer = new StringBuilder("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        bufferedReader.close();
        reader.close();
        in.close();
        return buffer.toString();
    }
}
