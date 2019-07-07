package com.github.mikephil.charting.interfaces;

/**
 * Interface that provides everything there is to know about the dimensions,
 * bounds, and range of the chart.
 * 
 * @author Philipp Jahoda
 */
public interface ChartInterface {

    float getXChartMin();

    float getXChartMax();

    float getYChartMin();

    float getYChartMax();
    
    int getXValCount();

    int getWidth();

    int getHeight();

    android.graphics.PointF getCenterOfView();

    android.graphics.PointF getCenterOffsets();

    android.graphics.RectF getContentRect();
    
    com.github.mikephil.charting.formatter.ValueFormatter getDefaultValueFormatter();

    com.github.mikephil.charting.data.ChartData getData();
}
