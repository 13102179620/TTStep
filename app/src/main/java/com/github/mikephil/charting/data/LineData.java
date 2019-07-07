
package com.github.mikephil.charting.data;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<LineDataSet> {

    public LineData() {
        super();
    }

    public LineData(java.util.List<String> xVals) {
        super(xVals);
    }

    public LineData(String[] xVals) {
        super(xVals);
    }

    public LineData(java.util.List<String> xVals, java.util.List<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(String[] xVals, java.util.List<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(java.util.List<String> xVals, LineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public LineData(String[] xVals, LineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static java.util.List<LineDataSet> toList(LineDataSet dataSet) {
        java.util.List<LineDataSet> sets = new java.util.ArrayList<LineDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
