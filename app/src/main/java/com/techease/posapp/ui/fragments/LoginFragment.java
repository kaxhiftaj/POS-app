package com.techease.posapp.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LoginFragment extends Fragment {


    @BindView(R.id.signin_email)
    EditText signin_email;


    @BindView(R.id.signin_pass)
    EditText signin_pass;

    @BindView(R.id.signin_confirmpass)
    EditText signin_confirmpass;

    @BindView(R.id.signin)
    Button signin;

    @BindView(R.id.signin_fb)
    Button signin_fb;

    @BindView(R.id.signin_google)
    Button signin_google;

    @BindView(R.id.new_user)
    TextView new_user ;

    String strEmail, strPassword ;
    Unbinder unbinder ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    android.support.v7.app.AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, v);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new RegsiterFragment();
               // FragmentManager fm = getFragmentManager();
               // FragmentTransaction transaction = fm.beginTransaction();
               // transaction.replace(R.id.fragment_container, fragment);
               // transaction.addToBackStack("tag").commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack("").commit();
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDataInput();
            }
        });

//        tv_forgot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Fragment fragment = new ForgetPassFragment();
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction transaction = fm.beginTransaction();
//                transaction.replace(R.id.fragment_container, fragment);
//                transaction.addToBackStack("tag").commit();
//            }
//        });


        return v ;
    }



    public void onDataInput() {
        strEmail = signin_email.getText().toString();
        strPassword = signin_pass.getText().toString();
        if ((!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches())) {
            signin_email.setError("Please enter valid email id");
        } else if (strPassword.equals("")) {
            signin_pass.setError("Please enter your password");
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
                Log.d("zma log ", response);

                if (response.contains("true")) {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response).getJSONObject("user_data");
                        String api_token = jsonObject.getString("api_token");
                        String user_id = jsonObject.getString("id");
                        String user_email=jsonObject.getString("email");
                        editor.putString("api_token", api_token);
                        editor.putString("user_id", user_id);
                        editor.putString("user_email",user_email);
                        editor.commit();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();


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
                Log.d("email",strEmail);
                Log.d("passwd",strPassword);
                params.put("email", strEmail);
                params.put("password", strPassword);
                //   params.put("Accept", "application/json");
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
