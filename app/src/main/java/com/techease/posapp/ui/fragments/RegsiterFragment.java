package com.techease.posapp.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RegsiterFragment extends Fragment {

   @BindView(R.id.user_phone_no)
    EditText signup_mobile;

    @BindView(R.id.user_first_name)
    EditText signup_firstName;

    @BindView(R.id.user_last_name)
    EditText signup_lastName;

    @BindView(R.id.signup)
    Button signup;

    @BindView(R.id.already_signed)
    TextView already_sgined;

    Unbinder unbinder;
    String strmobileNum, strFirstName, strLastName;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    android.support.v7.app.AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_regsiter, container, false);
        ButterKnife.bind(this, v);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        already_sgined.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
          Fragment fragment = new LoginFragment();
          getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
         }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDataInput();
            }
        });

        already_sgined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LoginFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack("tag").commit();
            }
        });

        return v;
    }


    public void onDataInput() {
        strmobileNum = signup_mobile.getText().toString();
        strFirstName = signup_firstName.getText().toString();
        strLastName = signup_lastName.getText().toString();

        if (strmobileNum.equals("") || strmobileNum.length() < 10) {
            signup_mobile.setError("Enter a valid Number");
        } else if (strFirstName == "") {
            signup_firstName.setError("Please enter valid email id");
        } else if (strLastName == "") {
            signup_lastName.setError("Please enter a secure password");
        } else {
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(getActivity());
            alertDialog.show();
            apiCall();
        }

    }

    public void apiCall() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_URL+"sign_up", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("umer", response);
                if (response.contains("true")) {

                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("user_data");
                        String user_id = jsonObject.getString("user_id");
                        String api_token = jsonObject.getString("api_token");
                        editor.putString("api_token", api_token);
                        editor.putString("user_id" , user_id);
                        editor.commit();
                        Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new LoginFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Connection Error");
                } else if (error instanceof AuthFailureError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Auth Failure");
                } else if (error instanceof ServerError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Server Error");
                } else if (error instanceof NetworkError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Network Error");
                } else if (error instanceof ParseError) {
                    AlertsUtils.showErrorDialog(getActivity(), "Parse Error");
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
                params.put("first_name",strFirstName );
                params.put("last_name", strLastName);
                params.put("mobile_no", strmobileNum);
                params.put("device", "android");
                params.put("device_id", "");
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