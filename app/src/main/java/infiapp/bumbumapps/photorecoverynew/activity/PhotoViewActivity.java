package infiapp.bumbumapps.photorecoverynew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import infiapp.bumbumapps.photorecoverynew.R;
import infiapp.bumbumapps.photorecoverynew.adapter.PhotoAdapter;

public class PhotoViewActivity extends AppCompatActivity {
    ImageView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        photoView=findViewById(R.id.photoView);
        Glide.with(getApplicationContext())
                .load(PhotoAdapter.photofile.get(0))
                .into(photoView);
    }
}