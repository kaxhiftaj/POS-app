package com.techease.posapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MapFragment extends Fragment {

    @BindView(R.id.mapView)
    MapView mMapView;
    private GoogleMap mMap;
    Unbinder unbinder;
    private GoogleMap googleMap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String token;
    LocationManager locationManager;
    android.support.v7.app.AlertDialog alertDialog;
    double lattitude, longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        token = sharedPreferences.getString("api_token", "");

        unbinder = ButterKnife.bind(this,v );

        locationManager = (LocationManager)getActivity(). getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        if(InternetUtils.isNetworkConnected(getActivity()))
        {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    editor.putString("currentLat", String.valueOf(longitude)).commit();
                    editor.putString("currentLng",String.valueOf(lattitude)).commit();
                    LatLng latLng = new LatLng(lattitude,longitude);
                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    LatLng sydney = new LatLng(lattitude,longitude );
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("My Current Location").snippet("This is My Current Location"));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney,12);
                    googleMap.animateCamera(cameraUpdate);

                }
            });
        }
        else
        {
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        apicall();
        return v;
    }

    public void showMarker(String lat, String lng, final String title, final String description) {
        double latt = Double.parseDouble(lat);
        double lngg = Double.parseDouble(lng);
        LatLng apiLatLng = new LatLng(latt, lngg);
        final Marker my_marker = googleMap.addMarker(new MarkerOptions().position(apiLatLng).title(title).snippet(description));
        googleMap.setInfoWindowAdapter(new InfoWindowCustom(getActivity()));
      /*  googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("snippet",my_marker.getSnippet());
                AlertsUtils.showErrorDialog(getActivity(),my_marker.getSnippet());
                return false;
            }
        });*/


    }
    @Override
    public void onResume() {
        super.onResume();
//        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //getting current location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;


            }else{

                Toast.makeText(getActivity(),"Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
 
    private void apicall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_URL+"jobs"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("200")) {
                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArr = jsonObject.getJSONArray("jobs");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject temp = jsonArr.getJSONObject(i);

                            JobsModel model = new JobsModel();
                            String job_title = temp.getString("job_title");
                            String description = temp.getString("description");
                            String latitude = temp.getString("latitude");
                            String longitude = temp.getString("longitude");

                            model.setJob_title(job_title);
                            model.setJob_desc(description);
                            model.setLatitude(latitude);
                            model.setLongitude(longitude);
                            showMarker(latitude,longitude,job_title,description);
                        
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (alertDialog != null)
                            alertDialog.dismiss();
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
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }

    public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {
        Context context;
        LayoutInflater inflater;
        public InfoWindowCustom(Context context) {
            this.context = context;
        }
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.custom_info_window, null);

          //  TextView title = (TextView) v.findViewById(R.id.myTitle);
          //  TextView subtitle = (TextView) v.findViewById(R.id.description);
          //  Button accept = v.findViewById(R.id.accept);
          //  Button reject = v.findViewById(R.id.reject);
          //  title.setText(marker.getTitle());
          //  subtitle.setText(marker.getSnippet());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(marker.getTitle());
            builder.setMessage(marker.getSnippet());
           // builder.setView(R.layout.custom_info_window);
            builder.setCancelable(false);
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "you Accepted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   Toast.makeText(getActivity(),"you Rejected",Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
            return v;
        }
    }

}

