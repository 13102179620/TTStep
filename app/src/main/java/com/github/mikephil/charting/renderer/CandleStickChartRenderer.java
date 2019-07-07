
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.CandleDataProvider;

public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {

    protected CandleDataProvider mChart;

    private com.github.mikephil.charting.buffer.CandleShadowBuffer[] mShadowBuffers;
    private com.github.mikephil.charting.buffer.CandleBodyBuffer[] mBodyBuffers;

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator,
            com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
    }

    @Override
    public void initBuffers() {
        com.github.mikephil.charting.data.CandleData candleData = mChart.getCandleData();
        mShadowBuffers = new com.github.mikephil.charting.buffer.CandleShadowBuffer[candleData.getDataSetCount()];
        mBodyBuffers = new com.github.mikephil.charting.buffer.CandleBodyBuffer[candleData.getDataSetCount()];

        for (int i = 0; i < mShadowBuffers.length; i++) {
            com.github.mikephil.charting.data.CandleDataSet set = candleData.getDataSetByIndex(i);
            mShadowBuffers[i] = new com.github.mikephil.charting.buffer.CandleShadowBuffer(set.getValueCount() * 4);
            mBodyBuffers[i] = new com.github.mikephil.charting.buffer.CandleBodyBuffer(set.getValueCount() * 4);
        }
    }

    @Override
    public void drawData(android.graphics.Canvas c) {

        com.github.mikephil.charting.data.CandleData candleData = mChart.getCandleData();

        for (com.github.mikephil.charting.data.CandleDataSet set : candleData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(android.graphics.Canvas c, com.github.mikephil.charting.data.CandleDataSet dataSet) {

        com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        int dataSetIndex = mChart.getCandleData().getIndexOfDataSet(dataSet);

        java.util.List<com.github.mikephil.charting.data.CandleEntry> entries = dataSet.getYVals();

        int minx = Math.max(mMinX, 0);
        int maxx = Math.min(mMaxX + 1, entries.size());

        int range = (maxx - minx) * 4;
        int to = (int)Math.ceil((maxx - minx) * phaseX + minx);

        com.github.mikephil.charting.buffer.CandleBodyBuffer bodyBuffer = mBodyBuffers[dataSetIndex];
        bodyBuffer.setBodySpace(dataSet.getBodySpace());
        bodyBuffer.setPhases(phaseX, phaseY);
        bodyBuffer.limitFrom(minx);
        bodyBuffer.limitTo(maxx);
        bodyBuffer.feed(entries);

        trans.pointValuesToPixel(bodyBuffer.buffer);

        com.github.mikephil.charting.buffer.CandleShadowBuffer shadowBuffer = mShadowBuffers[dataSetIndex];
        shadowBuffer.setPhases(phaseX, phaseY);
        shadowBuffer.limitFrom(minx);
        shadowBuffer.limitTo(maxx);
        shadowBuffer.feed(entries);

        trans.pointValuesToPixel(shadowBuffer.buffer);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = 0; j < range; j += 4) {

            // get the entry
            com.github.mikephil.charting.data.CandleEntry e = entries.get(j / 4 + minx);

            if (!fitsBounds(e.getXIndex(), mMinX, to))
                continue;

            if (dataSet.getShadowColorSameAsCandle()) {

                if (e.getOpen() > e.getClose())
                    mRenderPaint.setColor(
                            dataSet.getDecreasingColor() == com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getDecreasingColor()
                    );

                else if (e.getOpen() < e.getClose())
                    mRenderPaint.setColor(
                            dataSet.getIncreasingColor() == com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getIncreasingColor()
                    );

                else
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );

            } else {
                mRenderPaint.setColor(
                        dataSet.getShadowColor() == com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE ?
                                dataSet.getColor(j) :
                                dataSet.getShadowColor()
                );
            }

            mRenderPaint.setStyle(android.graphics.Paint.Style.STROKE);

            // draw the shadow
            c.drawLine(shadowBuffer.buffer[j], shadowBuffer.buffer[j + 1],
                    shadowBuffer.buffer[j + 2], shadowBuffer.buffer[j + 3],
                    mRenderPaint);

            float leftBody = bodyBuffer.buffer[j];
            float open = bodyBuffer.buffer[j + 1];
            float rightBody = bodyBuffer.buffer[j + 2];
            float close = bodyBuffer.buffer[j + 3];

            // draw body differently for increasing and decreasing entry
            if (open > close) { // decreasing

                if (dataSet.getDecreasingColor() == com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));
                } else {
                    mRenderPaint.setColor(dataSet.getDecreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                // draw the body
                c.drawRect(leftBody, close, rightBody, open, mRenderPaint);

            } else if(open < close) {

                if (dataSet.getIncreasingColor() == com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));
                } else {
                    mRenderPaint.setColor(dataSet.getIncreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                // draw the body
                c.drawRect(leftBody, open, rightBody, close, mRenderPaint);
            } else { // equal values
                
                mRenderPaint.setColor(dataSet.getShadowColor());
                c.drawLine(leftBody, open, rightBody, close, mRenderPaint);
            }
        }
    }

    // /**
    // * Transforms the values of an entry in order to draw the candle-body.
    // *
    // * @param bodyPoints
    // * @param e
    // * @param bodySpace
    // */
    // private void transformBody(float[] bodyPoints, CandleEntry e, float
    // bodySpace, Transformer trans) {
    //
    // float phase = mAnimator.getPhaseY();
    //
    // bodyPoints[0] = e.getXIndex() - 0.5f + bodySpace;
    // bodyPoints[1] = e.getClose() * phase;
    // bodyPoints[2] = e.getXIndex() + 0.5f - bodySpace;
    // bodyPoints[3] = e.getOpen() * phase;
    //
    // trans.pointValuesToPixel(bodyPoints);
    // }
    //
    // /**
    // * Transforms the values of an entry in order to draw the candle-shadow.
    // *
    // * @param shadowPoints
    // * @param e
    // */
    // private void transformShadow(float[] shadowPoints, CandleEntry e,
    // Transformer trans) {
    //
    // float phase = mAnimator.getPhaseY();
    //
    // shadowPoints[0] = e.getXIndex();
    // shadowPoints[1] = e.getHigh() * phase;
    // shadowPoints[2] = e.getXIndex();
    // shadowPoints[3] = e.getLow() * phase;
    //
    // trans.pointValuesToPixel(shadowPoints);
    // }

    @Override
    public void drawValues(android.graphics.Canvas c) {

        // if values are drawn
        if (mChart.getCandleData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            java.util.List<com.github.mikephil.charting.data.CandleDataSet> dataSets = mChart.getCandleData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                com.github.mikephil.charting.data.CandleDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                java.util.List<com.github.mikephil.charting.data.CandleEntry> entries = dataSet.getYVals();

                int minx = Math.max(mMinX, 0);
                int maxx = Math.min(mMaxX + 1, entries.size());

                float[] positions = trans.generateTransformedValuesCandle(
                        entries, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

                float yOffset = com.github.mikephil.charting.utils.Utils.convertDpToPixel(5f);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    com.github.mikephil.charting.data.CandleEntry entry = entries.get(j / 2 + minx);

                    drawValue(c, dataSet.getValueFormatter(), entry.getHigh(), entry, i, x, y - yOffset);
                }
            }
        }
    }

    @Override
    public void drawExtras(android.graphics.Canvas c) {
    }

    @Override
    public void drawHighlighted(android.graphics.Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            int xIndex = indices[i].getXIndex(); // get the
                                                 // x-position

            com.github.mikephil.charting.data.CandleDataSet set = mChart.getCandleData().getDataSetByIndex(
                    indices[i].getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            com.github.mikephil.charting.data.CandleEntry e = set.getEntryForXIndex(xIndex);

            if (e == null || e.getXIndex() != xIndex)
                continue;

            float low = e.getLow() * mAnimator.getPhaseY();
            float high = e.getHigh() * mAnimator.getPhaseY();
            float y = (low + high) / 2f;

            float min = mChart.getYChartMin();
            float max = mChart.getYChartMax();


            float[] pts = new float[] {
                    xIndex, y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }

}
