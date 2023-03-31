package infiapp.bumbumapps.photorecoverynew.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import infiapp.bumbumapps.photorecoverynew.ads.AdmobAdsModel;
import infiapp.bumbumapps.photorecoverynew.model.AudioModel;
import infiapp.bumbumapps.photorecoverynew.R;
import infiapp.bumbumapps.photorecoverynew.adapter.AudioAdapter;
import infiapp.bumbumapps.photorecoverynew.task.RecoverAudiosAsyncTask;
import infiapp.bumbumapps.photorecoverynew.utills.Utils;

import static infiapp.bumbumapps.photorecoverynew.utills.Utils.mAlbumAudios;

public class AudioActivity extends AppCompatActivity {

    int int_position;
    RecyclerView recyclerView;
    TextView btnRestore, btnUnchecked;
    ArrayList<AudioModel> mList = new ArrayList<AudioModel>();
    Activity aaa;
    String status;

    AudioAdapter adapter;
    RecoverAudiosAsyncTask mRecoverPhotosAsyncTask;

    MaterialToolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio2);

        new AdmobAdsModel(this).bannerAds(this, findViewById(R.id.adsView));
        intView();
        intData();

    }

    public void intView() {

        btnRestore = (TextView) findViewById(R.id.btnRestore);
        btnUnchecked = (TextView) findViewById(R.id.btnUnchecked);
        toolBar = findViewById(R.id.toolBar);
        recyclerView = (RecyclerView) findViewById(R.id.gv_folder);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, Utils.calculateNoOfColumns(getApplicationContext(),80));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        toolBar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    public void intData() {
        int_position = getIntent().getIntExtra("value", 0);
        if (mAlbumAudios != null && (mAlbumAudios.size() > int_position)) {
            mList.addAll((ArrayList<AudioModel>) mAlbumAudios.get(int_position).getListAudio().clone());
//            toolBar.setTitle(""+mAlbumAudios.get(int_position).getStrAudioFolder());
        }
        adapter = new AudioAdapter(this, mList, aaa);
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
            final ArrayList<AudioModel> tempList = adapter.getSelectedItem();


            if (tempList.size() == 0) {
                Toast.makeText(AudioActivity.this, "Cannot restore, all items are unchecked!", Toast.LENGTH_LONG).show();
            } else {

                mRecoverPhotosAsyncTask = new RecoverAudiosAsyncTask(AudioActivity.this, adapter.getSelectedItem(), new RecoverAudiosAsyncTask.OnRestoreListener() {
                    @Override
                    public void onComplete() {
                       Toast.makeText(AudioActivity.this,tempList.size()+" Audio restored",Toast.LENGTH_LONG).show();
                        adapter.setAllImagesUnseleted();
                        adapter.notifyDataSetChanged();
                    }
                });
                mRecoverPhotosAsyncTask.execute();
            }
        } else if (status.equals("back")) {
            finish();
        }
    }
}