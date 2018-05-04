package com.techease.posapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.gson.JsonObject;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.techease.posapp.R;
import com.techease.posapp.ui.adapters.RelatedImagesAdapter;
import com.techease.posapp.ui.models.JobImagesModel;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.HTTPMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class JobCompletedFragment extends Fragment {

    View parentView;
    @BindView(R.id.iv_first_image)
    ImageView ivFirstImage;
    @BindView(R.id.iv_second_image)
    ImageView ivSecondImage;
    @BindView(R.id.iv_current_time)
    ImageView ivCurrentTime;
    @BindView(R.id.iv_current_location)
    ImageView ivCurrentLocation;
    @BindView(R.id.iv_authentication)
    ImageView ivAuthentication;
    @BindView(R.id.iv_check_authentication)
    ImageView ivCheckAuthentication;
    @BindView(R.id.iv_current_check_time)
    ImageView ivCheckTime;
    @BindView(R.id.iv_current_check_location)
    ImageView check_location;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.et_comments)
    EditText etComments;
    Boolean flagFirstImage, flagSecondImage, flagLocation, flagTime, flagAuthentication;
    String strFirstImage, strSecondImage, strApiToken,str_JobID,strCurrentDateandTime, strLatitude, strLongitude;
    Uri image_uri;
    File fileFirstImage, fileSecondImage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    double lattitude, longitude;
    LocationManager locationManager;
    String completedJob_id;
    Dialog dialog;
    ArrayList<JobImagesModel> related_image_list;
    RelatedImagesAdapter relatedImagesAdapter;
    private static final int REQUEST_LOCATION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_job_completed, container, false);
        ButterKnife.bind(this, parentView);
        flagFirstImage = false;
        flagSecondImage = false;
        flagLocation = false;
        flagTime = false;
        flagAuthentication = false;

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        strApiToken = sharedPreferences.getString("api_token","null");
        str_JobID = sharedPreferences.getString("jobID", "null");

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        ivFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_image_dialogue);
                ImageView iv_cancel = dialog.findViewById(R.id.cancel);
                RecyclerView rv_relatedImages = dialog.findViewById(R.id.rv_job_completedImages);
                LinearLayoutManager layoutManager = new LinearLayoutManager(rv_relatedImages.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv_relatedImages.setLayoutManager(layoutManager);
                related_image_list = new ArrayList<>();

                ApiCallImages(str_JobID);
                relatedImagesAdapter = new RelatedImagesAdapter(getActivity(), related_image_list);
                rv_relatedImages.setAdapter(relatedImagesAdapter);
                dialog.show();
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        ivSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fancydialog();
                flagSecondImage = true;
                flagFirstImage = false;
            }
        });
        ivCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strLatitude = String.valueOf(lattitude);
                strLongitude = String.valueOf(longitude);

                if(strLatitude != null && strLongitude != null){
                    check_location.setVisibility(View.VISIBLE);
                }
                flagLocation = true;
            }
        });
        ivCurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataTime();
                if(strCurrentDateandTime != null){
                    ivCheckTime.setVisibility(View.VISIBLE);
                }

                flagTime = true;
            }
        });
        ivAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCheckAuthentication.setVisibility(View.VISIBLE);
                flagAuthentication = true;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etComments.getText().toString() == null
                        || strCurrentDateandTime == null || str_JobID == null || strLongitude == null || strLatitude == null){
                    Toast.makeText(getActivity(), "Missing Some Parameter", Toast.LENGTH_SHORT).show();
                }
                else {
                    firstApiCall();
                }

            }
        });
        return parentView;
    }

    private void fancydialog() {
        new FancyAlertDialog.Builder(getActivity())
                .setTitle("Upload Images")
                .setBackgroundColor(Color.parseColor("#3aC6ee"))  //Don't pass R.color.colorvalue
                .setMessage("Do you really want to take picture ?")
                .setNegativeBtnText("Gallery")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))
                .setPositiveBtnText("Camera")
                .setNegativeBtnBackground(Color.parseColor("#FF4081"))
                .isCancellable(true)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        intentCameraPic();
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        intentGalleryPic();
                    }
                })
                .build();
    }

    private void intentCameraPic() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);//zero can be replaced with any action code
    }

    private void intentGalleryPic() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {


            case 0://camera

                if (resultCode == RESULT_OK) {

                    Bitmap bm = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    File sourceFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pos/img.jpg");


                    FileOutputStream fo;
                    try {
                        sourceFile.createNewFile();
                        fo = new FileOutputStream(sourceFile);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (flagFirstImage) {

                        ivFirstImage.setImageBitmap(bm);
                        strFirstImage = sourceFile.getAbsolutePath().toString();
                        fileFirstImage = new File(strFirstImage);

                    }
                    if (flagSecondImage) {
                        ivSecondImage.setImageBitmap(bm);
                        strSecondImage = sourceFile.getAbsolutePath().toString();
                        fileSecondImage = new File(strSecondImage);
                    }


                } else {

                    Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();

                }
                break;


            case 1://gallery
                if (resultCode == RESULT_OK) {
                    image_uri = imageReturnedIntent.getData();
                    if (flagFirstImage) {

                        ivFirstImage.setImageURI(image_uri);
                        strFirstImage = getImagePath(image_uri);
                        fileFirstImage = new File(strFirstImage);

                    }
                    if (flagSecondImage) {
                        ivSecondImage.setImageURI(image_uri);
                        strSecondImage = getImagePath(image_uri);
                        fileSecondImage = new File(strSecondImage);
                    }


                } else {
                    Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();

                }
                break;

        }
    }


    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            String responseString;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Configuration.JOB_COMPLETED);
            try {
                HTTPMultiPartEntity entity = new HTTPMultiPartEntity(
                        new HTTPMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) 100) * 100));

                            }
                        });
              //  entity.addPart("files", new FileBody(fileFirstImage));
                entity.addPart("files", new FileBody(fileSecondImage));
                Looper.prepare();
                entity.addPart("api_token", new StringBody(strApiToken));
                entity.addPart("job_id", new StringBody(completedJob_id));
//                entity.addPart("comment", new StringBody(etComments.getText().toString()));
//                entity.addPart("latitude", new StringBody(strLatitude));
//                entity.addPart("longitude", new StringBody(strLongitude));
//                entity.addPart("current_time", new StringBody(strCurrentDateandTime));

                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                responseString = EntityUtils.toString(r_entity);

                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                responseString = e.toString();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                responseString = e.toString();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
            Log.d("zma", "api response "+ responseString);

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Api Response");
            builder.setMessage(s);
            builder.setCancelable(true);
            builder.show();
            Toast.makeText(getActivity(), "Done Successfully"+s, Toast.LENGTH_SHORT).show();
            Log.d("comp",s);
        }
    }

    //getting current date and time
    public void getDataTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        strCurrentDateandTime = formattedDate;
    }
    //end

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


    public void firstApiCall(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.JOB_COMPLETED
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response",response);

                if(response.contains("true")){
                    try {
                        JSONObject json = new JSONObject(response);
                        completedJob_id = json.getString("job_completed_id");
                        new UploadFileToServer().execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
               else if(response.contains("Distance is out of 200 km")){
                    Toast.makeText(getActivity(), "Distance is out of 200 km", Toast.LENGTH_SHORT).show();
                }
                else if(response.contains("This job already completed")){
                    Toast.makeText(getActivity(), "This job already completed", Toast.LENGTH_SHORT).show();
                }
                else if(response.contains("Time Interval is Greater than 10 minutes")){
                    Toast.makeText(getActivity(), "Time Interval is Greater than 10 minutes", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "You got some error", Toast.LENGTH_SHORT).show();
                }



            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", strApiToken);
                params.put("job_id",str_JobID);
                params.put("comment",etComments.getText().toString());
                params.put("latitude",strLatitude);
                params.put("longitude",strLongitude);
                params.put("current_time",strCurrentDateandTime);
//                params.put("latitude","34.168138519353846");
//                params.put("longitude","71.61093524374996");
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }

    //apiCall for job related images
    private void ApiCallImages(final String job_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.JOB_RELATED_IMAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("images");
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject temp = jsonArray.getJSONObject(j);
                                JobImagesModel jobImagesModel = new JobImagesModel();
                                String image = temp.getString("images");
                                jobImagesModel.setJob_images(image);
                                   String[] zma = {image};

                                   for (int k = 0; k<1; k++){
                                       JSONObject imageObject = jsonArray.getJSONObject(k);
                                       String mImage = imageObject.getString("images");
                                       Glide.with(getActivity()).load(mImage).into(ivFirstImage);
                                   }

//                                   for(int count=0;count<zma.length;count++){
//                                       Log.d("arr",zma[0]);
//                                       Toast.makeText(getActivity(), zma[1], Toast.LENGTH_SHORT).show();
//                                       }
                                       
                                jobImagesModel.setJob_images(image);
                                related_image_list.add(jobImagesModel);

                            }
                            relatedImagesAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), String.valueOf(e.getCause()), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), String.valueOf(error.getCause()), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("khan", job_id);
                params.put("job_id", job_id);
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }
    //end


}



