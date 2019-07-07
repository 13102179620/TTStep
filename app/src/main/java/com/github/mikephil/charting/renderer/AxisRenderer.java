
package com.github.mikephil.charting.renderer;

/**
 * Baseclass of all axis renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class AxisRenderer extends Renderer {

    protected com.github.mikephil.charting.utils.Transformer mTrans;

    /** paint object for the grid lines */
    protected android.graphics.Paint mGridPaint;

    /** paint for the x-label values */
    protected android.graphics.Paint mAxisLabelPaint;

    /** paint for the line surrounding the chart */
    protected android.graphics.Paint mAxisLinePaint;

	/** paint used for the limit lines */
	protected android.graphics.Paint mLimitLinePaint;

	public AxisRenderer(com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler, com.github.mikephil.charting.utils.Transformer trans) {
        super(viewPortHandler);

        this.mTrans = trans;

        mAxisLabelPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        mGridPaint = new android.graphics.Paint();
        mGridPaint.setColor(android.graphics.Color.GRAY);
        mGridPaint.setStrokeWidth(1f);
        mGridPaint.setStyle(android.graphics.Paint.Style.STROKE);
        mGridPaint.setAlpha(90);

        mAxisLinePaint = new android.graphics.Paint();
        mAxisLinePaint.setColor(android.graphics.Color.BLACK);
        mAxisLinePaint.setStrokeWidth(1f);
        mAxisLinePaint.setStyle(android.graphics.Paint.Style.STROKE);

		mLimitLinePaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
		mLimitLinePaint.setStyle(android.graphics.Paint.Style.STROKE);
	}

    /**
     * Returns the Paint object used for drawing the axis (labels).
     * 
     * @return
     */
    public android.graphics.Paint getPaintAxisLabels() {
        return mAxisLabelPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the grid-lines of the
     * axis.
     * 
     * @return
     */
    public android.graphics.Paint getPaintGrid() {
        return mGridPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the axis-line that goes
     * alongside the axis.
     * 
     * @return
     */
    public android.graphics.Paint getPaintAxisLine() {
        return mAxisLinePaint;
    }

    /**
     * Returns the Transformer object used for transforming the axis values.
     * 
     * @return
     */
    public com.github.mikephil.charting.utils.Transformer getTransformer() {
        return mTrans;
    }

    /**
     * Draws the axis labels to the screen.
     * 
     * @param c
     */
    public abstract void renderAxisLabels(android.graphics.Canvas c);

    /**
     * Draws the grid lines belonging to the axis.
     * 
     * @param c
     */
    public abstract void renderGridLines(android.graphics.Canvas c);

    /**
     * Draws the line that goes alongside the axis.
     * 
     * @param c
     */
    public abstract void renderAxisLine(android.graphics.Canvas c);

	/**
	 * Draws the LimitLines associated with this axis to the screen.
	 *
	 * @param c
	 */
	public abstract void renderLimitLines(android.graphics.Canvas c);
}
