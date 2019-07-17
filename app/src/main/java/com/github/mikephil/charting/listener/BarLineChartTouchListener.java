
package com.github.mikephil.charting.listener;

import com.github.mikephil.charting.highlight.Highlight;

/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 * 
 * @author Philipp Jahoda
 */
public class BarLineChartTouchListener extends ChartTouchListener<com.github.mikephil.charting.charts.BarLineChartBase<? extends com.github.mikephil.charting.data.BarLineScatterCandleBubbleData<? extends com.github.mikephil.charting.data.BarLineScatterCandleBubbleDataSet<? extends com.github.mikephil.charting.data.Entry>>>> {

    /** the original touch-matrix from the chart */
    private android.graphics.Matrix mMatrix = new android.graphics.Matrix();

    /** matrix for saving the original matrix state */
    private android.graphics.Matrix mSavedMatrix = new android.graphics.Matrix();

    /** point where the touch action started */
    private android.graphics.PointF mTouchStartPoint = new android.graphics.PointF();

    /** center between two pointers (fingers on the display) */
    private android.graphics.PointF mTouchPointCenter = new android.graphics.PointF();

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;

    private com.github.mikephil.charting.data.DataSet<?> mClosestDataSetToTouch;

    /** used for tracking velocity of dragging */
    private android.view.VelocityTracker mVelocityTracker;

    private long mDecelerationLastTime = 0;
    private android.graphics.PointF mDecelerationCurrentPoint = new android.graphics.PointF();
    private android.graphics.PointF mDecelerationVelocity = new android.graphics.PointF();

    public BarLineChartTouchListener(com.github.mikephil.charting.charts.BarLineChartBase<? extends com.github.mikephil.charting.data.BarLineScatterCandleBubbleData<? extends com.github.mikephil.charting.data.BarLineScatterCandleBubbleDataSet<? extends com.github.mikephil.charting.data.Entry>>> chart, android.graphics.Matrix touchMatrix) {
        super(chart);
        this.mMatrix = touchMatrix;
    }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(android.view.View v, android.view.MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = android.view.VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        if (event.getActionMasked() == android.view.MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
            return true;

        // Handle touch events here...
        switch (event.getAction() & android.view.MotionEvent.ACTION_MASK) {

            case android.view.MotionEvent.ACTION_DOWN:

                startAction(event);

                stopDeceleration();

                saveTouchStart(event);

                break;
            case android.view.MotionEvent.ACTION_POINTER_DOWN:

                if (event.getPointerCount() >= 2) {

                    mChart.disableScroll();

                    saveTouchStart(event);

                    // get the distance between the pointers on the x-axis
                    mSavedXDist = getXDist(event);

                    // get the distance between the pointers on the y-axis
                    mSavedYDist = getYDist(event);

                    // get the total distance between the pointers
                    mSavedDist = spacing(event);

                    if (mSavedDist > 10f) {

                        if (mChart.isPinchZoomEnabled()) {
                            mTouchMode = PINCH_ZOOM;
                        } else {
                            if (mSavedXDist > mSavedYDist)
                                mTouchMode = X_ZOOM;
                            else
                                mTouchMode = Y_ZOOM;
                        }
                    }

                    // determine the touch-pointer center
                    midPoint(mTouchPointCenter, event);
                }
                break;
            case android.view.MotionEvent.ACTION_MOVE:

                if (mTouchMode == DRAG) {

                    mChart.disableScroll();
                    performDrag(event);

                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

                    mChart.disableScroll();

                    if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled())
                        performZoom(event);

                } else if (mTouchMode == NONE
                        && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                                mTouchStartPoint.y)) > 5f) {


                    if (mChart.hasNoDragOffset()) {

                        if (!mChart.isFullyZoomedOut() && mChart.isDragEnabled()) {
                            mTouchMode = DRAG;
                        } else {

                            mLastGesture = ChartGesture.DRAG;

                            if (mChart.isHighlightPerDragEnabled())
                                performHighlightDrag(event);
                        }

                    } else if (mChart.isDragEnabled()) {
                        mLastGesture = ChartGesture.DRAG;
                        mTouchMode = DRAG;
                    }
                }
                break;

            case android.view.MotionEvent.ACTION_UP:

                final android.view.VelocityTracker velocityTracker = mVelocityTracker;
                final int pointerId = event.getPointerId(0);
                velocityTracker.computeCurrentVelocity(1000, com.github.mikephil.charting.utils.Utils.getMaximumFlingVelocity());
                final float velocityY = velocityTracker.getYVelocity(pointerId);
                final float velocityX = velocityTracker.getXVelocity(pointerId);

                if (Math.abs(velocityX) > com.github.mikephil.charting.utils.Utils.getMinimumFlingVelocity() ||
                        Math.abs(velocityY) > com.github.mikephil.charting.utils.Utils.getMinimumFlingVelocity()) {

                    if (mTouchMode == DRAG && mChart.isDragDecelerationEnabled()) {

                        stopDeceleration();

                        mDecelerationLastTime = android.view.animation.AnimationUtils.currentAnimationTimeMillis();
                        mDecelerationCurrentPoint = new android.graphics.PointF(event.getX(), event.getY());
                        mDecelerationVelocity = new android.graphics.PointF(velocityX, velocityY);

                        com.github.mikephil.charting.utils.Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google
                    }
                }

                if (mTouchMode == X_ZOOM ||
                        mTouchMode == Y_ZOOM ||
                        mTouchMode == PINCH_ZOOM ||
                        mTouchMode == POST_ZOOM) {

                    // Range might have changed, which means that Y-axis labels
                    // could have changed in size, affecting Y-axis size.
                    // So we need to recalculate offsets.
                    mChart.calculateOffsets();
                    mChart.postInvalidate();
                }

                mTouchMode = NONE;
                mChart.enableScroll();

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                endAction(event);

                break;
            case android.view.MotionEvent.ACTION_POINTER_UP:
                com.github.mikephil.charting.utils.Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker);

                mTouchMode = POST_ZOOM;
                break;

            case android.view.MotionEvent.ACTION_CANCEL:

                mTouchMode = NONE;
                endAction(event);
                break;
        }

        // Perform the transformation, update the chart
        // if (needsRefresh())
        mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, true);

        return true; // indicate event was handled
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE PERFORMS THE ACTUAL TOUCH ACTIONS */

    /**
     * Saves the current Matrix state and the touch-start point.
     * 
     * @param event
     */
    private void saveTouchStart(android.view.MotionEvent event) {

        mSavedMatrix.set(mMatrix);
        mTouchStartPoint.set(event.getX(), event.getY());

        mClosestDataSetToTouch = mChart.getDataSetByTouchPoint(event.getX(), event.getY());
    }

    /**
     * Performs all necessary operations needed for dragging.
     * 
     * @param event
     */
    private void performDrag(android.view.MotionEvent event) {

        mLastGesture = ChartGesture.DRAG;

        mMatrix.set(mSavedMatrix);

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        float dX, dY;

        // check if axis is inverted
        if (mChart.isAnyAxisInverted() && mClosestDataSetToTouch != null
                && mChart.getAxis(mClosestDataSetToTouch.getAxisDependency()).isInverted()) {

            // if there is an inverted horizontalbarchart
            if (mChart instanceof com.github.mikephil.charting.charts.HorizontalBarChart) {
                dX = -(event.getX() - mTouchStartPoint.x);
                dY = event.getY() - mTouchStartPoint.y;
            } else {
                dX = event.getX() - mTouchStartPoint.x;
                dY = -(event.getY() - mTouchStartPoint.y);
            }
        }
        else {
            dX = event.getX() - mTouchStartPoint.x;
            dY = event.getY() - mTouchStartPoint.y;
        }

        mMatrix.postTranslate(dX, dY);

        if (l != null)
            l.onChartTranslate(event, dX, dY);
    }

    /**
     * Performs the all operations necessary for pinch and axis zoom.
     * 
     * @param event
     */
    private void performZoom(android.view.MotionEvent event) {

        if (event.getPointerCount() >= 2) {

            OnChartGestureListener l = mChart.getOnChartGestureListener();

            // get the distance between the pointers of the touch
            // event
            float totalDist = spacing(event);

            if (totalDist > 10f) {

                // get the translation
                android.graphics.PointF t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y);

                // take actions depending on the activated touch
                // mode
                if (mTouchMode == PINCH_ZOOM) {

                    mLastGesture = ChartGesture.PINCH_ZOOM;

                    float scale = totalDist / mSavedDist; // total scale

                    boolean isZoomingOut = (scale < 1);
                    boolean canZoomMoreX = isZoomingOut ?
                            mChart.getViewPortHandler().canZoomOutMoreX() :
                            mChart.getViewPortHandler().canZoomInMoreX();

                    float scaleX = (mChart.isScaleXEnabled()) ? scale : 1f;
                    float scaleY = (mChart.isScaleYEnabled()) ? scale : 1f;

                    if (mChart.isScaleYEnabled() || canZoomMoreX) {

                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scaleX, scaleY, t.x, t.y);

                        if (l != null)
                            l.onChartScale(event, scaleX, scaleY);
                    }

                } else if (mTouchMode == X_ZOOM && mChart.isScaleXEnabled()) {

                    mLastGesture = ChartGesture.X_ZOOM;

                    float xDist = getXDist(event);
                    float scaleX = xDist / mSavedXDist; // x-axis scale

                    boolean isZoomingOut = (scaleX < 1);
                    boolean canZoomMoreX = isZoomingOut ?
                            mChart.getViewPortHandler().canZoomOutMoreX() :
                            mChart.getViewPortHandler().canZoomInMoreX();

                    if (canZoomMoreX) {

                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scaleX, 1f, t.x, t.y);

                        if (l != null)
                            l.onChartScale(event, scaleX, 1f);
                    }

                } else if (mTouchMode == Y_ZOOM && mChart.isScaleYEnabled()) {

                    mLastGesture = ChartGesture.Y_ZOOM;

                    float yDist = getYDist(event);
                    float scaleY = yDist / mSavedYDist; // y-axis scale

                    mMatrix.set(mSavedMatrix);

                    // y-axis comes from top to bottom, revert y
                    mMatrix.postScale(1f, scaleY, t.x, t.y);

                    if (l != null)
                        l.onChartScale(event, 1f, scaleY);
                }
            }
        }
    }

    /**
     * Highlights upon dragging, generates callbacks for the selection-listener.
     * 
     * @param e
     */
    private void performHighlightDrag(android.view.MotionEvent e) {

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h != null && !h.equalTo(mLastHighlighted)) {
            mLastHighlighted = h;
            mChart.highlightTouch(h);
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** DOING THE MATH BELOW ;-) */


    /**
     * Determines the center point between two pointer touch points.
     * 
     * @param point
     * @param event
     */
    private static void midPoint(android.graphics.PointF point, android.view.MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2f, y / 2f);
    }

    /**
     * returns the distance between two pointer touch points
     * 
     * @param event
     * @return
     */
    private static float spacing(android.view.MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * calculates the distance on the x-axis between two pointers (fingers on
     * the display)
     * 
     * @param e
     * @return
     */
    private static float getXDist(android.view.MotionEvent e) {
        float x = Math.abs(e.getX(0) - e.getX(1));
        return x;
    }

    /**
     * calculates the distance on the y-axis between two pointers (fingers on
     * the display)
     * 
     * @param e
     * @return
     */
    private static float getYDist(android.view.MotionEvent e) {
        float y = Math.abs(e.getY(0) - e.getY(1));
        return y;
    }

    /**
     * returns the correct translation depending on the provided x and y touch
     * points
     * 
     * @param x
     * @param y
     * @return
     */
    public android.graphics.PointF getTrans(float x, float y) {

        com.github.mikephil.charting.utils.ViewPortHandler vph = mChart.getViewPortHandler();

        float xTrans = x - vph.offsetLeft();
        float yTrans = 0f;

        // check if axis is inverted
        if (mChart.isAnyAxisInverted() && mClosestDataSetToTouch != null
                && mChart.isInverted(mClosestDataSetToTouch.getAxisDependency())) {
            yTrans = -(y - vph.offsetTop());
        } else {
            yTrans = -(mChart.getMeasuredHeight() - y - vph.offsetBottom());
        }

        return new android.graphics.PointF(xTrans, yTrans);
    }

    /**
     * ################ ################ ################ ################
     */
    /** GETTERS AND GESTURE RECOGNITION BELOW */

    /**
     * returns the matrix object the listener holds
     * 
     * @return
     */
    public android.graphics.Matrix getMatrix() {
        return mMatrix;
    }

    @Override
    public boolean onDoubleTap(android.view.MotionEvent e) {

        mLastGesture = ChartGesture.DOUBLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
            return super.onDoubleTap(e);
        }

        // check if double-tap zooming is enabled
        if (mChart.isDoubleTapToZoomEnabled()) {

            android.graphics.PointF trans = getTrans(e.getX(), e.getY());

            mChart.zoom(mChart.isScaleXEnabled() ? 1.4f : 1f, mChart.isScaleYEnabled() ? 1.4f : 1f, trans.x, trans.y);

            if (mChart.isLogEnabled())
                android.util.Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: "
                        + trans.y);
        }

        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(android.view.MotionEvent e) {

        mLastGesture = ChartGesture.LONG_PRESS;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {

            l.onChartLongPressed(e);
        }
    }

    @Override
    public boolean onSingleTapUp(android.view.MotionEvent e) {

        mLastGesture = ChartGesture.SINGLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        if(!mChart.isHighlightPerTapEnabled()) {
            return false;
        }


        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        performHighlight(h, e);

        return super.onSingleTapUp(e);
    }

//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent e) {
//
//        mLastGesture = ChartGesture.SINGLE_TAP;
//
//        OnChartGestureListener l = mChart.getOnChartGestureListener();
//
//        if (l != null) {
//            l.onChartSingleTapped(e);
//            l.onChartGestureEnd(e, mLastGesture);
//        }
//
//        return super.onSingleTapConfirmed(e);
//    }

    @Override
    public boolean onFling(android.view.MotionEvent e1, android.view.MotionEvent e2, float velocityX, float velocityY) {

        mLastGesture = ChartGesture.FLING;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartFling(e1, e2, velocityX, velocityY);
        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public void stopDeceleration() {
        mDecelerationVelocity = new android.graphics.PointF(0.f, 0.f);
    }

    public void computeScroll() {

        if (mDecelerationVelocity.x == 0.f && mDecelerationVelocity.y == 0.f)
            return; // There's no deceleration in progress

        final long currentTime = android.view.animation.AnimationUtils.currentAnimationTimeMillis();

        mDecelerationVelocity.x *= mChart.getDragDecelerationFrictionCoef();
        mDecelerationVelocity.y *= mChart.getDragDecelerationFrictionCoef();

        final float timeInterval = (float)(currentTime - mDecelerationLastTime) / 1000.f;

        float distanceX = mDecelerationVelocity.x * timeInterval;
        float distanceY = mDecelerationVelocity.y * timeInterval;

        mDecelerationCurrentPoint.x += distanceX;
        mDecelerationCurrentPoint.y += distanceY;

        android.view.MotionEvent event = android.view.MotionEvent.obtain(currentTime, currentTime, android.view.MotionEvent.ACTION_MOVE, mDecelerationCurrentPoint.x, mDecelerationCurrentPoint.y, 0);
        performDrag(event);
        event.recycle();
        mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, false);

        mDecelerationLastTime = currentTime;

        if (Math.abs(mDecelerationVelocity.x) >= 0.01 || Math.abs(mDecelerationVelocity.y) >= 0.01)
            com.github.mikephil.charting.utils.Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google
        else {
            // Range might have changed, which means that Y-axis labels
            // could have changed in size, affecting Y-axis size.
            // So we need to recalculate offsets.
            mChart.calculateOffsets();
            mChart.postInvalidate();

            stopDeceleration();
        }
    }
}
