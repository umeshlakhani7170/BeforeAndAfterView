package com.d42gmail.cavar.beforeandafter.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class UiUtils {
    public static int convertDpToPix(int dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (metrics.densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT));
    }
}