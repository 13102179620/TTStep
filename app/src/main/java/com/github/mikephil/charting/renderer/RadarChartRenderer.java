
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;

public class RadarChartRenderer extends LineScatterCandleRadarRenderer {

    protected com.github.mikephil.charting.charts.RadarChart mChart;

    /**
     * paint for drawing the web
     */
    protected android.graphics.Paint mWebPaint;

    public RadarChartRenderer(com.github.mikephil.charting.charts.RadarChart chart, ChartAnimator animator,
                              com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(android.graphics.Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(android.graphics.Color.rgb(255, 187, 115));

        mWebPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(android.graphics.Paint.Style.STROKE);
    }

    public android.graphics.Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawData(android.graphics.Canvas c) {

        com.github.mikephil.charting.data.RadarData radarData = mChart.getData();

        for (com.github.mikephil.charting.data.RadarDataSet set : radarData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(android.graphics.Canvas c, com.github.mikephil.charting.data.RadarDataSet dataSet) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        android.graphics.PointF center = mChart.getCenterOffsets();

        java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

        android.graphics.Path surface = new android.graphics.Path();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < entries.size(); j++) {

            mRenderPaint.setColor(dataSet.getColor(j));

            com.github.mikephil.charting.data.Entry e = entries.get(j);

            android.graphics.PointF p = com.github.mikephil.charting.utils.Utils.getPosition(center, (e.getVal() - mChart.getYChartMin()) * factor,
                                                                                             sliceangle * j + mChart.getRotationAngle());

            if (Float.isNaN(p.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(p.x, p.y);
                hasMovedToPoint = true;
            } else
                surface.lineTo(p.x, p.y);
        }

        surface.close();

        // draw filled
        if (dataSet.isDrawFilledEnabled()) {
            mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);
            mRenderPaint.setAlpha(dataSet.getFillAlpha());
            c.drawPath(surface, mRenderPaint);
            mRenderPaint.setAlpha(255);
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setStyle(android.graphics.Paint.Style.STROKE);

        // draw the line (only if filled is disabled or alpha is below 255)
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
            c.drawPath(surface, mRenderPaint);
    }

    @Override
    public void drawValues(android.graphics.Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        android.graphics.PointF center = mChart.getCenterOffsets();

        float yoffset = com.github.mikephil.charting.utils.Utils.convertDpToPixel(5f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            com.github.mikephil.charting.data.RadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                com.github.mikephil.charting.data.Entry entry = entries.get(j);

                android.graphics.PointF p = com.github.mikephil.charting.utils.Utils.getPosition(center, (entry.getVal() - mChart.getYChartMin()) * factor,
                                                                                                 sliceangle * j + mChart.getRotationAngle());

                drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, p.x, p.y - yoffset);
            }
        }
    }

    @Override
    public void drawExtras(android.graphics.Canvas c) {
        drawWeb(c);
    }

    protected void drawWeb(android.graphics.Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();

        android.graphics.PointF center = mChart.getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mChart.getWebLineWidth());
        mWebPaint.setColor(mChart.getWebColor());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        final int xIncrements = 1 + mChart.getSkipWebLineCount();

        for (int i = 0; i < mChart.getData().getXValCount(); i += xIncrements) {

            android.graphics.PointF p = com.github.mikephil.charting.utils.Utils.getPosition(center, mChart.getYRange() * factor, sliceangle * i
                    + rotationangle);

            c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
        }

        // draw the inner-web
        mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        mWebPaint.setColor(mChart.getWebColorInner());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        int labelCount = mChart.getYAxis().mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            for (int i = 0; i < mChart.getData().getXValCount(); i++) {

                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;

                android.graphics.PointF p1 = com.github.mikephil.charting.utils.Utils.getPosition(center, r, sliceangle * i + rotationangle);
                android.graphics.PointF p2 = com.github.mikephil.charting.utils.Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle);

                c.drawLine(p1.x, p1.y, p2.x, p2.y, mWebPaint);
            }
        }
    }

    @Override
    public void drawHighlighted(android.graphics.Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();
        float factor = mChart.getFactor();

        android.graphics.PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < indices.length; i++) {

            com.github.mikephil.charting.data.RadarDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            // get the index to highlight
            int xIndex = indices[i].getXIndex();

            com.github.mikephil.charting.data.Entry e = set.getEntryForXIndex(xIndex);
            if (e == null || e.getXIndex() != xIndex)
                continue;

            int j = set.getEntryPosition(e);
            float y = (e.getVal() - mChart.getYChartMin());

            if (Float.isNaN(y))
                continue;

            android.graphics.PointF p = com.github.mikephil.charting.utils.Utils.getPosition(center, y * factor,
                                                                                             sliceangle * j + mChart.getRotationAngle());

            float[] pts = new float[]{
                    p.x, p.y
            };

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }

}
