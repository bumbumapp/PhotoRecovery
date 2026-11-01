package infiapp.bumbumapps.photorecoverynew.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.InterstitialAd;
import com.huawei.hms.ads.banner.BannerView;

import infiapp.bumbumapps.photorecoverynew.BuildConfig;
import infiapp.bumbumapps.photorecoverynew.R;

/**
 * Huawei AppGallery build: same API and interstitial cap as the Play build, served by Huawei Ads (Petal).
 * Release builds use the app's Petal slots, debug builds Huawei's test slots.
 */
public class AdmobAdsModel {

    Context context;
    static InterstitialAd mInterstitialAd;
    String mTag = "AdmobAdsModel";

    public AdmobAdsModel(Context context) {
        this.context = context;
    }

    static String slot(Context context, int real, int test) {
        return context.getString(BuildConfig.DEBUG ? test : real);
    }

    /** Minimum time between two interstitials. */
    static final long MIN_INTERVAL_MS = 60_000L;
    static long lastShownAt = -MIN_INTERVAL_MS;

    static boolean capAllows(long now, long lastShown) {
        return now - lastShown >= MIN_INTERVAL_MS;
    }

    public void bannerAds(Context context, ViewGroup frameLayout) {
        if (frameLayout == null || !(context instanceof Activity) || !AdsConsent.canRequestAds(context)) {
            return;
        }
        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        frameLayout.setVisibility(View.GONE);
        BannerView banner = new BannerView(activity);
        banner.setAdId(slot(activity, R.string.hms_banner_id, R.string.hms_test_banner_id));
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        banner.setBannerAdSize(BannerAdSize.getCurrentDirectionBannerSize(activity, (int) (dm.widthPixels / dm.density)));
        banner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailed(int code) {
                Log.d(mTag, "Banner failed: " + code);
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(banner, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        banner.loadAd(new AdParam.Builder().build());
    }

    public void interstitialAdLoad(Context context) {
        if (mInterstitialAd != null || !AdsConsent.canRequestAds(context)) {
            return;
        }
        Context app = context.getApplicationContext();
        InterstitialAd ad = new InterstitialAd(app);
        ad.setAdId(slot(app, R.string.hms_interstitial_id, R.string.hms_test_interstitial_id));
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int code) {
                if (mInterstitialAd == ad) mInterstitialAd = null;
                Log.d(mTag, "Interstitial failed: " + code);
            }
        });
        mInterstitialAd = ad;
        ad.loadAd(new AdParam.Builder().build());
    }

    public void interstitialAdShow(Activity context, final GetBackPointer getBackPointer) {
        long now = android.os.SystemClock.elapsedRealtime();
        InterstitialAd ad = mInterstitialAd;
        if (ad == null || !ad.isLoaded() || !capAllows(now, lastShownAt) || context == null || context.isFinishing()) {
            if (ad == null && context != null) {
                interstitialAdLoad(context);
            }
            if (getBackPointer != null) {
                getBackPointer.returnAction();
            }
            return;
        }
        mInterstitialAd = null;
        lastShownAt = now;
        final Context app = context.getApplicationContext();
        // Huawei only reports load failures via onAdFailed: if show() never opens the ad, do not wait forever.
        final boolean[] opened = {false};
        final boolean[] done = {false};
        final Runnable finish = () -> {
            if (done[0]) {
                return;
            }
            done[0] = true;
            interstitialAdLoad(app);
            if (getBackPointer != null) {
                getBackPointer.returnAction();
            }
        };
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                opened[0] = true;
            }

            @Override
            public void onAdClosed() {
                finish.run();
            }

            @Override
            public void onAdFailed(int code) {
                finish.run();
            }
        });
        ad.show(context);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!opened[0]) {
                finish.run();
            }
        }, 3000L);
    }

    public interface GetBackPointer {
        public void returnAction();
    }
}
