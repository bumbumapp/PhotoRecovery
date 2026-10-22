package infiapp.bumbumapps.photorecoverynew.ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import infiapp.bumbumapps.photorecoverynew.R;


public class AdmobAdsModel {

    Context context;
    static InterstitialAd mInterstitialAd;
    String mTag = "AdmobAdsModel";

    public AdmobAdsModel(Context context) {
        this.context = context;
    }

    /** Ads are only requested with a configured unit ID and after UMP consent allows it. */
    static boolean canLoad(Context context, int unitRes) {
        String id = context.getString(unitRes);
        return id != null && !id.isEmpty() && AdsConsent.canRequestAds(context);
    }

    /** Minimum time between two interstitials. */
    static final long MIN_INTERVAL_MS = 60_000L;
    static long lastShownAt = -MIN_INTERVAL_MS;

    static boolean capAllows(long now, long lastShown) {
        return now - lastShown >= MIN_INTERVAL_MS;
    }

    public void bannerAds(Context context, ViewGroup frameLayout) {
        if (frameLayout == null || !canLoad(context, R.string.admob_banner_ads)) {
            return;
        }

        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(R.string.admob_banner_ads));
        adView.setAdSize(getAdSize((Activity) context));
        frameLayout.addView(adView);
        AdRequest build = new AdRequest.Builder().build();
        adView.loadAd(build);


        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(mTag, "onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(mTag, "onAdFailedToLoad: Banner Ads " + loadAdError.getMessage());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e(mTag, "onAdOpened: ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(mTag, "onAdLoaded: ");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e(mTag, "onAdClicked: ");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e(mTag, "onAdImpression: ");
            }
        });

    }

    private static AdSize getAdSize(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, (int) (displayMetrics.widthPixels / displayMetrics.density));
    }

    public void interstitialAdLoad(Context context) {
        if (mInterstitialAd != null || !canLoad(context, R.string.admob_interstitial_ads)) {
            return;
        }


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, context.getString(R.string.admob_interstitial_ads), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
                Log.d(mTag, "onAdLoaded: ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(mTag, "onAdFailedToLoad: " + loadAdError.getMessage());
            }
        });
    }

    public void interstitialAdShow(Activity context, final GetBackPointer getBackPointer) {

        long now = android.os.SystemClock.elapsedRealtime();
        if (mInterstitialAd != null && capAllows(now, lastShownAt) && context != null && !context.isFinishing()) {
            InterstitialAd ad = mInterstitialAd;
            mInterstitialAd = null;
            lastShownAt = now;
            ad.show(context);

            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.d(mTag, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                    // The pending action used to be lost when the ad could not be shown.
                    if (getBackPointer != null) {
                        getBackPointer.returnAction();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.d(mTag, "onAdShowedFullScreenContent: ");

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.d(mTag, "onAdDismissedFullScreenContent: ");
                    interstitialAdLoad(context);
                    if (getBackPointer != null) {
                        getBackPointer.returnAction();
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.d(mTag, "onAdImpression: ");
                }
            });
        } else {
            if (getBackPointer != null) {
                getBackPointer.returnAction();
            }
        }


    }

    public interface GetBackPointer {
        public void returnAction();
    }


}
