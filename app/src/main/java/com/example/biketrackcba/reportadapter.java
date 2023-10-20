package com.example.biketrackcba;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class reportadapter extends RecyclerView.Adapter<reportadapter.ViewHolder> {

    private List<Bitmap> dataset; // List of images
    private Context context;

    // Constructor
    public reportadapter(Context context, List<Bitmap> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap image = dataset.get(position);
        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addImage(Bitmap image) {
        dataset.add(image);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView1235);
        }
    }
}