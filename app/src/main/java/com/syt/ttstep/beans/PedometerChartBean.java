package com.syt.ttstep.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * @class name：PedometerChartBean
 * @describe 用于绘图的实体类，可序列化，从service回
 * @author syt
 * @time 2019/7/11
 */
public class PedometerChartBean implements Parcelable {
    //x轴长度最多有 24h * 60min
    public static final int MaxIndex = 1440;
    private int[] arrays ;
    private int index ;

    public PedometerChartBean(){
        index = 0;
        arrays = new int[MaxIndex];
    }

    public int[] getArrays() {
        return arrays;
    }

    public void setArrays(int[] arrays) {
        this.arrays = arrays;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    //重置数据
    public  void reset()
    {
        for (int i : arrays)
        {
            i=0;
        }
        index=0;
    }




    /**
     * 下面是序列化接口实现方法
     * */
    @Override
    public int describeContents() {
        return 0;
    }


    protected PedometerChartBean(Parcel in) {
        arrays = in.createIntArray();
        index = in.readInt();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(arrays);
        dest.writeInt(index);
    }

    public static final Creator<PedometerChartBean> CREATOR = new Creator<PedometerChartBean>() {
        @Override
        public PedometerChartBean createFromParcel(Parcel in) {
            return new PedometerChartBean(in);
        }

        @Override
        public PedometerChartBean[] newArray(int size) {
            return new PedometerChartBean[size];
        }
    };

    @Override
    public String toString() {
        return "PedometerChartBean{" +
                "arrays=" + Arrays.toString(arrays) +
                ", index=" + index +
                '}';
    }
}
