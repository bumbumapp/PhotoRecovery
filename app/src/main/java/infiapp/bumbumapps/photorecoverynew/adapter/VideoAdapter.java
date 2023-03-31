package infiapp.bumbumapps.photorecoverynew.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import infiapp.bumbumapps.photorecoverynew.activity.VideoViewActivity;
import infiapp.bumbumapps.photorecoverynew.model.VideoModel;
import infiapp.bumbumapps.photorecoverynew.R;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    Context context;
    ArrayList<VideoModel> listPhoto;
    BitmapDrawable placeholder;
    Activity aaa;
    public static List<String> videofile=new ArrayList<String>();
    int width,heigth,exact_heigth,exact_width,per;
    public VideoAdapter(Context context, ArrayList<VideoModel> mList, Activity aaa) {
        listPhoto = new ArrayList<>();
        this.context = context;
        this.listPhoto = mList;
        this.aaa = aaa;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_video, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

//        holder.tvDate.setText(DateFormat.getDateInstance().format(listPhoto.get(position).getLastModifiedVideo()));
//        holder.tvSize.setText(Utils.formatSize(listPhoto.get(position).getSizeVideo()));
        try {
            String path ="file://" + listPhoto.get(position).getPathVideo();
            BitmapFactory.Options options = new BitmapFactory.Options();
            InputStream in = context.getContentResolver().openInputStream(
                    Uri.parse(path));
            BitmapFactory.decodeStream(in, null, options);
            options.inJustDecodeBounds = true;
            width=options.outWidth;
            heigth=options.outHeight;
            exact_heigth=width/3;
            exact_width=(exact_heigth*width)/heigth;
            Log.d("Tag","Width"+width);
            Log.d("Tag","Heigth"+heigth);
            Log.d("Tag","exactWidth"+exact_width);
            Log.d("Tag","exactHeigth"+exact_heigth);
        } catch (FileNotFoundException e) {
           Log.d("Tag","exex"+e.getMessage());
        }

        exact_width=(heigth*exact_width)/heigth;
        if (!listPhoto.get(position).isCheck()) {
            holder.ivChecked.setVisibility(View.GONE);
        } else {
            holder.ivChecked.setVisibility(View.VISIBLE);
        }
        try {
            Glide.with(context)
                    .load("file://" + listPhoto.get(position).getPathVideo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .placeholder(placeholder)
                    .into(holder.ivPhoto);
        } catch (Exception e) {
            //do nothing
        }

        holder.albumCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!listPhoto.get(position).isCheck()) {
                    holder.ivChecked.setVisibility(View.VISIBLE);
                    listPhoto.get(position).setCheck(true);
                } else {
                    holder.ivChecked.setVisibility(View.GONE);
                    listPhoto.get(position).setCheck(false);
                }
                return true;
            }
        });
        holder.albumCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videofile.clear();
                videofile.add("file://" + listPhoto.get(position).getPathVideo());
                Intent intent= new Intent(context, VideoViewActivity.class);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public ArrayList<VideoModel> getSelectedItem() {
        ArrayList<VideoModel> arrayList = new ArrayList();
        if (this.listPhoto != null) {
            for (int i = 0; i < this.listPhoto.size(); i++) {
                if ((this.listPhoto.get(i)).isCheck()) {
                    arrayList.add(this.listPhoto.get(i));
                }
            }
        }
        return arrayList;
    }


    public void setAllImagesUnseleted() {
        if (this.listPhoto != null) {
            for (int i = 0; i < this.listPhoto.size(); i++) {
                if ((this.listPhoto.get(i)).isCheck()) {
                    (this.listPhoto.get(i)).setCheck(false);
                }
            }
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
//        TextView tvDate;
//        TextView tvSize;
        ImageView ivPhoto;
        ImageView ivChecked;
        CardView albumCardView;

        public MyViewHolder(View view) {
            super(view);
//            tvDate = (TextView) view.findViewById(R.id.tvDate);
//            tvSize = (TextView) view.findViewById(R.id.tvSize);
            ivPhoto = view.findViewById(R.id.iv_image);
            ivChecked = (ImageView) view.findViewById(R.id.isChecked);
            albumCardView = (CardView) view.findViewById(R.id.album_card);
        }
    }

}
