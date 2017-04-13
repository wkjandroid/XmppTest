package com.example.wkj.xmpptest.utils;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by wkj on 2017/3/26.
 */

public class PinyinUtil {
    public static String getPinyin(String str){
        return PinyinHelper.convertToPinyinString(str,"", PinyinFormat.WITHOUT_TONE);
    }
}
