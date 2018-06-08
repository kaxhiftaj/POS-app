package com.techease.posapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    String token,user_id,userProfileImage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView first_name,last_name,email,phone_no;
    Button btnEdit_profile;
    LinearLayout firstLastNames_layout,setNames_Layout;
    CircleImageView iv_profile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);
//        getActivity().setTitle("User  Info");
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView tv_toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView tv_toolbarSave = toolbar.findViewById(R.id.toolbar_save);
        tv_toolbarSave.setVisibility(View.GONE);
        tv_toolbarTitle.setText("Profile Info");
        iv_profile = view.findViewById(R.id.edit_profile_image);
        setNames_Layout = view.findViewById(R.id.setName_layout);
        firstLastNames_layout = view.findViewById(R.id.firstLastNames_layout);
        first_name = (TextView) view.findViewById(R.id.first_name);
        last_name = (TextView)view.findViewById(R.id.last_name);
        email = (TextView)view.findViewById(R.id.email);
        phone_no = (TextView)view.findViewById(R.id.phone_no);
        btnEdit_profile = (Button) view.findViewById(R.id.edit_profile);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("api_token", "");
        user_id = sharedPreferences.getString("user_id","");
        userProfileImage  = sharedPreferences.getString("user_image","");
        Glide.with(getActivity()).load(userProfileImage).into(iv_profile);
        apicall();

        setNames_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstLastNames_layout.setVisibility(View.VISIBLE);
            }
        });
        btnEdit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).addToBackStack("").commit();
            }
        });
        return view;
    }

    private void apicall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_PROFILE
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject temp = jsonObject.getJSONObject("user_data");
                    String strFirstName = temp.getString("first_name");
                    String strLastName = temp.getString("last_name");
                    String strEmail = temp.getString("email");
                    String strPhoneno = temp.getString("mobile_no");
                    Log.d("first", strFirstName);

                    first_name.setText(strFirstName);
                    last_name.setText(strLastName);
                    email.setText(strEmail);
                    phone_no.setText(strPhoneno);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "got some error", Toast.LENGTH_SHORT).show();
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
                params.put("user_id",user_id);
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
