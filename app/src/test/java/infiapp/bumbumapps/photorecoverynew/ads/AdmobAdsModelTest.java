package infiapp.bumbumapps.photorecoverynew.ads;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AdmobAdsModelTest {

    @Test
    public void interstitialFrequencyCap() {
        assertTrue(AdmobAdsModel.capAllows(10_000L, 10_000L - AdmobAdsModel.MIN_INTERVAL_MS));
        assertFalse(AdmobAdsModel.capAllows(10_000L, 10_000L - AdmobAdsModel.MIN_INTERVAL_MS + 1));
        assertTrue(AdmobAdsModel.capAllows(0L, -AdmobAdsModel.MIN_INTERVAL_MS));
    }
}
