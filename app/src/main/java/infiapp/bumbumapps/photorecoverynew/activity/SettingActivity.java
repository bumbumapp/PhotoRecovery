package infiapp.bumbumapps.photorecoverynew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import infiapp.bumbumapps.photorecoverynew.BuildConfig;
import infiapp.bumbumapps.photorecoverynew.R;
import infiapp.bumbumapps.photorecoverynew.utills.Config;

public class SettingActivity extends AppCompatActivity {

    LinearLayout privacy_policy, ratting, share;

    MaterialToolbar toolBar;

    TextView storagePath;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        privacy_policy = findViewById(R.id.privacy_policy);
        ratting = findViewById(R.id.ratting);
        share = findViewById(R.id.share);
        toolBar = findViewById(R.id.toolBar);
        storagePath = findViewById(R.id.storagePath);

        storagePath.setText("" + Config.RECOVER_DIRECTORY);

        toolBar.setNavigationOnClickListener(v -> {
            finish();
        });

        privacy_policy.setOnClickListener(v -> {
            try {
                Uri marketUri = Uri.parse(getString(R.string.privacy_policy));
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException e) {
                //DO Something
            }
        });

        ratting.setOnClickListener(v -> {

            try {
                Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException e) {
                Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }

        });

        share.setOnClickListener(v -> {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String shareMessage = "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //Do Something
            }

        });

    }
}