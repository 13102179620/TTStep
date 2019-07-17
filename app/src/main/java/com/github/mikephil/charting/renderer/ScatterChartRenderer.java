
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.ScatterDataProvider;

public class ScatterChartRenderer extends LineScatterCandleRadarRenderer {

    protected ScatterDataProvider mChart;

    protected com.github.mikephil.charting.buffer.ScatterBuffer[] mScatterBuffers;

    public ScatterChartRenderer(ScatterDataProvider chart, ChartAnimator animator,
            com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mRenderPaint.setStrokeWidth(com.github.mikephil.charting.utils.Utils.convertDpToPixel(1f));
    }

    @Override
    public void initBuffers() {

        com.github.mikephil.charting.data.ScatterData scatterData = mChart.getScatterData();

        mScatterBuffers = new com.github.mikephil.charting.buffer.ScatterBuffer[scatterData.getDataSetCount()];

        for (int i = 0; i < mScatterBuffers.length; i++) {
            com.github.mikephil.charting.data.ScatterDataSet set = scatterData.getDataSetByIndex(i);
            mScatterBuffers[i] = new com.github.mikephil.charting.buffer.ScatterBuffer(set.getEntryCount() * 2);
        }
    }

    @Override
    public void drawData(android.graphics.Canvas c) {

        com.github.mikephil.charting.data.ScatterData scatterData = mChart.getScatterData();

        for (com.github.mikephil.charting.data.ScatterDataSet set : scatterData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(android.graphics.Canvas c, com.github.mikephil.charting.data.ScatterDataSet dataSet) {

        com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

        float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        com.github.mikephil.charting.charts.ScatterChart.ScatterShape shape = dataSet.getScatterShape();

        com.github.mikephil.charting.buffer.ScatterBuffer buffer = mScatterBuffers[mChart.getScatterData().getIndexOfDataSet(
                dataSet)];
        buffer.setPhases(phaseX, phaseY);
        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        switch (shape) {
            case SQUARE:

                mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    c.drawRect(buffer.buffer[i] - shapeHalf,
                            buffer.buffer[i + 1] - shapeHalf, buffer.buffer[i]
                                    + shapeHalf, buffer.buffer[i + 1]
                                    + shapeHalf, mRenderPaint);
                }
                break;
            case CIRCLE:

                mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    c.drawCircle(buffer.buffer[i], buffer.buffer[i + 1], shapeHalf,
                            mRenderPaint);
                }
                break;
            case TRIANGLE:

                mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);

                // create a triangle path
                android.graphics.Path tri = new android.graphics.Path();

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    tri.moveTo(buffer.buffer[i], buffer.buffer[i + 1] - shapeHalf);
                    tri.lineTo(buffer.buffer[i] + shapeHalf, buffer.buffer[i + 1] + shapeHalf);
                    tri.lineTo(buffer.buffer[i] - shapeHalf, buffer.buffer[i + 1] + shapeHalf);
                    tri.close();

                    c.drawPath(tri, mRenderPaint);
                    tri.reset();
                }
                break;
            case CROSS:

                mRenderPaint.setStyle(android.graphics.Paint.Style.STROKE);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));

                    c.drawLine(buffer.buffer[i] - shapeHalf, buffer.buffer[i + 1],
                            buffer.buffer[i] + shapeHalf,
                            buffer.buffer[i + 1], mRenderPaint);
                    c.drawLine(buffer.buffer[i], buffer.buffer[i + 1] - shapeHalf,
                            buffer.buffer[i], buffer.buffer[i + 1]
                                    + shapeHalf, mRenderPaint);
                }
                break;
            default:
                break;
        }

        // else { // draw the custom-shape
        //
        // Path customShape = dataSet.getCustomScatterShape();
        //
        // for (int j = 0; j < entries.size() * mAnimator.getPhaseX(); j += 2) {
        //
        // Entry e = entries.get(j / 2);
        //
        // if (!fitsBounds(e.getXIndex(), mMinX, mMaxX))
        // continue;
        //
        // if (customShape == null)
        // return;
        //
        // mRenderPaint.setColor(dataSet.getColor(j));
        //
        // Path newPath = new Path(customShape);
        // newPath.offset(e.getXIndex(), e.getVal());
        //
        // // transform the provided custom path
        // trans.pathValueToPixel(newPath);
        // c.drawPath(newPath, mRenderPaint);
        // }
        // }
    }

    @Override
    public void drawValues(android.graphics.Canvas c) {

        // if values are drawn
        if (mChart.getScatterData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            java.util.List<com.github.mikephil.charting.data.ScatterDataSet> dataSets = mChart.getScatterData().getDataSets();

            for (int i = 0; i < mChart.getScatterData().getDataSetCount(); i++) {

                com.github.mikephil.charting.data.ScatterDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

                float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesScatter(entries,
                                mAnimator.getPhaseY());

                float shapeSize = dataSet.getScatterShapeSize();

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    if (!mViewPortHandler.isInBoundsRight(positions[j]))
                        break;

                    // make sure the lines don't do shitty things outside bounds
                    if ((!mViewPortHandler.isInBoundsLeft(positions[j])
                            || !mViewPortHandler.isInBoundsY(positions[j + 1])))
                        continue;

                    com.github.mikephil.charting.data.Entry entry = entries.get(j / 2);

                    drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, positions[j],
                            positions[j + 1] - shapeSize);
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

            com.github.mikephil.charting.data.ScatterDataSet set = mChart.getScatterData().getDataSetByIndex(indices[i]
                    .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            int xIndex = indices[i].getXIndex(); // get the
                                                 // x-position


            if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                continue;

            final float yVal = set.getYValForXIndex(xIndex);
            if (yVal == Float.NaN)
                continue;

            float y = yVal * mAnimator.getPhaseY();

            float[] pts = new float[] {
                    xIndex, y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }
}
