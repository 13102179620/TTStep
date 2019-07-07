package com.github.mikephil.charting.interfaces;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    com.github.mikephil.charting.data.LineData getLineData();

    com.github.mikephil.charting.components.YAxis getAxis(com.github.mikephil.charting.components.YAxis.AxisDependency dependency);
}
