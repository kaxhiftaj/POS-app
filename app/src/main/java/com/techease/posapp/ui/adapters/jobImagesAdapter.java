package com.techease.posapp.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobImagesModel;
import com.techease.posapp.ui.models.UserAcceptedModel;
import com.techease.posapp.utils.Configuration;

import java.util.ArrayList;
import java.util.List;

public class jobImagesAdapter extends RecyclerView.Adapter<jobImagesAdapter.ViewHolder> {
    ArrayList<JobImagesModel> jobImagesModelArrayList;
    Context context;

    public jobImagesAdapter(Context context, ArrayList<JobImagesModel> jobImagesModels) {
        this.context=context;
        this.jobImagesModelArrayList = jobImagesModels;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_images_layout, parent, false);
        return new jobImagesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         JobImagesModel jobImagesModel = jobImagesModelArrayList.get(position);
         Glide.with(context).load(jobImagesModel.getJob_images() ).into(holder.jobimages);
    }

    @Override
    public int getItemCount() {
        return jobImagesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView jobimages;


        public ViewHolder(View itemView) {
            super(itemView);
            jobimages = itemView.findViewById(R.id.myJobImages);
        }
    }
}
