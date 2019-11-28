package com.naveensaini.dwintest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DCImagesRecyclerAdapter extends RecyclerView.Adapter<DCImagesRecyclerAdapter.ViewHolder> {


    private ArrayList<Bitmap> imageList;
    private Context context;


    public DCImagesRecyclerAdapter(Context context, ArrayList<Bitmap> imageList) {
        this.context = context;
        this.imageList = imageList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dc_images_recycler_entry, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i("ImagesRecyclerBind", String.valueOf(position));
        holder.dRecyclerEntryImage.setImageBitmap(imageList.get(position));
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView dRecyclerEntryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dRecyclerEntryImage = itemView.findViewById(R.id.dRecyclerEntryImage);
        }
    }
}
















