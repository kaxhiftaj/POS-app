package com.techease.posapp.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.techease.posapp.R;
import com.techease.posapp.ui.adapters.JobsAdapter;
import com.techease.posapp.ui.adapters.UserAcceptedAdapter;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.ui.models.UserAcceptedModel;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAcceptedFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RecyclerView rv_userAccepted;
    ArrayList<UserAcceptedModel> job_accepted_list;
    UserAcceptedAdapter userAcceptedAdapter;
    String strApi_token, strUserID;
    Dialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_accepted, container, false);
        getActivity().setTitle("User Accepted");
        rv_userAccepted = view.findViewById(R.id.rv_user_accepted);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        strApi_token = sharedPreferences.getString("api_token","");
        strUserID = sharedPreferences.getString("user_id","");

        if (InternetUtils.isNetworkConnected(getActivity())) {

            rv_userAccepted.setLayoutManager(new LinearLayoutManager(getActivity()));
            job_accepted_list = new ArrayList<>();
            jonAccepted_apicall();
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(getActivity());
            alertDialog.show();
            userAcceptedAdapter = new UserAcceptedAdapter(getActivity(), job_accepted_list);
            rv_userAccepted.setAdapter(userAcceptedAdapter);

        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void jonAccepted_apicall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_JOB_ACCEPTED
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("200")) {
                    Log.d("umer",response);
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArr = jsonObject.getJSONArray("user_jobs");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject temp = jsonArr.getJSONObject(i);

                            UserAcceptedModel model = new UserAcceptedModel();
                            String jobId =temp.getString("job_id");
                            String usersId = temp.getString("users_id");
                            String jobTitle = temp.getString("mission_title");
                            String description = temp.getString("description");
                            String lat = temp.getString("latitude");
                            String lng = temp.getString("longitude");
                            String image = temp.getString("image");
                            String company_name = temp.getString("company_name");

                            Log.d("nido",jobTitle);
                            Log.d("nido",company_name);
                            model.setJob_id(jobId);
                            model.setCompany_name(company_name);
                            model.setJob_title(jobTitle);
                            model.setDescription(description);
                            model.setImage(image);
                            job_accepted_list.add(model);

                        }
                        userAcceptedAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }

                } else {
//
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
//                        AlertsUtils.showErrorDialog(getActivity(), "This User ID have no job Accepted Yet");
                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.popup_layout);
                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("");
                        TextView tvMessage = dialog.findViewById(R.id.tv_message);
                        tvMessage.setText("This User ID have no job\n accepted yet");
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
                if (alertDialog != null)
                    alertDialog.dismiss();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", strApi_token);
                params.put("users_id",strUserID);
                Log.d("token",strApi_token);
                Log.d("token",strUserID);
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
