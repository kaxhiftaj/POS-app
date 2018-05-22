package com.techease.posapp.ui.activities;

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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techease.posapp.R;
import com.techease.posapp.ui.fragments.HomeFragment;
import com.techease.posapp.ui.fragments.JobCompletedFragment;
import com.techease.posapp.ui.fragments.LoginFragment;
import com.techease.posapp.ui.fragments.MissionCompletedFragment;
import com.techease.posapp.ui.fragments.RegsiterFragment;
import com.techease.posapp.ui.fragments.UserAcceptedFragment;
import com.techease.posapp.ui.fragments.UserProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();
        setTitle("HOME");

<<<<<<< HEAD
        sharedPreferences = this.getSharedPreferences(Configuration.MY_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        str_mobileNo = sharedPreferences.getString("mobile_no", "");
        strToken = sharedPreferences.getString("api_token", "null");
        str_firstName = sharedPreferences.getString("user_firstName","");
        str_lastName = sharedPreferences.getString("user_lastName","");
        str_image = sharedPreferences.getString("user_image","");

=======
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tv_edit = headerView.findViewById(R.id.et_edit);
<<<<<<< HEAD

        Glide.with(MainActivity.this).load(str_image).into(profile_image);
        tv_firstName.setText(str_firstName + " " + str_lastName);
        tv_mobile_no.setText(mobile_no);

=======
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UserProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).addToBackStack("abc").commit();
                setTitle("My Account");
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        }
        else if (id == R.id.nav_completed) {
          fragment = new MissionCompletedFragment();
        } else if (id == R.id.nav_profile_Info ){
           fragment = new UserProfileFragment();
        } else if (id == R.id.nav_privacy ){

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_about) {

        }else if (id == R.id.nav_invite) {
        }
        else if (id == R.id.nav_logout) {
           startActivity(new Intent(MainActivity.this,FullScreenActivity.class));
        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).addToBackStack("tag").commit();
        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
<<<<<<< HEAD


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
=======
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
}
