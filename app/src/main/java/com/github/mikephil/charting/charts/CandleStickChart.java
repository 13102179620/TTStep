
package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 * 
 * @author Philipp Jahoda
 */
public class CandleStickChart extends BarLineChartBase<com.github.mikephil.charting.data.CandleData> implements CandleDataProvider {

    public CandleStickChart(android.content.Context context) {
        super(context);
    }

    public CandleStickChart(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleStickChart(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new CandleStickChartRenderer(this, mAnimator, mViewPortHandler);
        mXChartMin = -0.5f;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        mXChartMax += 0.5f;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);
    }

    @Override
    public com.github.mikephil.charting.data.CandleData getCandleData() {
        return mData;
    }
}
