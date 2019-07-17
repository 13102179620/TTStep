package com.github.mikephil.charting.animation;

/**
 * Interface for creating custom made easing functions. Uses the
 * TimeInterpolator interface provided by Android.
 */
@android.annotation.SuppressLint("NewApi")
public interface EasingFunction extends android.animation.TimeInterpolator {

    @Override
    float getInterpolation(float input);
}
