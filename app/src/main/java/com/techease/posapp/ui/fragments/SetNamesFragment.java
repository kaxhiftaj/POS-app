package com.techease.posapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techease.posapp.R;
import com.techease.posapp.ui.activities.MainActivity;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;


public class SetNamesFragment extends Fragment {
    EditText et_set_FirstName, et_setLastName;
    Button  btn_Go;
    String strFirstName,strLastName,strMobile;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    android.support.v7.app.AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_names, container, false);
        et_set_FirstName = view.findViewById(R.id.set_first_name);
        et_setLastName = view.findViewById(R.id.set_last_name);
        btn_Go = (Button)view.findViewById(R.id.go);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        strMobile = sharedPreferences.getString("phone_no","");

        btn_Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataInput();
            }
        });

        return view;
    }

    public void onDataInput() {
        strFirstName = et_set_FirstName.getText().toString().trim();
        strLastName = et_setLastName.getText().toString().trim();
        if (strFirstName.equals("")) {
            et_set_FirstName.setError("Please enter valid phone number");

        }else if (strLastName.equals("")) {
            et_setLastName.setError("Please enter your Last Name");
        } else {
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(getActivity());
            alertDialog.show();
            apiCall();
        }
    }

    public void apiCall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,  Configuration.USER_LOGIN
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("zma response", response);

                 if (response.contains("true")) {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("user_data");
                        String api_token = jsonObject.getString("api_token");
                        int user_id = jsonObject.getInt("user_id");
                        Log.d("my",api_token);
                        Log.d("my",String.valueOf(user_id));

                        editor.putString("api_token", api_token);
                        editor.putString("user_id", String.valueOf(user_id));
                        editor.commit();
                        startActivity(new Intent(getActivity(), MainActivity.class));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        Log.d("error", String.valueOf(e.getMessage()));
                    }

                } else {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        AlertsUtils.showErrorDialog(getActivity(), message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (alertDialog != null)
                    alertDialog.dismiss();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                } else if (error instanceof AuthFailureError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Auth Failure");
                } else if (error instanceof ServerError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Server Error");
                } else if (error instanceof NetworkError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Network Error");
                } else if (error instanceof ParseError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Parsing Error");
                }

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no",strMobile);
                params.put("first_name",strFirstName);
                params.put("last_name",strLastName);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }
}
