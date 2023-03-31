package infiapp.bumbumapps.photorecoverynew.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import infiapp.bumbumapps.photorecoverynew.ads.AdmobAdsModel;
import infiapp.bumbumapps.photorecoverynew.model.VideoModel;
import infiapp.bumbumapps.photorecoverynew.R;
import infiapp.bumbumapps.photorecoverynew.adapter.VideoAdapter;
import infiapp.bumbumapps.photorecoverynew.task.RecoverVideosAsyncTask;
import infiapp.bumbumapps.photorecoverynew.utills.Utils;

import static infiapp.bumbumapps.photorecoverynew.utills.Utils.mAlbumVideos;

public class VideoActivity extends AppCompatActivity {

    int int_position;
    RecyclerView recyclerView;
    VideoAdapter adapter;
    TextView btnRestore, btnUnchecked;
    ArrayList<VideoModel> mList = new ArrayList<VideoModel>();
    RecoverVideosAsyncTask recoverVideosAsyncTask;
    LinearLayout ll_back;
    Activity aaa;
    String status;

    MaterialToolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        new AdmobAdsModel(this).bannerAds(this, findViewById(R.id.adsView));
        intView();
        intData();
    }

    public void intView() {

        btnRestore = (TextView) findViewById(R.id.btnRestore);
        btnUnchecked = (TextView) findViewById(R.id.btnUnchecked);
        recyclerView = (RecyclerView) findViewById(R.id.gv_folder);
        toolBar = findViewById(R.id.toolBar);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, Utils.calculateNoOfColumns(getApplicationContext(),100));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        toolBar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    public void intData() {
        int_position = getIntent().getIntExtra("value", 0);
        if (mAlbumVideos != null && (mAlbumVideos.size() > int_position)) {
            mList.addAll((ArrayList<VideoModel>) mAlbumVideos.get(int_position).getListVideo().clone());
//            toolBar.setTitle("" + mAlbumVideos.get(int_position).getStrVideoFolder());
        }

        adapter = new VideoAdapter(this, mList, aaa);
        recyclerView.setAdapter(adapter);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status = "restore";
                gotonext();
            }
        });

        btnUnchecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.setAllImagesUnseleted();
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onBackPressed() {
        status = "back";
        gotonext();
    }


    private void gotonext() {

        if (status.equals("restore")) {
            final ArrayList<VideoModel> tempList = adapter.getSelectedItem();


            if (tempList.size() == 0) {
                Toast.makeText(VideoActivity.this, "Cannot restore, all items are unchecked!", Toast.LENGTH_LONG).show();
            } else {

                recoverVideosAsyncTask = new RecoverVideosAsyncTask(VideoActivity.this, adapter.getSelectedItem(), new RecoverVideosAsyncTask.OnRestoreListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(VideoActivity.this,tempList.size()+" video restored",Toast.LENGTH_LONG).show();
                        adapter.setAllImagesUnseleted();
                        adapter.notifyDataSetChanged();
                    }
                });
                recoverVideosAsyncTask.execute();
            }
        } else if (status.equals("back")) {
            finish();
        }
    }


}