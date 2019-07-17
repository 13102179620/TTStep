package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;

/**
 * Created by Philipp Jahoda on 11/07/15.
 */
public abstract class LineScatterCandleRadarRenderer extends DataRenderer {

    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    private android.graphics.Path mHighlightLinePath = new android.graphics.Path();

    public LineScatterCandleRadarRenderer(ChartAnimator animator, com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param c
     * @param pts the transformed x- and y-position of the lines
     * @param set the currently drawn dataset
     */
    protected void drawHighlightLines(android.graphics.Canvas c, float[] pts, com.github.mikephil.charting.data.LineScatterCandleRadarDataSet set) {

        // set color and stroke-width
        mHighlightPaint.setColor(set.getHighLightColor());
        mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {

            // create vertical path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(pts[0], mViewPortHandler.contentTop());
            mHighlightLinePath.lineTo(pts[0], mViewPortHandler.contentBottom());

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {

            // create horizontal path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }
    }
}
