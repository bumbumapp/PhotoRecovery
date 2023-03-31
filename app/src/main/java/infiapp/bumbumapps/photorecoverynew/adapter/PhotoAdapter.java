package infiapp.bumbumapps.photorecoverynew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import infiapp.bumbumapps.photorecoverynew.activity.PhotoViewActivity;
import infiapp.bumbumapps.photorecoverynew.model.PhotoModel;
import infiapp.bumbumapps.photorecoverynew.R;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {
    Context context;
    ArrayList<PhotoModel> listPhoto;
    BitmapDrawable placeholder;
    public static List<String>photofile=new ArrayList<>();
    Activity aaa;


    public PhotoAdapter(Context context, ArrayList<PhotoModel> mList, Activity aaa) {
        listPhoto = new ArrayList<>();
        this.context = context;
        this.listPhoto = mList;
        this.aaa = aaa;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        ImageView ivChecked;
        CardView albumCardView;

        public MyViewHolder(View view) {
            super(view);
            ivPhoto = (ImageView) view.findViewById(R.id.iv_image);
            ivChecked = (ImageView) view.findViewById(R.id.isChecked);
            albumCardView = (CardView) view.findViewById(R.id.album_card);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_photo, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
//        holder.tvDate.setText(DateFormat.getDateInstance().format(listPhoto.get(position).getLastModified()));
//        holder.tvSize.setText(Utils.formatSize(listPhoto.get(position).getSizePhoto()));


        if (listPhoto.get(position).getIsCheck()) {
            holder.ivChecked.setVisibility(View.VISIBLE);
        } else {
            holder.ivChecked.setVisibility(View.GONE);
        }

        try {
            Glide.with(context)
                    .load("file://" + listPhoto.get(position).getPathPhoto())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .error(R.drawable.ic_error)
                    .placeholder(placeholder)
                    .centerCrop()
                    .into(holder.ivPhoto);
        } catch (Exception e) {
            //do nothing
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.albumCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listPhoto.get(position).getIsCheck()) {
                    holder.ivChecked.setVisibility(View.GONE);
                    listPhoto.get(position).setIsCheck(false);
                } else {
                    holder.ivChecked.setVisibility(View.VISIBLE);
                    listPhoto.get(position).setIsCheck(true);
                }
                return true;
            }
        });
        holder.albumCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photofile.clear();
                photofile.add("file://" + listPhoto.get(position).getPathPhoto());
                Intent intent= new Intent(context, PhotoViewActivity.class);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public ArrayList<PhotoModel> getSelectedItem() {
        ArrayList<PhotoModel> arrayList = new ArrayList();
        if (this.listPhoto != null) {
            for (int i = 0; i < this.listPhoto.size(); i++) {
                if ((this.listPhoto.get(i)).getIsCheck()) {
                    arrayList.add(this.listPhoto.get(i));
                }
            }
        }
        return arrayList;
    }

    public void setAllImagesUnseleted() {
        if (this.listPhoto != null) {
            for (int i = 0; i < this.listPhoto.size(); i++) {
                if ((this.listPhoto.get(i)).getIsCheck()) {
                    (this.listPhoto.get(i)).setIsCheck(false);
                }
            }
        }
    }

}
