package com.jhj.contacts.util;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 中文转汉语拼音，支持多音字 <br>
 * 英文和字符直接返回
 */
public class CharacterUtil {
    /**
     * 字符串每个字符的全拼
     */
    private static final int TYPE_SPELLING = 1000;
    /**
     * 字符串每个字符的首字母
     */
    private static final int TYPE_ALPHA = 1001;
    private Properties p = new Properties();
    private Map<String, String[]> dictionary = new HashMap<>();

    private CharacterUtil(Context context) {
        decodeCharater(context);
        decodeSpelling(context);

    }

    private static volatile CharacterUtil character;

    public static CharacterUtil getInstance(Context context) {
        if (character == null) {
            synchronized (CharacterUtil.class) {
                if (character == null) {
                    character = new CharacterUtil(context);
                }
            }
        }
        return character;
    }

    /**
     * <h1/>返回字符串第一个字符的首字母<p>
     * 第一个字符为汉字时，返回其首字母。<br/>
     * 第一个字符为英文或符号时，直接返回
     *
     * @param chinese String
     * @return 刘德华 -> l  <br/>
     * #刘德华 -> #
     */
    public String getFirstAlpha(String chinese) {
        return getStringSpelling(chinese, true, TYPE_ALPHA);
    }

    /**
     * <h1>返回字符串所有字符的首字母<p>
     * 当字符为汉字时，返回其首字<br/>
     * 当字符为英文或符号时，直接返回
     *
     * @param chinese String
     * @return 刘德华 -> ldh <br/>
     * #刘德华 -> #ldh
     */
    public String getAllAlpha(String chinese) {
        return getStringSpelling(chinese, false, TYPE_ALPHA);
    }

    /**
     * <h1/>返回字符串第一个字符的全拼<p>
     * 当第一个字符为汉字时，返回其全拼。<br/>
     * 当第一个字符为英文或符号时，直接返回
     *
     * @param chinese String
     * @return 刘德华 -> liu <br/>
     * #刘德华 -> #
     */
    public String getFirstSpelling(String chinese) {
        return getStringSpelling(chinese, true, TYPE_SPELLING);
    }

    /**
     * <h1/>返回字符串所有字符的全拼<p>
     * 当字符为汉字时，返回全拼<br/>
     * 当字符为英文或符号时，直接返回
     *
     * @param chinese String
     * @return 刘德华 -> liudehua <br/>
     * #刘德华 -> #liudehua
     */
    public String getAllSpelling(String chinese) {
        return getStringSpelling(chinese, false, TYPE_SPELLING);
    }

    /**
     * 以数组的形式返回字符串每个字符的全拼
     *
     * @param chinese string
     * @return array
     */
    public String[] getStringArray(String chinese) {
        if (chinese == null) {
            return null;
        }
        char[] chs = chinese.trim().toCharArray();
        String[] array = new String[chs.length];
        for (int i = 0; i < chinese.length(); i++) {
            String[] spellArray = getHanyuPinyins(chs[i]);
            //字同音同调同、字同音同调不同
            if (spellArray.length == 1) {
                array[i] = spellArray[0];
            } else {
                String resultPy = getSpelling(chinese, chs, i, spellArray, TYPE_SPELLING);
                if (resultPy != null) {
                    array[i] = resultPy;
                }
            }
        }
        return array;
    }

    /**
     * 获取字符串的拼音，取出满足条件的
     *
     * @param chinese     要转化的字符串
     * @param isOnlyFirst true 只对字符串的第一个字符操作
     * @param type        当type为TYPE_SPELLING时，字符的全拼<br/>当type为TYPE_ALPHA时，字符的首字母
     * @return String
     */
    private String getStringSpelling(String chinese, boolean isOnlyFirst, int type) {
        int leng;
        if (chinese == null) {
            return null;
        }
        char[] chs = chinese.trim().toCharArray();
        if (isOnlyFirst) {
            leng = 1;
        } else {
            leng = chs.length;
        }
        StringBuilder spellBuilder = new StringBuilder();
        for (int i = 0; i < leng; i++) {
            String[] spellArray = getHanyuPinyins(chs[i]);
            //字同音同调同、字同音同调不同
            if (spellArray.length == 1) {
                if (type == TYPE_SPELLING) {
                    spellBuilder.append(spellArray[0]);
                } else if (type == TYPE_ALPHA) {
                    spellBuilder.append(spellArray[0].charAt(0));
                }
            } else {
                String resultPy = getSpelling(chinese, chs, i, spellArray, type);
                if (resultPy != null) {
                    spellBuilder.append(resultPy);
                }
            }
        }
        return spellBuilder.toString();
    }

    /**
     * 对多音字进行判断
     */
    @Nullable
    private String getSpelling(String chinese, char[] chs, int i, String[] spellArray, int type) {
        //字同音不同
        String resultPy = null, defaultPy = null;
        for (String py : spellArray) {
            if (dictionary.get(py) != null) {
                List<String> dataList = Arrays.asList(dictionary.get(py));
                boolean isTrue = dictionary.containsKey(py);
                //向左多取一个字,例如 银[行]
                if (i >= 1 && i + 1 <= chinese.length()) {
                    String left = chinese.substring(i - 1, i + 1);
                    if (isTrue && dataList.contains(left)) {
                        resultPy = py;
                        break;
                    }
                }

                //向右多取一个字,例如 [长]沙
                if (i <= chinese.length() - 2) {
                    String right = chinese.substring(i, i + 2);
                    if (isTrue && dataList.contains(right)) {
                        resultPy = py;
                        break;
                    }
                }

                //左右各多取一个字,例如 龙[爪]槐
                if (i >= 1 && i + 2 <= chinese.length()) {
                    String middle = chinese.substring(i - 1, i + 2);
                    if (isTrue && dataList.contains(middle)) {
                        resultPy = py;
                        break;
                    }
                }
                //向左多取2个字,如 芈月[传],列车长
                if (i >= 2 && i + 1 <= chinese.length()) {
                    String left3 = chinese.substring(i - 2, i + 1);
                    if (isTrue && dataList.contains(left3)) {
                        resultPy = py;
                        break;
                    }
                }

                //向右多取2个字,如 [长]孙无忌
                if (i <= chinese.length() - 3) {
                    String right3 = chinese.substring(i, i + 3);
                    if (isTrue && dataList.contains(right3)) {
                        resultPy = py;
                        break;
                    }
                }

                //默认拼音
                if (isTrue && dataList.contains(String.valueOf(chs[i]))) {
                    defaultPy = py;
                }
            }
        }

        if (resultPy == null) {
            if (defaultPy == null) {
                resultPy = null;
            } else {
                resultPy = spellArray[0];
            }
        }
        String string = null;
        if (type == TYPE_ALPHA) {
            if (resultPy != null) {
                string = resultPy.substring(0, 1);
            }
        } else if (type == TYPE_SPELLING) {
            string = resultPy;
        }
        return string;
    }


    /**
     * <h1>汉字返回拼音，英文和字符直接返回
     * <h2/>工作流程<p/>
     * 1、将char类型的强行转化为int型，得到的是其10进制的编码<br/>
     * 2、用Integer.toHexString()方法将10进制转化为16进制。<br/>
     * 3、根据16进制数据在字典中查询其拼音，拆分为数组。<br/>
     * 4、字典中只有汉字对应的16进制数据，其他字符查询结果为空，返回其原有字符的数组。<p/>
     * <p>
     * eg、德 ->14503 ->5FB7 ->de ->[de]<br/>
     * 重 ->37325 ->91CD ->zhong,chong ->[zhong,chong]<br/>
     * #  ->35 -> 23 -> null ->[#]
     *
     * @param c 要查询的char
     * @return 汉字返回拼音，英文和字符直接返回
     */
    private String[] getHanyuPinyins(char c) {
        String key = Integer.toHexString((int) c).toUpperCase();
        String str = (String) p.get(key);
        //当字符c为符号时，str为空返回原字符
        if (str == null) {
            return new String[]{String.valueOf(c)};
        }
        return str.split(",");
    }

    /**
     * 获取汉字字典
     *
     * @param context context;
     */
    private void decodeSpelling(Context context) {
        try {
            InputStream in = context.getAssets().open("pinyin.txt");
            InputStreamReader reader = new InputStreamReader(in, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            p.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析多音字字典
     *
     * @param context context;
     */
    private void decodeCharater(Context context) {
        try {
            InputStream in = context.getAssets().open("duoyinzi.txt");
            InputStreamReader reader = new InputStreamReader(in, "utf-8");
            BufferedReader brr = new BufferedReader(reader);
            String line;
            while ((line = brr.readLine()) != null) {
                String[] arr = line.split("#");
                if (arr[1] != null) {
                    String[] sems = arr[1].split("/");
                    dictionary.put(arr[0], sems);
                }
            }
            brr.close();
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
