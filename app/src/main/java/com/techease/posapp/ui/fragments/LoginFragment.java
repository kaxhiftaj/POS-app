package com.techease.posapp.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.hbb20.CountryCodePicker;
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


    @BindView(R.id.signin_phone_no)
    EditText etSignin_phone;

    @BindView(R.id.signin)
    Button signin;
    TextView tvSignIn;
    Typeface typeface;
    String strPhone;
    Unbinder unbinder ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    android.support.v7.app.AlertDialog alertDialog;
    String countryCodeAndroid = "91";
    CountryCodePicker ccp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, v);


        ccp = (CountryCodePicker) v.findViewById(R.id.country_code);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDataInput();
            }
        });
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCodeAndroid = ccp.getSelectedCountryCode();
            }
        });

        return v ;
    }

    public void onDataInput() {
        strPhone = etSignin_phone.getText().toString().trim();
        editor.putString("phone_no",strPhone).commit();
        if (strPhone.equals("")) {
            etSignin_phone.setError("Please enter valid phone number");
        }/* else if (strFirstName.equals("")) {
            etFistName.setError("Please enter your FirstName");
        }else if (strLastName.equals("")) {
                etLastName.setError("Please enter your Last Name");
        }*/ else {
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
                editor.putString("response",response).commit();
                if(response.contains("false")){
                    if (alertDialog != null)
                        alertDialog.dismiss();

                    Fragment fragment = new VerificationCodeFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack("").commit();
                }

                else if (response.contains("true")) {
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
                        Fragment fragment = new VerificationCodeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

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
                params.put("mobile_no",strPhone);

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
