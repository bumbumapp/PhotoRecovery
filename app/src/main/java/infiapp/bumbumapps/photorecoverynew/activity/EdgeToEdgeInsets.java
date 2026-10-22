package infiapp.bumbumapps.photorecoverynew.activity;

import android.app.Activity;
import android.app.Application;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Android 15+ (targetSdk 35/36) forces edge-to-edge drawing. This helper keeps the legacy
 * look for every activity: the window content is laid out inside the safe area (status bar,
 * navigation bar, display cutout and, for adjustResize windows, the keyboard) and the bar
 * areas are painted with the theme's statusBarColor / navigationBarColor.
 */
public final class EdgeToEdgeInsets implements Application.ActivityLifecycleCallbacks {

    private static final int TAG_KEY = 0x7f0e0e0e;

    public static void install(@NonNull Application app) {
        // Below API 35 the system still fits the content inside the bars; padding again would double it.
        if (android.os.Build.VERSION.SDK_INT < 35) return;
        app.registerActivityLifecycleCallbacks(new EdgeToEdgeInsets());
    }

    /** Can also be called directly from an activity after setContentView. */
    public static void apply(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (window == null) return;
        View decor = window.getDecorView();
        if (!(decor instanceof ViewGroup) || ((ViewGroup) decor).getChildCount() == 0) return;
        if (decor.getTag(TAG_KEY) != null) return;
        decor.setTag(TAG_KEY, Boolean.TRUE);

        final View root = ((ViewGroup) decor).getChildAt(0);
        int statusColor = Color.BLACK;
        int navColor = Color.BLACK;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.statusBarColor, android.R.attr.navigationBarColor});
        try {
            statusColor = a.getColor(0, Color.BLACK);
            navColor = a.getColor(1, Color.BLACK);
        } finally {
            a.recycle();
        }
        if (Color.alpha(statusColor) < 255) statusColor = Color.BLACK;
        if (Color.alpha(navColor) < 255) navColor = Color.BLACK;

        if (root.getBackground() == null) {
            Drawable bg = decor.getBackground();
            if (bg != null && bg.getConstantState() != null) {
                root.setBackground(bg.getConstantState().newDrawable(activity.getResources()).mutate());
            } else if (bg != null) {
                root.setBackground(bg);
            }
        }
        decor.setBackgroundColor(statusColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, decor);
        controller.setAppearanceLightStatusBars(isLight(statusColor));
        controller.setAppearanceLightNavigationBars(isLight(navColor));

        int adjust = window.getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST;
        final boolean resizeForIme = adjust != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                && adjust != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            int bottom = bars.bottom;
            if (resizeForIme) {
                bottom = Math.max(bottom, insets.getInsets(WindowInsetsCompat.Type.ime()).bottom);
            }
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                if (mlp.leftMargin != bars.left || mlp.topMargin != bars.top
                        || mlp.rightMargin != bars.right || mlp.bottomMargin != bottom) {
                    mlp.setMargins(bars.left, bars.top, bars.right, bottom);
                    v.setLayoutParams(mlp);
                }
            }
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }

    static boolean isLight(int color) {
        double r = ((color >> 16) & 0xff) / 255.0, g = ((color >> 8) & 0xff) / 255.0, b = (color & 0xff) / 255.0;
        return (0.299 * r + 0.587 * g + 0.114 * b) > 0.6;
    }

    @Override public void onActivityStarted(@NonNull Activity activity) { apply(activity); }
    @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle b) { }
    @Override public void onActivityResumed(@NonNull Activity activity) { }
    @Override public void onActivityPaused(@NonNull Activity activity) { }
    @Override public void onActivityStopped(@NonNull Activity activity) { }
    @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle b) { }
    @Override public void onActivityDestroyed(@NonNull Activity activity) { }
}
