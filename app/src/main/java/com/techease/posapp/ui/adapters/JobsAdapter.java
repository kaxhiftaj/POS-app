package com.techease.posapp.ui.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobsModel;

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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final JobsModel model = jobsModelArrayList.get(position);

        holder.job_title.setText(model.getJob_title());
        holder.job_desc.setText(model.getJob_desc());
        holder.job_time.setText(model.getJob_time());

//        holder.appointments_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new AppointmentDetails();
//
//                String merchant_id = model.getMerchant_id();
//                String client_name = model.getClient_name();
//                String service_name = model.getService_name();
//                String booking_start_time = model.getBooking_start_time();
//                String booking_end_time = model.getBooking_end_time();
//                String appointment_status = model.getAppointment_status();
//                String appointment_type = model.getAppointment_type();
//                String price = model.getPrice();
//                String payment_status =  model.getPayment_status();
//                String booking_date = model.getBooking_date();
//
//                Bundle bundle=new Bundle();
//                bundle.putString("merchant_id",merchant_id);
//                bundle.putString("client_name",client_name);
//                bundle.putString("service_name",service_name);
//                bundle.putString("booking_start_time",booking_start_time);
//                bundle.putString("booking_end_time",booking_end_time);
//                bundle.putString("appointment_status",appointment_status);
//                bundle.putString("appointment_type",appointment_type);
//                bundle.putString("price",price);
//                bundle.putString("payment_status",payment_status);
//                bundle.putString("booking_date",booking_date);
//                fragment.setArguments(bundle);
//                Activity activity = (FullScreenActivity) context;
//                activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("view").commit();
//
//            }
//        });

    }

    @Override
    public int getItemCount() {

        return jobsModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView job_title, job_desc, job_time;
        ImageView job_image ;

        public MyViewHolder(View itemView) {
            super(itemView);
            job_title = (TextView) itemView.findViewById(R.id.job_title);
            job_desc = (TextView) itemView.findViewById(R.id.job_desc);
            job_image = (ImageView)itemView.findViewById(R.id.job_image);
            job_time = (TextView) itemView.findViewById(R.id.job_time);


        }
    }
}

