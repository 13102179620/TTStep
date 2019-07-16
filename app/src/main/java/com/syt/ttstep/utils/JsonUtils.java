package com.syt.ttstep.utils;

import com.google.gson.Gson;

/**
 * @class name：JsonUtils
 * @describe json 解析工具类
 * @author syt
 * @time 2019/7/11
 */
public class JsonUtils {

    public static String objToJson(Object object){
        Gson gson = new Gson();
        String res = gson.toJson(object);
        return res;
    }

}
