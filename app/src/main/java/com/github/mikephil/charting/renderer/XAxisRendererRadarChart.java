
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.components.XAxis;

public class XAxisRendererRadarChart extends XAxisRenderer {

    private com.github.mikephil.charting.charts.RadarChart mChart;

    public XAxisRendererRadarChart(com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler, XAxis xAxis, com.github.mikephil.charting.charts.RadarChart chart) {
        super(viewPortHandler, xAxis, null);

        mChart = chart;
    }

    @Override
    public void renderAxisLabels(android.graphics.Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        final android.graphics.PointF drawLabelAnchor = new android.graphics.PointF(0.5f, 0.0f);

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        android.graphics.PointF center = mChart.getCenterOffsets();

        int mod = mXAxis.mAxisLabelModulus;
        for (int i = 0; i < mXAxis.getValues().size(); i += mod) {
            String label = mXAxis.getValues().get(i);

            float angle = (sliceangle * i + mChart.getRotationAngle()) % 360f;

            android.graphics.PointF p = com.github.mikephil.charting.utils.Utils.getPosition(center, mChart.getYRange() * factor
                    + mXAxis.mLabelRotatedWidth / 2f, angle);

            drawLabel(c, label, i, p.x, p.y - mXAxis.mLabelRotatedHeight / 2.f,
                    drawLabelAnchor, labelRotationAngleDegrees);
        }
    }

	/**
	 * XAxis LimitLines on RadarChart not yet supported.
	 *
	 * @param c
	 */
	@Override
	public void renderLimitLines(android.graphics.Canvas c) {
		// this space intentionally left blank
	}
}
