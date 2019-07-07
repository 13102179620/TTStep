package com.github.mikephil.charting.formatter;

/**
 * Interface that can be used to return a customized color instead of setting
 * colors via the setColor(...) method of the DataSet.
 * 
 * @author Philipp Jahoda
 */
public interface ColorFormatter {

    int getColor(com.github.mikephil.charting.data.Entry e, int index);
}