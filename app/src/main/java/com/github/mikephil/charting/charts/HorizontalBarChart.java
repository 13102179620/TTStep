package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.HorizontalBarHighlighter;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.renderer.YAxisRendererHorizontalBarChart;

/**
 * BarChart with horizontal bar orientation. In this implementation, x- and y-axis are switched, meaning the YAxis class
 * represents the horizontal values and the XAxis class represents the vertical values.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChart extends BarChart {

	public HorizontalBarChart(android.content.Context context) {
		super(context);
	}

	public HorizontalBarChart(android.content.Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalBarChart(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init() {
		super.init();

		mLeftAxisTransformer = new com.github.mikephil.charting.utils.TransformerHorizontalBarChart(mViewPortHandler);
		mRightAxisTransformer = new com.github.mikephil.charting.utils.TransformerHorizontalBarChart(mViewPortHandler);

		mRenderer = new HorizontalBarChartRenderer(this, mAnimator, mViewPortHandler);
		mHighlighter = new HorizontalBarHighlighter(this);

		mAxisRendererLeft = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
		mAxisRendererRight = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisRight, mRightAxisTransformer);
		mXAxisRenderer = new XAxisRendererHorizontalBarChart(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);
	}

	@Override
	public void calculateOffsets() {

		float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;

		// setup offsets for legend
		if (mLegend != null && mLegend.isEnabled()) {

			if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART || mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART_CENTER) {

				offsetRight += Math.min(mLegend.mNeededWidth, mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent())
						+ mLegend.getXOffset() * 2f;

			} else if (mLegend.getPosition() == LegendPosition.LEFT_OF_CHART
					|| mLegend.getPosition() == LegendPosition.LEFT_OF_CHART_CENTER) {

				offsetLeft += Math.min(mLegend.mNeededWidth, mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent())
						+ mLegend.getXOffset() * 2f;

			} else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
					|| mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
					|| mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

				// It's possible that we do not need this offset anymore as it
				//   is available through the extraOffsets, but changing it can mean
				//   changing default visibility for existing apps.
				float yOffset = mLegend.mTextHeightMax * 2.f;

				offsetBottom += Math.min(mLegend.mNeededHeight + yOffset, mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());

			} else if (mLegend.getPosition() == LegendPosition.ABOVE_CHART_LEFT
					|| mLegend.getPosition() == LegendPosition.ABOVE_CHART_RIGHT
					|| mLegend.getPosition() == LegendPosition.ABOVE_CHART_CENTER) {

				// It's possible that we do not need this offset anymore as it
				//   is available through the extraOffsets, but changing it can mean
				//   changing default visibility for existing apps.
				float yOffset = mLegend.mTextHeightMax * 2.f;

				offsetTop += Math.min(mLegend.mNeededHeight + yOffset, mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());
			}
		}

		// offsets for y-labels
		if (mAxisLeft.needsOffset()) {
			offsetTop += mAxisLeft.getRequiredHeightSpace(mAxisRendererLeft.getPaintAxisLabels());
		}

		if (mAxisRight.needsOffset()) {
			offsetBottom += mAxisRight.getRequiredHeightSpace(mAxisRendererRight.getPaintAxisLabels());
		}

		float xlabelwidth = mXAxis.mLabelRotatedWidth;

		if (mXAxis.isEnabled()) {

			// offsets for x-labels
			if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

				offsetLeft += xlabelwidth;

			} else if (mXAxis.getPosition() == XAxisPosition.TOP) {

				offsetRight += xlabelwidth;

			} else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {

				offsetLeft += xlabelwidth;
				offsetRight += xlabelwidth;
			}
		}

		offsetTop += getExtraTopOffset();
		offsetRight += getExtraRightOffset();
		offsetBottom += getExtraBottomOffset();
		offsetLeft += getExtraLeftOffset();

		float minOffset = com.github.mikephil.charting.utils.Utils.convertDpToPixel(mMinOffset);

		mViewPortHandler.restrainViewPort(
				Math.max(minOffset, offsetLeft),
				Math.max(minOffset, offsetTop),
				Math.max(minOffset, offsetRight),
				Math.max(minOffset, offsetBottom));

		if (mLogEnabled) {
			android.util.Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " + offsetRight + ", offsetBottom: "
					+ offsetBottom);
			android.util.Log.i(LOG_TAG, "Content: " + mViewPortHandler.getContentRect().toString());
		}

		prepareOffsetMatrix();
		prepareValuePxMatrix();
	}

	@Override
	protected void prepareValuePxMatrix() {
		mRightAxisTransformer.prepareMatrixValuePx(mAxisRight.mAxisMinimum, mAxisRight.mAxisRange, mDeltaX, mXChartMin);
		mLeftAxisTransformer.prepareMatrixValuePx(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisRange, mDeltaX, mXChartMin);
	}

	@Override
	protected void calcModulus() {
		float[] values = new float[9];
		mViewPortHandler.getMatrixTouch().getValues(values);

		mXAxis.mAxisLabelModulus =
				(int) Math.ceil((mData.getXValCount() * mXAxis.mLabelRotatedHeight)
				/ (mViewPortHandler.contentHeight() * values[android.graphics.Matrix.MSCALE_Y]));

		if (mXAxis.mAxisLabelModulus < 1)
			mXAxis.mAxisLabelModulus = 1;
	}

	@Override
	public android.graphics.RectF getBarBounds(com.github.mikephil.charting.data.BarEntry e) {

		com.github.mikephil.charting.data.BarDataSet set = mData.getDataSetForEntry(e);

		if (set == null)
			return null;

		float barspace = set.getBarSpace();
		float y = e.getVal();
		float x = e.getXIndex();

		float spaceHalf = barspace / 2f;

		float top = x - 0.5f + spaceHalf;
		float bottom = x + 0.5f - spaceHalf;
		float left = y >= 0 ? y : 0;
		float right = y <= 0 ? y : 0;

		android.graphics.RectF bounds = new android.graphics.RectF(left, top, right, bottom);

		getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);

		return bounds;
	}

	@Override
	public android.graphics.PointF getPosition(com.github.mikephil.charting.data.Entry e, AxisDependency axis) {

		if (e == null)
			return null;

		float[] vals = new float[] { e.getVal(), e.getXIndex() };

		getTransformer(axis).pointValuesToPixel(vals);

		return new android.graphics.PointF(vals[0], vals[1]);
	}

	/**
	 * Returns the Highlight object (contains x-index and DataSet index) of the selected value at the given touch point
	 * inside the BarChart.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public Highlight getHighlightByTouchPoint(float x, float y) {

		if (mDataNotSet || mData == null) {
			android.util.Log.e(LOG_TAG, "Can't select by touch. No data set.");
			return null;
		} else
			return mHighlighter.getHighlight(y, x); // switch x and y
	}

	/**
	 * Returns the lowest x-index (value on the x-axis) that is still visible on the chart.
	 * 
	 * @return
	 */
	@Override
	public int getLowestVisibleXIndex() {

		float step = mData.getDataSetCount();
		float div = (step <= 1) ? 1 : step + mData.getGroupSpace();

		float[] pts = new float[] { mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom() };

		getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
		return (int) (((pts[1] <= 0) ? 0 : ((pts[1])) / div) + 1);
	}

	/**
	 * Returns the highest x-index (value on the x-axis) that is still visible on the chart.
	 * 
	 * @return
	 */
	@Override
	public int getHighestVisibleXIndex() {

		float step = mData.getDataSetCount();
		float div = (step <= 1) ? 1 : step + mData.getGroupSpace();

		float[] pts = new float[] { mViewPortHandler.contentLeft(), mViewPortHandler.contentTop() };

		getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
		return (int) ((pts[1] >= getXChartMax()) ? getXChartMax() / div : (pts[1] / div));
	}
}
