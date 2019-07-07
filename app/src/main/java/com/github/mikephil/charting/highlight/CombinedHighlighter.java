package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider;

/**
 * Created by Philipp Jahoda on 12/09/15.
 */
public class CombinedHighlighter extends ChartHighlighter<BarLineScatterCandleBubbleDataProvider> {

    public CombinedHighlighter(BarLineScatterCandleBubbleDataProvider chart) {
        super(chart);
    }

    /**
     * Returns a list of SelectionDetail object corresponding to the given xIndex.
     *
     * @param xIndex
     * @return
     */
    @Override
    protected java.util.List<com.github.mikephil.charting.utils.SelectionDetail> getSelectionDetailsAtIndex(int xIndex) {

        com.github.mikephil.charting.data.CombinedData data = (com.github.mikephil.charting.data.CombinedData) mChart.getData();

        // get all chartdata objects
        java.util.List<com.github.mikephil.charting.data.ChartData> dataObjects = data.getAllData();

        java.util.List<com.github.mikephil.charting.utils.SelectionDetail> vals = new java.util.ArrayList<com.github.mikephil.charting.utils.SelectionDetail>();

        float[] pts = new float[2];

        for (int i = 0; i < dataObjects.size(); i++) {

            for(int j = 0; j < dataObjects.get(i).getDataSetCount(); j++) {

                com.github.mikephil.charting.data.DataSet<?> dataSet = dataObjects.get(i).getDataSetByIndex(j);

                // dont include datasets that cannot be highlighted
                if (!dataSet.isHighlightEnabled())
                    continue;

                // extract all y-values from all DataSets at the given x-index
                final float yVal = dataSet.getYValForXIndex(xIndex);
                if (yVal == Float.NaN)
                    continue;

                pts[1] = yVal;

                mChart.getTransformer(dataSet.getAxisDependency()).pointValuesToPixel(pts);

                if (!Float.isNaN(pts[1])) {
                    vals.add(new com.github.mikephil.charting.utils.SelectionDetail(pts[1], j, dataSet));
                }
            }
        }

        return vals;
    }
}
