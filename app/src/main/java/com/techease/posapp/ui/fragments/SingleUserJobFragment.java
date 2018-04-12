package com.techease.posapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.techease.posapp.R;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.InternetUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class SingleUserJobFragment extends Fragment {
    MapView user_mapView;
    GoogleMap user_googleMap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    android.support.v7.app.AlertDialog alertDialog;
    String apiToken, user_id, job_id;
    LocationManager locationManager;
    double distance;
    private static final int REQUEST_LOCATION = 1;
    double lattitude, longitude;
    TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_user_job, container, false);
        textView = view.findViewById(R.id.distance);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        apiToken = sharedPreferences.getString("api_token", "null");
        user_id = sharedPreferences.getString("user_id", "null");
        job_id = sharedPreferences.getString("job_id", "null");
        Log.d("zma_token",apiToken);
        Log.d("zma_user_id",user_id);
        Log.d("job_id",job_id);

        user_mapView = (MapView) view.findViewById(R.id.UsermapView);
        user_mapView.onCreate(savedInstanceState);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        if (InternetUtils.isNetworkConnected(getActivity())) {
            user_mapView.onCreate(savedInstanceState);
            user_mapView.onResume();

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        user_mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                user_googleMap = googleMap;
                String Lat = sharedPreferences.getString("selectedLatitude", "");
                String lng = sharedPreferences.getString("selectedLongitude", "");
                final String title = sharedPreferences.getString("title", "");
                final String description = sharedPreferences.getString("descp", "");

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                user_googleMap.setMyLocationEnabled(true);
                    LatLng jobLatLng = new LatLng(Double.parseDouble(Lat), Double.parseDouble(lng));
                    LatLng currentlocation = new LatLng(lattitude,longitude);
                //finding the distance between two location
                Location locationA = new Location("pointA");
                locationA.setLatitude(lattitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("pointB");
                locationB.setLatitude(Double.parseDouble(Lat));
                locationB.setLongitude(Double.parseDouble(lng));
                distance = locationA.distanceTo(locationB);
                CalculationByDistance(jobLatLng,currentlocation);
                //textView.setText(String.valueOf("Total Distance = "+distance));
                //end

                    user_googleMap.addMarker(new MarkerOptions().position(jobLatLng).title(title).snippet(description));
                    user_googleMap.addMarker(new MarkerOptions().position(currentlocation).title("Current Location").snippet("i am here"));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentlocation,9);
                    user_googleMap.animateCamera(cameraUpdate);
                    user_googleMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(lattitude,longitude), new LatLng(Double.parseDouble(Lat), Double.parseDouble(lng)))
                            .width(5).color(Color.BLUE));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if(marker.getTitle().equals(title)){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(title);
                                builder.setMessage(description);
                                builder.setCancelable(false);
                                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        apiCall();
                                    }
                                });
                                builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getActivity(),"you Rejected",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.show();
                            }
                            else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                builder1.setTitle("Current Location");
                                builder1.setMessage("I am here");
                                builder1.setCancelable(true);
                                builder1.show();
                            }
                            return false;
                        }
                    });

                }
            });

            return view;
        }

        private void apiCall(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.JOB_ACCEPTED,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
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
                    Log.d("zma_job_id",job_id);
                    params.put("api_token", apiToken);
                    params.put("job_id",job_id);
                    params.put("users_id",user_id);
                    return params;
                }

            };
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(stringRequest);

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

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        // Toast.makeText(this, "Radius Value" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec, Toast.LENGTH_SHORT).show();
        textView.setText(" KiloMeter =" + kmInDec );
        return Radius * c;
    }
    }
