package infiapp.bumbumapps.photorecoverynew.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.NonPersonalizedAd;
import com.huawei.hms.ads.RequestOptions;

/**
 * Huawei AppGallery build: same API as the Play build's UMP flow. Huawei Ads (Petal) are initialised with
 * non-personalized ads only, so no consent form is needed.
 */
public final class AdsConsent {
    private static final String TAG = "AdsConsent";
    private static boolean sdkReady;

    private AdsConsent() {
    }

    public static void gather(Activity activity, Runnable onReady) {
        if (init(activity) && !activity.isFinishing() && !activity.isDestroyed() && onReady != null) {
            onReady.run();
        }
    }

    /** Initialises Huawei Ads once; returns whether ads can be requested. */
    static synchronized boolean init(Context context) {
        if (sdkReady) return true;
        try {
            HwAds.init(context.getApplicationContext());
            RequestOptions options = HwAds.getRequestOptions().toBuilder()
                    .setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED)
                    .build();
            HwAds.setRequestOptions(options);
            sdkReady = true;
        } catch (RuntimeException e) {
            Log.w(TAG, "Huawei Ads init failed", e);
        }
        return sdkReady;
    }

    public static boolean canRequestAds(Context context) {
        return init(context);
    }

    public static boolean isPrivacyOptionsRequired(Context context) {
        return false;
    }

    public static void showPrivacyOptions(Activity activity) {
    }
}
