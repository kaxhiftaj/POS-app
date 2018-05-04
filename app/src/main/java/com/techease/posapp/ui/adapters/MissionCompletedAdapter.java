package com.techease.posapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.ui.models.MissionCompletedModel;

import java.util.ArrayList;

public class MissionCompletedAdapter extends RecyclerView.Adapter<MissionCompletedAdapter.MyViewHolder>  {
    Context context;
    ArrayList<MissionCompletedModel> missionCompletedModelArrayList;

    public MissionCompletedAdapter(Context context,ArrayList<MissionCompletedModel> missionCompletedModels){
        this.context = context;
        this.missionCompletedModelArrayList = missionCompletedModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mission_completed_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
         MissionCompletedModel completedModel = missionCompletedModelArrayList.get(position);
         Glide.with(context).load(completedModel.getCompany_logo()).into(holder.company_logo);
         holder.company_name.setText(completedModel.getCompany_name());
         holder.mission_title.setText(completedModel.getMission_title());
         holder.time.setText(completedModel.getDate());
         holder.short_desc.setText(completedModel.getShort_description());
    }

    @Override
    public int getItemCount() {
        return missionCompletedModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView company_name,mission_title,short_desc,time,date,comment;
        ImageView company_logo;
        public MyViewHolder(View itemView) {
            super(itemView);
            company_name = itemView.findViewById(R.id.completed_company_name);
            mission_title = itemView.findViewById(R.id.completed_mission_title);
            short_desc = itemView.findViewById(R.id.completed_job_short_desc);
            time = itemView.findViewById(R.id.completed_job_time);
            company_logo = itemView.findViewById(R.id.completed_job_image);
        }
    }
}
