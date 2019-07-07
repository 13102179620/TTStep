
package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.interfaces.ScatterDataProvider;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;

/**
 * The ScatterChart. Draws dots, triangles, squares and custom shapes into the
 * Chart-View. CIRCLE and SCQUARE offer the best performance, TRIANGLE has the
 * worst performance.
 * 
 * @author Philipp Jahoda
 */
public class ScatterChart extends BarLineChartBase<com.github.mikephil.charting.data.ScatterData> implements ScatterDataProvider {

    /**
     * enum that defines the shape that is drawn where the values are, CIRCLE
     * and SCQUARE offer the best performance, TRIANGLE has the worst
     * performance.
     */
    public enum ScatterShape {
        CROSS, TRIANGLE, CIRCLE, SQUARE
    }

    public ScatterChart(android.content.Context context) {
        super(context);
    }

    public ScatterChart(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public ScatterChart(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new ScatterChartRenderer(this, mAnimator, mViewPortHandler);
        mXChartMin = -0.5f;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        if (mDeltaX == 0 && mData.getYValCount() > 0)
            mDeltaX = 1;

        mXChartMax += 0.5f;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);
    }

    /**
     * Returns all possible predefined ScatterShapes.
     * 
     * @return
     */
    public static ScatterShape[] getAllPossibleShapes() {
        return new ScatterShape[] {
                ScatterShape.SQUARE, ScatterShape.CIRCLE, ScatterShape.TRIANGLE, ScatterShape.CROSS
        };
    }

    public com.github.mikephil.charting.data.ScatterData getScatterData() {
        return mData;
    };
}
