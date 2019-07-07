
package com.github.mikephil.charting.data;

public class RadarDataSet extends LineRadarDataSet<Entry> {
    
    public RadarDataSet(java.util.List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<Entry> copy() {

        java.util.List<Entry> yVals = new java.util.ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        RadarDataSet copied = new RadarDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }
}
