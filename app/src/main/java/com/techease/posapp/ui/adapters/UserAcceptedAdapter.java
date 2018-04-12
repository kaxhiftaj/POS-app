package com.techease.posapp.ui.adapters;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.ui.models.UserAcceptedModel;

import java.util.ArrayList;

public class UserAcceptedAdapter extends RecyclerView.Adapter<UserAcceptedAdapter.ViewHolder> {

    ArrayList<UserAcceptedModel> acceptedModelArrayList;
    Context context;

    public UserAcceptedAdapter(Context context, ArrayList<UserAcceptedModel> acceptedModelArrayList) {
        this.context=context;
        this.acceptedModelArrayList = acceptedModelArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.accepted_job_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserAcceptedModel model = acceptedModelArrayList.get(position);
        Glide.with(context).load(model.getImage()).into(holder.imageView);
        holder.title.setText(model.getJob_title());
        holder.description.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return acceptedModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title,description;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.accepted_job_image);
            title = (TextView) itemView.findViewById(R.id.accepted_job_title);
            description = (TextView) itemView.findViewById(R.id.accepted_job_desc);
        }
    }
}
