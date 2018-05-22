package com.techease.posapp.ui.adapters;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.fragments.HomeFragment;
import com.techease.posapp.ui.fragments.JobCompletedFragment;
import com.techease.posapp.ui.fragments.SingleUserJobFragment;
import com.techease.posapp.ui.models.JobImagesModel;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.utils.Configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaxhiftaj on 4/4/18.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {

    ArrayList<JobsModel> jobsModelArrayList;
    Context context;
    ArrayList<JobImagesModel> jobImages_List;
    jobImagesAdapter jobImagesAdapter;
    Dialog dialog;

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final JobsModel model = jobsModelArrayList.get(position);

        Glide.with(context).load(model.getFeature_image()).into(holder.feature_image);
        holder.companyName.setText(model.getCompany_name());
        holder.job_title.setText(model.getJob_title());
        holder.short_desc.setText(model.getShort_desc());
        holder.job_time.setText(model.getJob_time());
        holder.editor.putString("apilat",model.getLatitude()).commit();
        holder.editor.putString("apilng",model.getLongitude()).commit();

        holder.iv_map_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.editor.putString("selectedJob",String.valueOf(model.getJob_id())).commit();
                holder.editor.putString("selectedLatitude",String.valueOf(model.getLatitude())).commit();
                holder.editor.putString("selectedLongitude",String.valueOf(model.getLongitude())).commit();
                holder.editor.putString("title",String.valueOf(model.getJob_title())).commit();
                holder.editor.putString("descp",String.valueOf(model.getJob_desc())).commit();

                Fragment fragment = new SingleUserJobFragment();
                ((AppCompatActivity)context).getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).addToBackStack("").commit();
            }
        });
        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_info_window);

                final String userJobId = model.getJob_id();
                RecyclerView rv_images = dialog.findViewById(R.id.rv_images);
                LinearLayoutManager layoutManager = new LinearLayoutManager(rv_images.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv_images.setLayoutManager(layoutManager);
                jobImages_List = new ArrayList<>();

                ApiCallImages(userJobId);

                jobImagesAdapter = new jobImagesAdapter(context, jobImages_List);
                rv_images.setAdapter(jobImagesAdapter);
                dialog.setTitle("Title");
                TextView companyName = (TextView) dialog.findViewById(R.id.companyName);

                companyName.setText(model.getCompany_name());
                TextView title = dialog.findViewById(R.id.myTitle);
                title.setText(model.getJob_title());
                TextView job_short_desc = dialog.findViewById(R.id.tvShortDes);
                job_short_desc.setText(model.getShort_desc());

                TextView payJob = dialog.findViewById(R.id.pay_per_job);
                payJob.setText(model.getPay_per_job());
                TextView brief_description = dialog.findViewById(R.id.description);
                brief_description.setText(model.getBrief_desc());

                Button accept = (Button) dialog.findViewById(R.id.accept);
                Button reject = (Button) dialog.findViewById(R.id.reject);

               accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "accepted", Toast.LENGTH_SHORT).show();
                        final String userJobId =model.getJob_id();
                        Log.d("job",userJobId);
                        String str_token = holder.sharedPreferences.getString("api_token","");
                        String str_user_id =  holder.sharedPreferences.getString("user_id","");
                        apiCall(str_token,userJobId,str_user_id);
                        dialog.dismiss();
                        Fragment fragment = new HomeFragment();
                        ((AppCompatActivity)context).getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).commit();
                   }
                });

                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });

    }
    @Override
    public int getItemCount() {

        return jobsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView job_title,companyName,short_desc, job_time;
        ImageView feature_image,iv_map_navigation;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        RelativeLayout cardLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            cardLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
            iv_map_navigation = itemView.findViewById(R.id.map_navigation);
            job_title = (TextView) itemView.findViewById(R.id.job_title);
            companyName = itemView.findViewById(R.id.company_name);
            short_desc = (TextView) itemView.findViewById(R.id.job_desc);
            feature_image = (ImageView)itemView.findViewById(R.id.job_image);
            job_time = (TextView) itemView.findViewById(R.id.job_time);
            sharedPreferences = context.getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    private void apiCall(final String token, final String job_id, final String user_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.JOB_ACCEPTED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", token);
                params.put("job_id",job_id);
                params.put("users_id",user_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }

    //apiCall for getting jobImages
    private void ApiCallImages(final String job_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.JOB_RELATED_IMAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                          Log.d("abdul",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("images");
                            for(int j=0;j<jsonArray.length();j++){
                                JSONObject temp = jsonArray.getJSONObject(j);
                                JobImagesModel jobImagesModel = new JobImagesModel();
                                String image = temp.getString("images");
                                jobImagesModel.setJob_images(image);
                                jobImages_List.add(jobImagesModel);

                            }
                            jobImagesAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(context, String.valueOf(e.getCause()), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, String.valueOf(error.getCause()), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("khan",job_id);
                params.put("job_id",job_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }
    //end
}

