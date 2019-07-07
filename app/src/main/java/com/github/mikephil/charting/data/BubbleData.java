
package com.github.mikephil.charting.data;

public class BubbleData extends BarLineScatterCandleBubbleData<BubbleDataSet> {

    public BubbleData() {
        super();
    }

    public BubbleData(java.util.List<String> xVals) {
        super(xVals);
    }

    public BubbleData(String[] xVals) {
        super(xVals);
    }

    public BubbleData(java.util.List<String> xVals, java.util.List<BubbleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BubbleData(String[] xVals, java.util.List<BubbleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BubbleData(java.util.List<String> xVals, BubbleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public BubbleData(String[] xVals, BubbleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static java.util.List<BubbleDataSet> toList(BubbleDataSet dataSet) {
        java.util.List<BubbleDataSet> sets = new java.util.ArrayList<BubbleDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Sets the width of the circle that surrounds the bubble when highlighted
     * for all DataSet objects this data object contains, in dp.
     * 
     * @param width
     */
    public void setHighlightCircleWidth(float width) {
        for (BubbleDataSet set : mDataSets) {
            set.setHighlightCircleWidth(width);
        }
    }
}
