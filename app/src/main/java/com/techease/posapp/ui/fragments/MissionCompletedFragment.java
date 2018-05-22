package com.techease.posapp.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.techease.posapp.R;
import com.techease.posapp.ui.adapters.JobsAdapter;
import com.techease.posapp.ui.adapters.MissionCompletedAdapter;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.ui.models.MissionCompletedModel;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MissionCompletedFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String str_token,str_user_id;
    ArrayList<MissionCompletedModel> mission_completed_list;
    MissionCompletedAdapter missionCompletedAdapter;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mission_completed, container, false);
        getActivity().setTitle("Completed");
        RecyclerView rv_missionCompleted = view.findViewById(R.id.rv_mission_completed);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        str_token = sharedPreferences.getString("api_token", "");
        str_user_id = sharedPreferences.getString("user_id","");

        if (InternetUtils.isNetworkConnected(getActivity())) {

            rv_missionCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
            mission_completed_list = new ArrayList<>();
            apicall();
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(getActivity());
            alertDialog.show();
            missionCompletedAdapter = new MissionCompletedAdapter(getActivity(), mission_completed_list);
            rv_missionCompleted.setAdapter(missionCompletedAdapter);

        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return view;
    }


    private void apicall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.MISSION_COMPLETED
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("abdullah", response);
                alertDialog.dismiss();
                if (response.contains("true")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Job_details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            MissionCompletedModel missionCompletedModel = new MissionCompletedModel();
                            String company_name = temp.getString("company_name");
                            String mission_title = temp.getString("mission_title");
                            String short_description = temp.getString("short_desc");
                            String brief_description = temp.getString("brief_desc");
                            String comments = temp.getString("comments");
                            String time = temp.getString("time");
                            String date = temp.getString("date");
                            String company_logo = temp.getString("company_logo");

                            missionCompletedModel.setCompany_name(company_name);
                            missionCompletedModel.setMission_title(mission_title);
                            missionCompletedModel.setShort_description(short_description);
                            missionCompletedModel.setBrief_description(brief_description);
                            missionCompletedModel.setComments(comments);
                            missionCompletedModel.setTime(time);
                            missionCompletedModel.setDate(date);
                            missionCompletedModel.setCompany_logo(company_logo);

                            mission_completed_list.add(missionCompletedModel);
                        }
                        missionCompletedAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.popup_layout);
                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("");
                        TextView tvMessage = dialog.findViewById(R.id.tv_message);
                        tvMessage.setText("This User ID have no job\n completed yet");
                        tvMessage.setGravity(Gravity.CENTER);
                        TextView tv_btn = dialog.findViewById(R.id.ok);
                        tv_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }
                }
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
                params.put("api_token", str_token);
                params.put("user_id",str_user_id);
                Log.d("okhaya",str_token);
                Log.d("okhaya",str_user_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }

    public void showCompletedJob(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.MISSION_COMPLETED
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("abdullah", response);
                alertDialog.dismiss();
                if (response.contains("true")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Job_details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            MissionCompletedModel missionCompletedModel = new MissionCompletedModel();
                            String company_name = temp.getString("company_name");
                            String mission_title = temp.getString("mission_title");
                            String short_description = temp.getString("short_desc");
                            String brief_description = temp.getString("brief_desc");
                            String comments = temp.getString("comments");
                            String time = temp.getString("time");
                            String date = temp.getString("date");
                            String company_logo = temp.getString("company_logo");

                            missionCompletedModel.setCompany_name(company_name);
                            missionCompletedModel.setMission_title(mission_title);
                            missionCompletedModel.setShort_description(short_description);
                            missionCompletedModel.setBrief_description(brief_description);
                            missionCompletedModel.setComments(comments);
                            missionCompletedModel.setTime(time);
                            missionCompletedModel.setDate(date);
                            missionCompletedModel.setCompany_logo(company_logo);

                            mission_completed_list.add(missionCompletedModel);
                        }
                        missionCompletedAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.popup_layout);
                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("");
                        TextView tvMessage = dialog.findViewById(R.id.tv_message);
                        tvMessage.setText("This User ID have no job\n completed yet");
                        tvMessage.setGravity(Gravity.CENTER);
                        TextView tv_btn = dialog.findViewById(R.id.ok);
                        tv_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }
                }
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
                params.put("api_token", str_token);
                params.put("user_id",str_user_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }
}
