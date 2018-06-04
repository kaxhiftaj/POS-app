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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.HEAD;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String str_mobileNo,strToken,str_firstName,str_lastName,str_image,str_mobile_no;
    ImageView profile_image;
    TextView tv_firstName,tv_mobile_no;


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
        str_firstName = sharedPreferences.getString("user_firstName","");
        str_lastName = sharedPreferences.getString("user_lastName","");
        str_image = sharedPreferences.getString("user_image","");

        Log.d("umer",str_mobileNo);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        profile_image = headerView.findViewById(R.id.imageView);
        TextView tv_edit = headerView.findViewById(R.id.et_edit);
        tv_firstName = headerView.findViewById(R.id.et_name);
        tv_mobile_no = headerView.findViewById(R.id.et_phone);


        Glide.with(MainActivity.this).load(str_image).into(profile_image);
        tv_firstName.setText(str_firstName + " " + str_lastName);
        tv_mobile_no.setText(str_mobileNo);

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).addToBackStack("abc").commit();
                setTitle("My Account");
                drawer.closeDrawer(Gravity.LEFT);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).addToBackStack("abc").commit();
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
//        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
