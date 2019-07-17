
package com.github.mikephil.charting.data;

public class ScatterData extends BarLineScatterCandleBubbleData<ScatterDataSet> {

    public ScatterData() {
        super();
    }
    
    public ScatterData(java.util.List<String> xVals) {
        super(xVals);
    }

    public ScatterData(String[] xVals) {
        super(xVals);
    }

    public ScatterData(java.util.List<String> xVals, java.util.List<ScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(String[] xVals, java.util.List<ScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(java.util.List<String> xVals, ScatterDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public ScatterData(String[] xVals, ScatterDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static java.util.List<ScatterDataSet> toList(ScatterDataSet dataSet) {
        java.util.List<ScatterDataSet> sets = new java.util.ArrayList<ScatterDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the maximum shape-size across all DataSets.
     * 
     * @return
     */
    public float getGreatestShapeSize() {

        float max = 0f;

        for (ScatterDataSet set : mDataSets) {
            float size = set.getScatterShapeSize();

            if (size > max)
                max = size;
        }

        return max;
    }
}
