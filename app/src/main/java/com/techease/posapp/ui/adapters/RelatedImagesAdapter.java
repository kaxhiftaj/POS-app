package com.techease.posapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobImagesModel;

import java.util.ArrayList;

public class RelatedImagesAdapter extends RecyclerView.Adapter<RelatedImagesAdapter.ViewHolder>{

        ArrayList<JobImagesModel> relatedImages_list;
        Context context;

    public RelatedImagesAdapter(Context context, ArrayList<JobImagesModel> jobImagesModels) {
        this.context=context;
        this.relatedImages_list = jobImagesModels;
    }
@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.related_image_layout,parent,false);
        return new RelatedImagesAdapter.ViewHolder(v);
        }

@Override
public void onBindViewHolder(ViewHolder holder,int position){
        JobImagesModel jobImagesModel=relatedImages_list.get(position);
        Glide.with(context).load(jobImagesModel.getJob_images()).into(holder.related_images);

        }

@Override
public int getItemCount(){
        return relatedImages_list.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView related_images;

    public ViewHolder(View itemView) {
        super(itemView);
        related_images = itemView.findViewById(R.id.related_images);
    }
}
}

