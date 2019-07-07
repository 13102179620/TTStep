package com.github.mikephil.charting.interfaces;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    com.github.mikephil.charting.data.BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isDrawHighlightArrowEnabled();
}
