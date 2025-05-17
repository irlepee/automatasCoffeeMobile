package com.example.automatascoffeemobilejava.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DimensionUtils {

    public static float pxToDp(Context context, float px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }

    public static float dpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }
}
