package com.syt.ttstep.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @class name：DateUtils
 * @describe 时间工具类
 * @author syt 
 * @time 2019/7/11 
 */
public class DateUtils {
    
    public static long getTimestempByDay(){
        // TODO: 2019/7/11  
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM--dd");
        Date d = new Date();
        String  dateStr = simpleDateFormat.format(d);

        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date.getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return 0L;
    }

}
