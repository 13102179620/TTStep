package com.github.mikephil.charting.data;

public class CandleData extends BarLineScatterCandleBubbleData<CandleDataSet> {

    public CandleData() {
        super();
    }
    
    public CandleData(java.util.List<String> xVals) {
        super(xVals);
    }
    
    public CandleData(String[] xVals) {
        super(xVals);
    }
    
    public CandleData(java.util.List<String> xVals, java.util.List<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(String[] xVals, java.util.List<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public CandleData(java.util.List<String> xVals, CandleDataSet dataSet) {
        super(xVals, toList(dataSet));        
    }
    
    public CandleData(String[] xVals, CandleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }
    
    private static java.util.List<CandleDataSet> toList(CandleDataSet dataSet) {
        java.util.List<CandleDataSet> sets = new java.util.ArrayList<CandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
