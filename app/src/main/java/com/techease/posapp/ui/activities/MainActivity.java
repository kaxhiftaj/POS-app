package com.techease.posapp.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.transition.Slide;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.techease.posapp.R;
import com.techease.posapp.ui.adapters.JobsAdapter;
import com.techease.posapp.ui.fragments.EditProfileFragment;
import com.techease.posapp.ui.fragments.HomeFragment;
import com.techease.posapp.ui.fragments.JobCompletedFragment;
import com.techease.posapp.ui.fragments.LoginFragment;
import com.techease.posapp.ui.fragments.MissionCompletedFragment;
import com.techease.posapp.ui.fragments.RegsiterFragment;
import com.techease.posapp.ui.fragments.UserAcceptedFragment;
import com.techease.posapp.ui.fragments.UserProfileFragment;
import com.techease.posapp.ui.fragments.VerificationCodeFragment;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.InternetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String str_firstName, str_lastName, str_mobileNo, str_image, strToken;
    String mobile_no;
    ImageView profile_image;
    TextView tv_firstName, tv_mobile_no;
    android.support.v7.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();
        setTitle("HOME");

        sharedPreferences = this.getSharedPreferences(Configuration.MY_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        str_mobileNo = sharedPreferences.getString("mobile_no", "");
        strToken = sharedPreferences.getString("api_token", "null");

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        tv_firstName = headerView.findViewById(R.id.tv_Firstname);
        tv_mobile_no = headerView.findViewById(R.id.tv_phone);
        profile_image = headerView.findViewById(R.id.iv_profile);
        TextView tv_edit = headerView.findViewById(R.id.et_edit);

        if (InternetUtils.isNetworkConnected(MainActivity.this)) {
            apiCall();
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(MainActivity.this);
            alertDialog.show();

        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).addToBackStack("abc").commit();
                drawer.closeDrawer(Gravity.LEFT);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).addToBackStack("abc").commit();
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            apiCall();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_missions) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_user_accepted) {
            fragment = new UserAcceptedFragment();
        } else if (id == R.id.nav_completed) {
            fragment = new MissionCompletedFragment();
        } else if (id == R.id.nav_profile_Info) {
            fragment = new EditProfileFragment();
        } else if (id == R.id.nav_privacy) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_invite) {
        } else if (id == R.id.nav_logout)
            {
            startActivity(new Intent(MainActivity.this, FullScreenActivity.class));
                MainActivity.this.finish();
        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).addToBackStack("tag").commit();
        item.setChecked(true);
//        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCall();
    }

    @Override
    protected void onPause() {
        super.onPause();
        apiCall();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        apiCall();
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiCall();
    }

    public void apiCall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_LOGIN
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("main", response);
                editor.putString("response", response).commit();
                if (response.contains("false")) {
                    if (alertDialog != null)
                        alertDialog.dismiss();

                    Fragment fragment = new VerificationCodeFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else if (response.contains("true")) {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("user_data");

                        str_firstName = jsonObject.getString("first_name");
                        str_lastName = jsonObject.getString("last_name");
                        mobile_no = jsonObject.getString("mobile_no");
                        str_image = jsonObject.getString("user_img");


                        Glide.with(MainActivity.this).load(str_image).into(profile_image);
                        tv_firstName.setText(str_firstName + " " + str_lastName);
                        tv_mobile_no.setText(mobile_no);

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
                        AlertsUtils.showErrorDialog(MainActivity.this, message);

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
                    AlertsUtils.showErrorDialog(MainActivity.this, "Auth Failure");
                } else if (error instanceof ServerError) {
                    AlertsUtils.showErrorDialog(MainActivity.this, "Server Error");
                } else if (error instanceof NetworkError) {
                    AlertsUtils.showErrorDialog(MainActivity.this, "Network Error");
                } else if (error instanceof ParseError) {
                    AlertsUtils.showErrorDialog(MainActivity.this, "Parsing Error");
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
                params.put("mobile_no", str_mobileNo);

                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }
}
