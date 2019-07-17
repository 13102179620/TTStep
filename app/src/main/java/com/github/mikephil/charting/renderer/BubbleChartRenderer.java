
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.BubbleDataProvider;

/**
 * Bubble chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed
 * under Apache License 2.0 Ported by Daniel Cohen Gindi
 */
public class BubbleChartRenderer extends DataRenderer {

    protected BubbleDataProvider mChart;

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator,
            com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);

        mHighlightPaint.setStyle(android.graphics.Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(com.github.mikephil.charting.utils.Utils.convertDpToPixel(1.5f));
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(android.graphics.Canvas c) {

        com.github.mikephil.charting.data.BubbleData bubbleData = mChart.getBubbleData();

        for (com.github.mikephil.charting.data.BubbleDataSet set : bubbleData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    private float[] sizeBuffer = new float[4];
    private float[] pointBuffer = new float[2];

    protected float getShapeSize(float entrySize, float maxSize, float reference) {
        final float factor = (maxSize == 0f) ? 1f : (float) Math.sqrt(entrySize / maxSize);
        final float shapeSize = reference * factor;
        return shapeSize;
    }

    protected void drawDataSet(android.graphics.Canvas c, com.github.mikephil.charting.data.BubbleDataSet dataSet) {

        com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        java.util.List<com.github.mikephil.charting.data.BubbleEntry> entries = dataSet.getYVals();

        com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

        sizeBuffer[0] = 0f;
        sizeBuffer[2] = 1f;

        trans.pointValuesToPixel(sizeBuffer);

        // calcualte the full width of 1 step on the x-axis
        final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
        final float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
        final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

        for (int j = minx; j < maxx; j++) {

            final com.github.mikephil.charting.data.BubbleEntry entry = entries.get(j);

            pointBuffer[0] = (float) (entry.getXIndex() - minx) * phaseX + (float) minx;
            pointBuffer[1] = (float) (entry.getVal()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize) / 2f;

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf))
                break;

            final int color = dataSet.getColor(entry.getXIndex());

            mRenderPaint.setColor(color);
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, mRenderPaint);
        }
    }

    @Override
    public void drawValues(android.graphics.Canvas c) {

        com.github.mikephil.charting.data.BubbleData bubbleData = mChart.getBubbleData();

        if (bubbleData == null)
            return;

        // if values are drawn
        if (bubbleData.getYValCount() < (int) (Math.ceil((float) (mChart.getMaxVisibleCount())
                * mViewPortHandler.getScaleX()))) {

            final java.util.List<com.github.mikephil.charting.data.BubbleDataSet> dataSets = bubbleData.getDataSets();

            float lineHeight = com.github.mikephil.charting.utils.Utils.calcTextHeight(mValuePaint, "1");

            for (int i = 0; i < dataSets.size(); i++) {

                com.github.mikephil.charting.data.BubbleDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                final float phaseX = mAnimator.getPhaseX();
                final float phaseY = mAnimator.getPhaseY();

                final float alpha = phaseX == 1 ? phaseY : phaseX;
                int valueTextColor = dataSet.getValueTextColor();
                valueTextColor = android.graphics.Color.argb(Math.round(255.f * alpha), android.graphics.Color.red(valueTextColor),
                                                             android.graphics.Color.green(valueTextColor), android.graphics.Color.blue(valueTextColor));

                mValuePaint.setColor(valueTextColor);

                final java.util.List<com.github.mikephil.charting.data.BubbleEntry> entries = dataSet.getYVals();

                com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
                com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int minx = dataSet.getEntryPosition(entryFrom);
                int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, dataSet.getEntryCount());

                final float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesBubble(entries, phaseX, phaseY, minx, maxx);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if ((!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)))
                        continue;

                    com.github.mikephil.charting.data.BubbleEntry entry = entries.get(j / 2 + minx);

                    drawValue(c, dataSet.getValueFormatter(), entry.getSize(), entry, i, x,
                            y + (0.5f * lineHeight));
                }
            }
        }

    }

    @Override
    public void drawExtras(android.graphics.Canvas c) {
    }

    private float[] _hsvBuffer = new float[3];

    @Override
    public void drawHighlighted(android.graphics.Canvas c, Highlight[] indices) {

        com.github.mikephil.charting.data.BubbleData bubbleData = mChart.getBubbleData();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        for (Highlight indice : indices) {

            com.github.mikephil.charting.data.BubbleDataSet dataSet = bubbleData.getDataSetByIndex(indice.getDataSetIndex());

            if (dataSet == null || !dataSet.isHighlightEnabled())
                continue;

            com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
            com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

            int minx = dataSet.getEntryPosition(entryFrom);
            int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, dataSet.getEntryCount());

            final com.github.mikephil.charting.data.BubbleEntry entry = (com.github.mikephil.charting.data.BubbleEntry) bubbleData.getEntryForHighlight(indice);
            if (entry == null || entry.getXIndex() != indice.getXIndex())
                continue;

            com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            
            sizeBuffer[0] = 0f;
            sizeBuffer[2] = 1f;

            trans.pointValuesToPixel(sizeBuffer);
            
            // calcualte the full width of 1 step on the x-axis
            final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
            final float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
            final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

            pointBuffer[0] = (float) (entry.getXIndex() - minx) * phaseX + (float) minx;
            pointBuffer[1] = (float) (entry.getVal()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize) / 2f;

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf))
                break;

            if (indice.getXIndex() < minx || indice.getXIndex() >= maxx)
                continue;

            final int originalColor = dataSet.getColor(entry.getXIndex());

            android.graphics.Color.RGBToHSV(android.graphics.Color.red(originalColor), android.graphics.Color.green(originalColor),
                                            android.graphics.Color.blue(originalColor), _hsvBuffer);
            _hsvBuffer[2] *= 0.5f;
            final int color = android.graphics.Color.HSVToColor(android.graphics.Color.alpha(originalColor), _hsvBuffer);

            mHighlightPaint.setColor(color);
            mHighlightPaint.setStrokeWidth(dataSet.getHighlightCircleWidth());
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, mHighlightPaint);
        }
    }
}
