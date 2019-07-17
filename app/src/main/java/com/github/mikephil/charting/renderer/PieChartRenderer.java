
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;

public class PieChartRenderer extends DataRenderer {

    protected com.github.mikephil.charting.charts.PieChart mChart;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    protected android.graphics.Paint mHolePaint;
    protected android.graphics.Paint mTransparentCirclePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private android.text.TextPaint mCenterTextPaint;

    private android.text.StaticLayout mCenterTextLayout;
    private android.text.SpannableString mCenterTextLastValue;
    private android.graphics.RectF mCenterTextLastBounds = new android.graphics.RectF();
    private android.graphics.RectF[] mRectBuffer = {new android.graphics.RectF(), new android.graphics.RectF(), new android.graphics.RectF()};

    /**
     * Bitmap for drawing the center hole
     */
    protected android.graphics.Bitmap mDrawBitmap;

    protected android.graphics.Canvas mBitmapCanvas;

    public PieChartRenderer(com.github.mikephil.charting.charts.PieChart chart, ChartAnimator animator,
                            com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHolePaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(android.graphics.Color.WHITE);
        mHolePaint.setStyle(android.graphics.Paint.Style.FILL);

        mTransparentCirclePaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mTransparentCirclePaint.setColor(android.graphics.Color.WHITE);
        mTransparentCirclePaint.setStyle(android.graphics.Paint.Style.FILL);
        mTransparentCirclePaint.setAlpha(105);

        mCenterTextPaint = new android.text.TextPaint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(android.graphics.Color.BLACK);
        mCenterTextPaint.setTextSize(com.github.mikephil.charting.utils.Utils.convertDpToPixel(12f));
        //mCenterTextPaint.setTextAlign(Align.CENTER);

        mValuePaint.setTextSize(com.github.mikephil.charting.utils.Utils.convertDpToPixel(13f));
        mValuePaint.setColor(android.graphics.Color.WHITE);
        mValuePaint.setTextAlign(android.graphics.Paint.Align.CENTER);
    }

    public android.graphics.Paint getPaintHole() {
        return mHolePaint;
    }

    public android.graphics.Paint getPaintTransparentCircle() {
        return mTransparentCirclePaint;
    }

    public android.text.TextPaint getPaintCenterText() {
        return mCenterTextPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub
    }

    @Override
    public void drawData(android.graphics.Canvas c) {

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        if (mDrawBitmap == null
                || (mDrawBitmap.getWidth() != width)
                || (mDrawBitmap.getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_4444);
                mBitmapCanvas = new android.graphics.Canvas(mDrawBitmap);
            } else
                return;
        }

        mDrawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);

        com.github.mikephil.charting.data.PieData pieData = mChart.getData();

        for (com.github.mikephil.charting.data.PieDataSet set : pieData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(android.graphics.Canvas c, com.github.mikephil.charting.data.PieDataSet dataSet) {

        float angle = mChart.getRotationAngle();

        java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();
        float[] drawAngles = mChart.getDrawAngles();

        for (int j = 0; j < entries.size(); j++) {

            float newangle = drawAngles[j];
            float sliceSpace = dataSet.getSliceSpace();

            com.github.mikephil.charting.data.Entry e = entries.get(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                if (!mChart.needsHighlight(e.getXIndex(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    mRenderPaint.setColor(dataSet.getColor(j));
                    mBitmapCanvas.drawArc(mChart.getCircleBox(),
                            (angle + sliceSpace / 2f) * mAnimator.getPhaseY(),
                            (newangle - sliceSpace / 2f) * mAnimator.getPhaseY(),
                            true, mRenderPaint);
                }
            }

            angle += newangle * mAnimator.getPhaseX();
        }
    }

    @Override
    public void drawValues(android.graphics.Canvas c) {

        android.graphics.PointF center = mChart.getCenterCircleBox();

        // get whole the radius
        float r = mChart.getRadius();
        float rotationAngle = mChart.getRotationAngle();
        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        float off = r / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            off = (r - (r / 100f * mChart.getHoleRadius())) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        com.github.mikephil.charting.data.PieData data = mChart.getData();
        java.util.List<com.github.mikephil.charting.data.PieDataSet> dataSets = data.getDataSets();
        boolean drawXVals = mChart.isDrawSliceTextEnabled();

        int cnt = 0;

        for (int i = 0; i < dataSets.size(); i++) {

            com.github.mikephil.charting.data.PieDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled() && !drawXVals)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            float lineHeight = com.github.mikephil.charting.utils.Utils.calcTextHeight(mValuePaint, "Q")
                    + com.github.mikephil.charting.utils.Utils.convertDpToPixel(4f);

            java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

            for (int j = 0, maxEntry = Math.min(
                    (int) Math.ceil(entries.size() * mAnimator.getPhaseX()), entries.size()); j < maxEntry; j++) {

                com.github.mikephil.charting.data.Entry entry = entries.get(j);

                // offset needed to center the drawn text in the slice
                float offset = drawAngles[cnt] / 2;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians((rotationAngle + absoluteAngles[cnt] - offset)
                        * mAnimator.getPhaseY())) + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians((rotationAngle + absoluteAngles[cnt] - offset)
                        * mAnimator.getPhaseY())) + center.y);

                float value = mChart.isUsePercentValuesEnabled() ? entry.getVal()
                        / data.getYValueSum() * 100f : entry.getVal();

                ValueFormatter formatter = dataSet.getValueFormatter();

                boolean drawYVals = dataSet.isDrawValuesEnabled();

                // draw everything, depending on settings
                if (drawXVals && drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y);

                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);

                } else if (drawXVals && !drawYVals) {
                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight / 2f, mValuePaint);
                } else if (!drawXVals && drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y + lineHeight / 2f);
                }

                cnt++;
            }
        }
    }

    @Override
    public void drawExtras(android.graphics.Canvas c) {
        // drawCircles(c);
        drawHole(c);
        c.drawBitmap(mDrawBitmap, 0, 0, mRenderPaint);
        drawCenterText(c);
    }

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    protected void drawHole(android.graphics.Canvas c) {

        if (mChart.isDrawHoleEnabled()) {

            float transparentCircleRadius = mChart.getTransparentCircleRadius();
            float holeRadius = mChart.getHoleRadius();
            float radius = mChart.getRadius();

            android.graphics.PointF center = mChart.getCenterCircleBox();

            // only draw the circle if it can be seen (not covered by the hole)
            if (transparentCircleRadius > holeRadius) {

                // get original alpha
                int alpha = mTransparentCirclePaint.getAlpha();
                mTransparentCirclePaint.setAlpha((int) ((float) alpha * mAnimator.getPhaseX() * mAnimator.getPhaseY()));

                // draw the transparent-circle
                mBitmapCanvas.drawCircle(center.x, center.y,
                        radius / 100 * transparentCircleRadius, mTransparentCirclePaint);

                // reset alpha
                mTransparentCirclePaint.setAlpha(alpha);
            }

            // draw the hole-circle
            mBitmapCanvas.drawCircle(center.x, center.y,
                    radius / 100 * holeRadius, mHolePaint);
        }
    }

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    protected void drawCenterText(android.graphics.Canvas c) {

        android.text.SpannableString centerText = mChart.getCenterText();

        if (mChart.isDrawCenterTextEnabled() && centerText != null) {

            android.graphics.PointF center = mChart.getCenterCircleBox();

            float innerRadius = mChart.isDrawHoleEnabled() && mChart.isHoleTransparent() ? mChart.getRadius() * (mChart.getHoleRadius() / 100f) : mChart.getRadius();

            android.graphics.RectF holeRect = mRectBuffer[0];
            holeRect.left = center.x - innerRadius;
            holeRect.top = center.y - innerRadius;
            holeRect.right = center.x + innerRadius;
            holeRect.bottom = center.y + innerRadius;
            android.graphics.RectF boundingRect = mRectBuffer[1];
            boundingRect.set(holeRect);

            float radiusPercent = mChart.getCenterTextRadiusPercent();
            if (radiusPercent > 0.0) {
                boundingRect.inset((boundingRect.width() - boundingRect.width() * radiusPercent) / 2.f,
                        (boundingRect.height() - boundingRect.height() * radiusPercent) / 2.f);
            }

            if (!centerText.equals(mCenterTextLastValue) || !boundingRect.equals(mCenterTextLastBounds)) {

                // Next time we won't recalculate StaticLayout...
                mCenterTextLastBounds.set(boundingRect);
                mCenterTextLastValue = centerText;

                float width = mCenterTextLastBounds.width();

                // If width is 0, it will crash. Always have a minimum of 1
                mCenterTextLayout = new android.text.StaticLayout(centerText, 0, centerText.length(),
                                                                  mCenterTextPaint,
                                                                  (int) Math.max(Math.ceil(width), 1.f),
                                                                  android.text.Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, false);
            }

            // I wish we could make an ellipse clipping path on Android to clip to the hole...
            // If we ever find out how, this is the place to add it, based on holeRect

            //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
            float layoutHeight = mCenterTextLayout.getHeight();

            c.save();
            c.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layoutHeight) / 2.f);
            mCenterTextLayout.draw(c);
            c.restore();

//            }
//
//        else {
//
//
//                // get all lines from the text
//                String[] lines = centerText.toString().split("\n");
//
//                float maxlineheight = 0f;
//
//                // calc the maximum line height
//                for (String line : lines) {
//                    float curHeight = Utils.calcTextHeight(mCenterTextPaint, line);
//                    if (curHeight > maxlineheight)
//                        maxlineheight = curHeight;
//                }
//
//                float linespacing = maxlineheight * 0.25f;
//
//                float totalheight = maxlineheight * lines.length - linespacing * (lines.length - 1);
//
//                int cnt = lines.length;
//
//                float y = center.y;
//
//                for (int i = 0; i < lines.length; i++) {
//
//                    String line = lines[lines.length - i - 1];
//
//
//
//                    c.drawText(line, center.x, y
//                                    + maxlineheight * cnt - totalheight / 2f,
//                            mCenterTextPaint);
//                    cnt--;
//                    y -= linespacing;
//                }
//            }
        }
    }

    @Override
    public void drawHighlighted(android.graphics.Canvas c, Highlight[] indices) {

        float rotationAngle = mChart.getRotationAngle();
        float angle = 0f;

        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        for (int i = 0; i < indices.length; i++) {

            // get the index to highlight
            int xIndex = indices[i].getXIndex();
            if (xIndex >= drawAngles.length)
                continue;

            com.github.mikephil.charting.data.PieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            if (xIndex == 0)
                angle = rotationAngle;
            else
                angle = rotationAngle + absoluteAngles[xIndex - 1];

            angle *= mAnimator.getPhaseY();

            float sliceDegrees = drawAngles[xIndex];

            float shift = set.getSelectionShift();
            android.graphics.RectF circleBox = mChart.getCircleBox();

            /**
             * Make the box containing current arc larger equally in every
             * dimension, to preserve shape of arc. Code provided by:
             *
             * @link https://github.com/wogg
             */
            android.graphics.RectF highlighted = new android.graphics.RectF(circleBox.left - shift,
                                                                            circleBox.top - shift,
                                                                            circleBox.right + shift,
                                                                            circleBox.bottom + shift);

            mRenderPaint.setColor(set.getColor(xIndex));

            // redefine the rect that contains the arc so that the
            // highlighted pie is not cut off
            mBitmapCanvas.drawArc(highlighted, angle + set.getSliceSpace() / 2f, sliceDegrees
                    * mAnimator.getPhaseY()
                    - set.getSliceSpace() / 2f, true, mRenderPaint);
        }
    }

    /**
     * This gives all pie-slices a rounded edge.
     *
     * @param c
     */
    protected void drawRoundedSlices(android.graphics.Canvas c) {

        if (!mChart.isDrawRoundedSlicesEnabled())
            return;

        com.github.mikephil.charting.data.PieDataSet dataSet = mChart.getData().getDataSet();

        if (!dataSet.isVisible())
            return;

        android.graphics.PointF center = mChart.getCenterCircleBox();
        float r = mChart.getRadius();

        // calculate the radius of the "slice-circle"
        float circleRadius = (r - (r * mChart.getHoleRadius() / 100f)) / 2f;

        java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();
        float[] drawAngles = mChart.getDrawAngles();
        float angle = mChart.getRotationAngle();

        for (int j = 0; j < entries.size(); j++) {

            float newangle = drawAngles[j];

            com.github.mikephil.charting.data.Entry e = entries.get(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                float x = (float) ((r - circleRadius)
                        * Math.cos(Math.toRadians((angle + newangle)
                        * mAnimator.getPhaseY())) + center.x);
                float y = (float) ((r - circleRadius)
                        * Math.sin(Math.toRadians((angle + newangle)
                        * mAnimator.getPhaseY())) + center.y);

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += newangle * mAnimator.getPhaseX();
        }
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mDrawBitmap != null) {
            mDrawBitmap.recycle();
            mDrawBitmap = null;
        }
    }
}
