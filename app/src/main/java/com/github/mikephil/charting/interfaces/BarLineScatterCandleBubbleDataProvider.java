package com.github.mikephil.charting.interfaces;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    com.github.mikephil.charting.utils.Transformer getTransformer(com.github.mikephil.charting.components.YAxis.AxisDependency axis);
    int getMaxVisibleCount();
    boolean isInverted(com.github.mikephil.charting.components.YAxis.AxisDependency axis);
    
    int getLowestVisibleXIndex();
    int getHighestVisibleXIndex();

    com.github.mikephil.charting.data.BarLineScatterCandleBubbleData getData();
}
