
package com.github.mikephil.charting.data;

/**
 * Data container for the RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<RadarDataSet> {

    public RadarData() {
        super();
    }
    
    public RadarData(java.util.List<String> xVals) {
        super(xVals);
    }
    
    public RadarData(String[] xVals) {
        super(xVals);
    }
    
    public RadarData(java.util.List<String> xVals, java.util.List<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(String[] xVals, java.util.List<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(java.util.List<String> xVals, RadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public RadarData(String[] xVals, RadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }
    
    private static java.util.List<RadarDataSet> toList(RadarDataSet dataSet) {
        java.util.List<RadarDataSet> sets = new java.util.ArrayList<RadarDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
