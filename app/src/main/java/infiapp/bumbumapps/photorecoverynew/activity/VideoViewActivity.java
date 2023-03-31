package infiapp.bumbumapps.photorecoverynew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import infiapp.bumbumapps.photorecoverynew.R;
import infiapp.bumbumapps.photorecoverynew.adapter.VideoAdapter;

public class VideoViewActivity extends AppCompatActivity {
    VideoView videoView;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
         videoView =(VideoView)findViewById(R.id.vdVw);
         back=findViewById(R.id.back);
        //Set MediaController  to enable play, pause, forward, etc options.
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        //Location of Media File
        Uri uri = Uri.parse(VideoAdapter.videofile.get(0));
        //Starting VideView By Setting MediaController and URI
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        back.setOnClickListener(view -> {
            videoView.suspend();
            onBackPressed();
        });
    }


}