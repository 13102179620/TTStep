
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.components.XAxis;

public class XAxisRendererBarChart extends XAxisRenderer {

    protected com.github.mikephil.charting.charts.BarChart mChart;

    public XAxisRendererBarChart(com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler, XAxis xAxis, com.github.mikephil.charting.utils.Transformer trans,
                                 com.github.mikephil.charting.charts.BarChart chart) {
        super(viewPortHandler, xAxis, trans);

        this.mChart = chart;
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param pos
     */
    @Override
    protected void drawLabels(android.graphics.Canvas c, float pos, android.graphics.PointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        com.github.mikephil.charting.data.BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace()
                    + bd.getGroupSpace() / 2f;

            // consider groups (center label for each group)
            if (step > 1) {
                position[0] += ((float) step - 1f) / 2f;
            }

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0]) && i >= 0
                    && i < mXAxis.getValues().size()) {

                String label = mXAxis.getValues().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1) {
                        float width = com.github.mikephil.charting.utils.Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (position[0] + width / 2.f > mViewPortHandler.contentRight())
                            position[0] = mViewPortHandler.contentRight() - (width / 2.f);

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = com.github.mikephil.charting.utils.Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (position[0] - width / 2.f < mViewPortHandler.contentLeft())
                            position[0] = mViewPortHandler.contentLeft() + (width / 2.f);
                    }
                }

                drawLabel(c, label, i, position[0], pos, anchor, labelRotationAngleDegrees);
            }
        }
    }

    @Override
    public void renderGridLines(android.graphics.Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        float[] position = new float[] {
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());

        com.github.mikephil.charting.data.BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

        for (int i = mMinX; i < mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace() - 0.5f;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                c.drawLine(position[0], mViewPortHandler.offsetTop(), position[0],
                        mViewPortHandler.contentBottom(), mGridPaint);
            }
        }
    }
}
