package com.syt.ttstep.utils;

import java.text.DecimalFormat;

public class StringUtils {
    public static String getFormatSring(double val){
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        return decimalFormat.format(val);
    }

    public static String getFormatSring(int val){
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        return decimalFormat.format(val);
    }

    public static String getFormatSring(float val){
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        return decimalFormat.format(val);
    }
}
