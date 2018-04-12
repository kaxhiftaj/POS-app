package com.techease.posapp.ui.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.fragments.SingleUserJobFragment;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.utils.Configuration;

import java.util.ArrayList;

/**
 * Created by kaxhiftaj on 4/4/18.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {

    ArrayList<JobsModel> jobsModelArrayList;
    Context context;

    public JobsAdapter(Context context, ArrayList<JobsModel> jobsModels) {
        this.context=context;
        this.jobsModelArrayList = jobsModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_jobs, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final JobsModel model = jobsModelArrayList.get(position);
        Glide.with(context).load(model.getJob_image()).into(holder.job_image);
        holder.job_title.setText(model.getJob_title());
        holder.job_desc.setText(model.getJob_desc());
        holder.job_time.setText(model.getJob_time());
        holder.editor.putString("apilat",model.getLatitude()).commit();
        holder.editor.putString("apilng",model.getLongitude()).commit();
        holder.iv_map_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.editor.putString("selectedLatitude",String.valueOf(model.getLatitude())).commit();
                holder.editor.putString("selectedLongitude",String.valueOf(model.getLongitude())).commit();
                holder.editor.putString("title",String.valueOf(model.getJob_title())).commit();
                holder.editor.putString("descp",String.valueOf(model.getJob_desc())).commit();

                Fragment fragment = new SingleUserJobFragment();
                ((AppCompatActivity)context).getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).addToBackStack("").commit();
            }
        });

    }
    @Override
    public int getItemCount() {

        return jobsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout individual_layout;
        TextView job_title, job_desc, job_time;
        ImageView job_image,iv_map_navigation;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        public MyViewHolder(View itemView) {
            super(itemView);
           // individual_layout = (RelativeLayout) itemView.findViewById(R.id.individual_layout);
            iv_map_navigation = itemView.findViewById(R.id.map_navigation);
            job_title = (TextView) itemView.findViewById(R.id.job_title);
            job_desc = (TextView) itemView.findViewById(R.id.job_desc);
            job_image = (ImageView)itemView.findViewById(R.id.job_image);
            job_time = (TextView) itemView.findViewById(R.id.job_time);
            sharedPreferences = context.getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

        }
    }
}

