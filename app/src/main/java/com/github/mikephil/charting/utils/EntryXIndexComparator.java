package com.github.mikephil.charting.utils;

/**
 * Comparator for comparing Entry-objects by their x-index.
 * Created by philipp on 17/06/15.
 */
public class EntryXIndexComparator implements java.util.Comparator<com.github.mikephil.charting.data.Entry> {
    @Override
    public int compare(com.github.mikephil.charting.data.Entry entry1, com.github.mikephil.charting.data.Entry entry2) {
        return entry1.getXIndex() - entry2.getXIndex();
    }
}
