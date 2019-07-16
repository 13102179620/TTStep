package com.syt.ttstep.utils;

import java.text.DecimalFormat;

public class MathCaculateUtils {

    public static String getFormatVal(double val){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(val);
    }

}
