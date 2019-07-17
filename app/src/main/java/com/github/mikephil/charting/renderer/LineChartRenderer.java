
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.LineDataProvider;

public class LineChartRenderer extends LineScatterCandleRadarRenderer {

    protected LineDataProvider mChart;

    /**
     * paint for the inner circle of the value indicators
     */
    protected android.graphics.Paint mCirclePaintInner;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected android.graphics.Bitmap mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected android.graphics.Canvas mBitmapCanvas;

    protected android.graphics.Path cubicPath = new android.graphics.Path();
    protected android.graphics.Path cubicFillPath = new android.graphics.Path();

    protected com.github.mikephil.charting.buffer.LineBuffer[] mLineBuffers;

    protected com.github.mikephil.charting.buffer.CircleBuffer[] mCircleBuffers;

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
                             com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(android.graphics.Paint.Style.FILL);
        mCirclePaintInner.setColor(android.graphics.Color.WHITE);
    }

    @Override
    public void initBuffers() {

        com.github.mikephil.charting.data.LineData lineData = mChart.getLineData();
        mLineBuffers = new com.github.mikephil.charting.buffer.LineBuffer[lineData.getDataSetCount()];
        mCircleBuffers = new com.github.mikephil.charting.buffer.CircleBuffer[lineData.getDataSetCount()];

        for (int i = 0; i < mLineBuffers.length; i++) {
            com.github.mikephil.charting.data.LineDataSet set = lineData.getDataSetByIndex(i);
            mLineBuffers[i] = new com.github.mikephil.charting.buffer.LineBuffer(set.getEntryCount() * 4 - 4);
            mCircleBuffers[i] = new com.github.mikephil.charting.buffer.CircleBuffer(set.getEntryCount() * 2);
        }
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

        com.github.mikephil.charting.data.LineData lineData = mChart.getLineData();

        for (com.github.mikephil.charting.data.LineDataSet set : lineData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }

        c.drawBitmap(mDrawBitmap, 0, 0, mRenderPaint);
    }

    protected void drawDataSet(android.graphics.Canvas c, com.github.mikephil.charting.data.LineDataSet dataSet) {

        java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

        if (entries.size() < 1)
            return;

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

        // if drawing cubic lines is enabled
        if (dataSet.isDrawCubicEnabled()) {

            drawCubic(c, dataSet, entries);

            // draw normal (straight) lines
        } else {
            drawLinear(c, dataSet, entries);
        }

        mRenderPaint.setPathEffect(null);
    }

    /**
     * Draws a cubic line.
     *
     * @param c
     * @param dataSet
     * @param entries
     */
    protected void drawCubic(android.graphics.Canvas c, com.github.mikephil.charting.data.LineDataSet dataSet, java.util.List<com.github.mikephil.charting.data.Entry> entries) {

        com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int diff = (entryFrom == entryTo) ? 1 : 0;
        int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(
                minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float intensity = dataSet.getCubicIntensity();

        cubicPath.reset();

        int size = (int) Math.ceil((maxx - minx) * phaseX + minx);

        if (size - minx >= 2) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            com.github.mikephil.charting.data.Entry prevPrev = entries.get(minx);
            com.github.mikephil.charting.data.Entry prev = entries.get(minx);
            com.github.mikephil.charting.data.Entry cur = entries.get(minx);
            com.github.mikephil.charting.data.Entry next = entries.get(minx + 1);

            // let the spline start
            cubicPath.moveTo(cur.getXIndex(), cur.getVal() * phaseY);

            prevDx = (cur.getXIndex() - prev.getXIndex()) * intensity;
            prevDy = (cur.getVal() - prev.getVal()) * intensity;

            curDx = (next.getXIndex() - cur.getXIndex()) * intensity;
            curDy = (next.getVal() - cur.getVal()) * intensity;

            // the first cubic
            cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                    cur.getXIndex() - curDx,
                    (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);

            for (int j = minx + 1, count = Math.min(size, entries.size() - 1); j < count; j++) {

                prevPrev = entries.get(j == 1 ? 0 : j - 2);
                prev = entries.get(j - 1);
                cur = entries.get(j);
                next = entries.get(j + 1);

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }

            if (size > entries.size() - 1) {

                prevPrev = entries.get((entries.size() >= 3) ? entries.size() - 3
                        : entries.size() - 2);
                prev = entries.get(entries.size() - 2);
                cur = entries.get(entries.size() - 1);
                next = cur;

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                // the last cubic
                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans,
                    entryFrom.getXIndex(), entryFrom.getXIndex() + size);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(android.graphics.Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(android.graphics.Canvas c, com.github.mikephil.charting.data.LineDataSet dataSet, android.graphics.Path spline, com.github.mikephil.charting.utils.Transformer trans,
                                 int from, int to) {

        if (to - from <= 1)
            return;

        float fillMin = dataSet.getFillFormatter()
                .getFillLinePosition(dataSet, mChart);

        spline.lineTo(to - 1, fillMin);
        spline.lineTo(from, fillMin);
        spline.close();

        trans.pathValueToPixel(spline);

        drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
    }

    /**
     * Draws a normal line.
     *
     * @param c
     * @param dataSet
     * @param entries
     */
    protected void drawLinear(android.graphics.Canvas c, com.github.mikephil.charting.data.LineDataSet dataSet, java.util.List<com.github.mikephil.charting.data.Entry> entries) {

        int dataSetIndex = mChart.getLineData().getIndexOfDataSet(dataSet);

        com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(android.graphics.Paint.Style.STROKE);

        android.graphics.Canvas canvas = null;

        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int diff = (entryFrom == entryTo) ? 1 : 0;
        int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(
                minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());

        int range = (maxx - minx) * 4 - 4;

        com.github.mikephil.charting.buffer.LineBuffer buffer = mLineBuffers[dataSetIndex];
        buffer.setPhases(phaseX, phaseY);
        buffer.limitFrom(minx);
        buffer.limitTo(maxx);
        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < range; j += 4) {

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])
                        || (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) && !mViewPortHandler
                        .isInBoundsBottom(buffer.buffer[j + 3]))
                        || (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) && !mViewPortHandler
                        .isInBoundsBottom(buffer.buffer[j + 3])))
                    continue;

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));

                canvas.drawLine(buffer.buffer[j], buffer.buffer[j + 1],
                        buffer.buffer[j + 2], buffer.buffer[j + 3], mRenderPaint);
            }

        } else { // only one color per dataset

            mRenderPaint.setColor(dataSet.getColor());

            // c.drawLines(buffer.buffer, mRenderPaint);
            canvas.drawLines(buffer.buffer, 0, range,
                    mRenderPaint);
        }

        mRenderPaint.setPathEffect(null);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entries.size() > 0) {
            drawLinearFill(c, dataSet, entries, minx, maxx, trans);
        }
    }

    protected void drawLinearFill(android.graphics.Canvas c, com.github.mikephil.charting.data.LineDataSet dataSet, java.util.List<com.github.mikephil.charting.data.Entry> entries, int minx,
                                  int maxx,
                                  com.github.mikephil.charting.utils.Transformer trans) {

        android.graphics.Path filled = generateFilledPath(
                entries,
                dataSet.getFillFormatter().getFillLinePosition(dataSet, mChart), minx, maxx);

        trans.pathValueToPixel(filled);

        drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
    }

    /**
     * Draws the provided path in filled mode with the provided color and alpha.
     * Special thanks to Angelo Suzuki (https://github.com/tinsukE) for this.
     *
     * @param c
     * @param filledPath
     * @param fillColor
     * @param fillAlpha
     */
    protected void drawFilledPath(android.graphics.Canvas c, android.graphics.Path filledPath, int fillColor, int fillAlpha) {
        c.save();
        c.clipPath(filledPath);

        int color = (fillAlpha << 24) | (fillColor & 0xffffff);
        c.drawColor(color);
        c.restore();
    }

    /**
     * Generates the path that is used for filled drawing.
     *
     * @param entries
     * @return
     */
    private android.graphics.Path generateFilledPath(java.util.List<com.github.mikephil.charting.data.Entry> entries, float fillMin, int from, int to) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        android.graphics.Path filled = new android.graphics.Path();
        filled.moveTo(entries.get(from).getXIndex(), fillMin);
        filled.lineTo(entries.get(from).getXIndex(), entries.get(from).getVal() * phaseY);

        // create a new path
        for (int x = from + 1, count = (int) Math.ceil((to - from) * phaseX + from); x < count; x++) {

            com.github.mikephil.charting.data.Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        // close up
        filled.lineTo(
                entries.get(
                        Math.max(
                                Math.min((int) Math.ceil((to - from) * phaseX + from) - 1,
                                        entries.size() - 1), 0)).getXIndex(), fillMin);

        filled.close();

        return filled;
    }

    @Override
    public void drawValues(android.graphics.Canvas c) {

        if (mChart.getLineData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            java.util.List<com.github.mikephil.charting.data.LineDataSet> dataSets = mChart.getLineData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                com.github.mikephil.charting.data.LineDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleSize() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

                com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
                com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int diff = (entryFrom == entryTo) ? 1 : 0;
                int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
                int maxx = Math.min(Math.max(
                        minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());

                float[] positions = trans.generateTransformedValuesLine(
                        entries, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    com.github.mikephil.charting.data.Entry entry = entries.get(j / 2 + minx);

                    drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, x,
                            y - valOffset);
                }
            }
        }
    }

    @Override
    public void drawExtras(android.graphics.Canvas c) {
        drawCircles(c);
    }

    protected void drawCircles(android.graphics.Canvas c) {

        mRenderPaint.setStyle(android.graphics.Paint.Style.FILL);

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        java.util.List<com.github.mikephil.charting.data.LineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {

            com.github.mikephil.charting.data.LineDataSet dataSet = dataSets.get(i);

            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled() ||
                    dataSet.getEntryCount() == 0)
                continue;

            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());

            com.github.mikephil.charting.utils.Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            java.util.List<com.github.mikephil.charting.data.Entry> entries = dataSet.getYVals();

            com.github.mikephil.charting.data.Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX);
            com.github.mikephil.charting.data.Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

            int diff = (entryFrom == entryTo) ? 1 : 0;
            int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
            int maxx = Math.min(Math.max(
                    minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());

            com.github.mikephil.charting.buffer.CircleBuffer buffer = mCircleBuffers[i];
            buffer.setPhases(phaseX, phaseY);
            buffer.limitFrom(minx);
            buffer.limitTo(maxx);
            buffer.feed(entries);

            trans.pointValuesToPixel(buffer.buffer);

            float halfsize = dataSet.getCircleSize() / 2f;

            for (int j = 0, count = (int) Math.ceil((maxx - minx) * phaseX + minx) * 2; j < count; j += 2) {

                float x = buffer.buffer[j];
                float y = buffer.buffer[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                // make sure the circles don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                    continue;

                int circleColor = dataSet.getCircleColor(j / 2 + minx);

                mRenderPaint.setColor(circleColor);

                c.drawCircle(x, y, dataSet.getCircleSize(),
                        mRenderPaint);

                if (dataSet.isDrawCircleHoleEnabled()
                        && circleColor != mCirclePaintInner.getColor())
                    c.drawCircle(x, y,
                            halfsize,
                            mCirclePaintInner);
            }
        }
    }

    @Override
    public void drawHighlighted(android.graphics.Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            com.github.mikephil.charting.data.LineDataSet set = mChart.getLineData().getDataSetByIndex(indices[i]
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

            float y = yVal * mAnimator.getPhaseY(); // get
            // the
            // y-position

            float[] pts = new float[]{
                    xIndex, y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

            // draw the lines
            drawHighlightLines(c, pts, set);
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
