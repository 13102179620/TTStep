
package com.github.mikephil.charting.utils;

/**
 * Class that holds predefined color integer arrays (e.g.
 * ColorTemplate.VORDIPLOM_COLORS) and convenience methods for loading colors
 * from resources.
 * 
 * @author Philipp Jahoda
 */
public class ColorTemplate {

    /**
     * an "invalid" color that indicates that no color is set
     */
    public static final int COLOR_NONE = -1;

    /**
     * this "color" is used for the Legend creation and indicates that the next
     * form should be skipped
     */
    public static final int COLOR_SKIP = -2;

    /**
     * THE COLOR THEMES ARE PREDEFINED (predefined color integer arrays), FEEL
     * FREE TO CREATE YOUR OWN WITH AS MANY DIFFERENT COLORS AS YOU WANT
     */
    public static final int[] LIBERTY_COLORS = {
            android.graphics.Color.rgb(207, 248, 246), android.graphics.Color.rgb(148, 212, 212), android.graphics.Color.rgb(136, 180, 187),
            android.graphics.Color.rgb(118, 174, 175), android.graphics.Color.rgb(42, 109, 130)
    };
    public static final int[] JOYFUL_COLORS = {
            android.graphics.Color.rgb(217, 80, 138), android.graphics.Color.rgb(254, 149, 7), android.graphics.Color.rgb(254, 247, 120),
            android.graphics.Color.rgb(106, 167, 134), android.graphics.Color.rgb(53, 194, 209)
    };
    public static final int[] PASTEL_COLORS = {
            android.graphics.Color.rgb(64, 89, 128), android.graphics.Color.rgb(149, 165, 124), android.graphics.Color.rgb(217, 184, 162),
            android.graphics.Color.rgb(191, 134, 134), android.graphics.Color.rgb(179, 48, 80)
    };
    public static final int[] COLORFUL_COLORS = {
            android.graphics.Color.rgb(193, 37, 82), android.graphics.Color.rgb(255, 102, 0), android.graphics.Color.rgb(245, 199, 0),
            android.graphics.Color.rgb(106, 150, 31), android.graphics.Color.rgb(179, 100, 53)
    };
    public static final int[] VORDIPLOM_COLORS = {
            android.graphics.Color.rgb(192, 255, 140), android.graphics.Color.rgb(255, 247, 140), android.graphics.Color.rgb(255, 208, 140),
            android.graphics.Color.rgb(140, 234, 255), android.graphics.Color.rgb(255, 140, 157)
    };

    /**
     * Returns the Android ICS holo blue light color.
     * 
     * @return
     */
    public static int getHoloBlue() {
        return android.graphics.Color.rgb(51, 181, 229);
    }

    /**
     * turn an array of resource-colors (contains resource-id integers) into an
     * array list of actual color integers
     * 
     * @param r
     * @param colors an integer array of resource id's of colors
     * @return
     */
    public static java.util.List<Integer> createColors(android.content.res.Resources r, int[] colors) {

        java.util.List<Integer> result = new java.util.ArrayList<Integer>();

        for (int i : colors) {
            result.add(r.getColor(i));
        }

        return result;
    }

    /**
     * Turns an array of colors (integer color values) into an ArrayList of
     * colors.
     * 
     * @param colors
     * @return
     */
    public static java.util.List<Integer> createColors(int[] colors) {

        java.util.List<Integer> result = new java.util.ArrayList<Integer>();

        for (int i : colors) {
            result.add(i);
        }

        return result;
    }
}
