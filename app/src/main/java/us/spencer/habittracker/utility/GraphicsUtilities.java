package us.spencer.habittracker.utility;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class GraphicsUtilities {

    public static float dpToPixels(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static int pixelsToDp(Context context, float pixels) {
        return (int) (pixels / context.getResources().getDisplayMetrics().density);
    }
}
