
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Superclass of all render classes for the different data types (line, bar, ...).
 *
 * @author Philipp Jahoda
 */
public abstract class DataRenderer extends Renderer {

    /**
     * the animator object used to perform animations on the chart data
     */
    protected ChartAnimator mAnimator;

    /**
     * main paint object used for rendering
     */
    protected android.graphics.Paint mRenderPaint;

    /**
     * paint used for highlighting values
     */
    protected android.graphics.Paint mHighlightPaint;

    protected android.graphics.Paint mDrawPaint;

    /**
     * paint object for drawing values (text representing values of chart
     * entries)
     */
    protected android.graphics.Paint mValuePaint;

    public DataRenderer(ChartAnimator animator, com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
        this.mAnimator = animator;

        mRenderPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);

        mDrawPaint = new android.graphics.Paint(android.graphics.Paint.DITHER_FLAG);

        mValuePaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(android.graphics.Color.rgb(63, 63, 63));
        mValuePaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        mValuePaint.setTextSize(com.github.mikephil.charting.utils.Utils.convertDpToPixel(9f));

        mHighlightPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(android.graphics.Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(android.graphics.Color.rgb(255, 187, 115));
    }

    /**
     * Returns the Paint object this renderer uses for drawing the values
     * (value-text).
     *
     * @return
     */
    public android.graphics.Paint getPaintValues() {
        return mValuePaint;
    }

    /**
     * Returns the Paint object this renderer uses for drawing highlight
     * indicators.
     *
     * @return
     */
    public android.graphics.Paint getPaintHighlight() {
        return mHighlightPaint;
    }

    /**
     * Returns the Paint object used for rendering.
     *
     * @return
     */
    public android.graphics.Paint getPaintRender() {
        return mRenderPaint;
    }

    /**
     * Applies the required styling (provided by the DataSet) to the value-paint
     * object.
     *
     * @param set
     */
    protected void applyValueTextStyle(com.github.mikephil.charting.data.DataSet<?> set) {

        mValuePaint.setColor(set.getValueTextColor());
        mValuePaint.setTypeface(set.getValueTypeface());
        mValuePaint.setTextSize(set.getValueTextSize());
    }

    /**
     * Initializes the buffers used for rendering with a new size. Since this
     * method performs memory allocations, it should only be called if
     * necessary.
     */
    public abstract void initBuffers();

    /**
     * Draws the actual data in form of lines, bars, ... depending on Renderer subclass.
     *
     * @param c
     */
    public abstract void drawData(android.graphics.Canvas c);

    /**
     * Loops over all Entrys and draws their values.
     *
     * @param c
     */
    public abstract void drawValues(android.graphics.Canvas c);

    /**
     * Draws the value of the given entry by using the provided ValueFormatter.
     *
     * @param c            canvas
     * @param formatter    formatter for custom value-formatting
     * @param value        the value to be drawn
     * @param entry        the entry the value belongs to
     * @param dataSetIndex the index of the DataSet the drawn Entry belongs to
     * @param x            position
     * @param y            position
     */
    public void drawValue(android.graphics.Canvas c, ValueFormatter formatter, float value, com.github.mikephil.charting.data.Entry entry, int dataSetIndex, float x, float y) {
        c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y, mValuePaint);
    }

    /**
     * Draws any kind of additional information (e.g. line-circles).
     *
     * @param c
     */
    public abstract void drawExtras(android.graphics.Canvas c);

    /**
     * Draws all highlight indicators for the values that are currently highlighted.
     *
     * @param c
     * @param indices the highlighted values
     */
    public abstract void drawHighlighted(android.graphics.Canvas c, Highlight[] indices);
}
