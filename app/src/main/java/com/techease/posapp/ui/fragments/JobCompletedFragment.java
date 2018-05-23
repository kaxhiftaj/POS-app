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
import android.widget.LinearLayout;
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
import com.techease.posapp.ui.activities.MainActivity;
import com.techease.posapp.ui.adapters.RelatedImagesAdapter;
import com.techease.posapp.ui.helpers.DatabaseHelper;
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
import java.util.List;
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
    LinearLayout ivCurrentTime;
    @BindView(R.id.iv_current_location)
    LinearLayout ivCurrentLocation;
    @BindView(R.id.iv_authentication)
    LinearLayout ivAuthentication;
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
    String strFirstImage, strSecondImage, strApiToken, str_JobID, strCurrentDateandTime, strLatitude, strLongitude;
    String strThirdImage="", strFourthImage="", strFifthImage="", strSixthImage="";
    Uri image_uri;
    File fileFirstImage = null;
    File fileSecondImage = null;
    File filethirdImage = null;
    File fileFourthImage = null;
    File fileFifthImage = null;
    File fileSixthImage = null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    double lattitude, longitude;
    LocationManager locationManager;
    Dialog dialog;
    ArrayList<JobImagesModel> related_image_list;
    RelatedImagesAdapter relatedImagesAdapter;
    private static final int REQUEST_LOCATION = 100;
    String strMissionTitle, strMissionDesc, strCompletedJob_id;
    TextView tv_missionTitle, tv_missionDesc;
    RecyclerView rv_relatedImages;
    boolean thirdBoolean=false,fourthBoolean=false,fifthBoolean=false,sixthBoolean=false;
    int count = 0;
    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_job_completed, container, false);
        getActivity().setTitle("Accepted");
        ButterKnife.bind(this, parentView);
        flagFirstImage = false;
        flagSecondImage = false;
        flagLocation = false;
        flagTime = false;
        flagAuthentication = false;
        tv_missionTitle = parentView.findViewById(R.id.missionTitle);
        tv_missionDesc = parentView.findViewById(R.id.missionDesc);

        databaseHelper = new DatabaseHelper(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        strApiToken = sharedPreferences.getString("api_token", "null");
        str_JobID = sharedPreferences.getString("jobID", "null");
        strMissionTitle = sharedPreferences.getString("missionTitle", "null");
        strMissionDesc = sharedPreferences.getString("missionDesc", "null");
        tv_missionTitle.setText(strMissionTitle);
        tv_missionDesc.setText(strMissionDesc);

        //showing job related image
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_image_dialogue);
        rv_relatedImages = dialog.findViewById(R.id.rv_job_completedImages);
        related_image_list = new ArrayList<>();
        ApiCallImages(str_JobID);
        relatedImagesAdapter = new RelatedImagesAdapter(getActivity(), related_image_list);
        rv_relatedImages.setAdapter(relatedImagesAdapter);
        //end

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

//        filethirdImage = new File(strThirdImage);
//        fileFourthImage = new File(strFourthImage);
//        fileFifthImage = new File(strFifthImage);
//        fileSixthImage = new File(strSixthImage);

        ivFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView iv_cancel = dialog.findViewById(R.id.cancel);
//                RecyclerView rv_relatedImages = dialog.findViewById(R.id.rv_job_completedImages);
                LinearLayoutManager layoutManager = new LinearLayoutManager(rv_relatedImages.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv_relatedImages.setLayoutManager(layoutManager);
//                related_image_list = new ArrayList<>();

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

                if (strLatitude != null && strLongitude != null) {
                    check_location.setVisibility(View.VISIBLE);
                }
                flagLocation = true;
            }
        });
        ivCurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataTime();
                if (strCurrentDateandTime != null) {
                    ivCheckTime.setVisibility(View.VISIBLE);
                }

                flagTime = true;
            }
        });
        ivAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraForAuthenthication();
                flagAuthentication = true;

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileSecondImage == null){
                    Toast.makeText(getActivity(), "select at least one image", Toast.LENGTH_SHORT).show();
                }
                else  if (etComments.getText() == null) {
                    Toast.makeText(getActivity(), "Please add comment", Toast.LENGTH_SHORT).show();
                } else if (strLatitude == null || strLongitude == null) {
                    Toast.makeText(getActivity(), "Please confirm your location", Toast.LENGTH_SHORT).show();
                } else if (strCurrentDateandTime == null) {
                    Toast.makeText(getActivity(), "Please confirm your time", Toast.LENGTH_SHORT).show();
                } else if (str_JobID == null || strApiToken == null) {
                    Toast.makeText(getActivity(), "Incorrect job id or token", Toast.LENGTH_SHORT).show();
                }
                else {
                    new UploadFileToServer().execute();
                }

                //data insertion into sqlite
//                boolean insert = databaseHelper.insertData(etComments.getText().toString(),strSecondImage);
//                if(insert){
//                    Toast.makeText(getActivity(), "Data Inserted", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getActivity(), "Not Inserted", Toast.LENGTH_SHORT).show();
//                }
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
                .setPositiveBtnBackground(Color.parseColor("#3aC6ee"))
                .setPositiveBtnText("Camera")
                .setNegativeBtnBackground(Color.parseColor("#3aC6ee"))
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
        startActivityForResult(takePicture, 0);
    }

    private void intentGalleryPic() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    public void showCameraForAuthenthication() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 2);

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
//                        ivSecondImage.setImageBitmap(bm);
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

                        switch (count) {
                            case 0:
                                strSecondImage = getImagePath(image_uri);
                                fileSecondImage = new File(strSecondImage);
                                count++;
                                break;
                            case 1:
                                thirdBoolean = true;
                                if(thirdBoolean){
                                    strThirdImage = getImagePath(image_uri);
                                    filethirdImage = new File(strThirdImage);
                                }
                                count++;
                                break;

                            case 2:
                                fourthBoolean = true;
                                if(fourthBoolean){
                                    strFourthImage = getImagePath(image_uri);
                                    fileFourthImage = new File(strFourthImage);
                                }
                                count++;
                                break;
                            case 3:
                                fifthBoolean = true;
                                if(fifthBoolean){
                                    strFifthImage = getImagePath(image_uri);
                                    fileFifthImage = new File(strFifthImage);
                                }
                                count++;
                                break;

                            case 4:
                                sixthBoolean = true;
                                if(sixthBoolean){
                                    strSixthImage = getImagePath(image_uri);
                                    fileSixthImage = new File(strSixthImage);
                                }
                                count++;
                                break;

                            case 5:
                                Toast.makeText(getActivity(), "Maximum limit is 5", Toast.LENGTH_SHORT).show();
                                break;



                        }

                    }


                } else {
                    Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();

                }
                break;

            case 2: //camera for Authenthication

                Bitmap bm = (Bitmap) imageReturnedIntent.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

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

                if (flagAuthentication) {
                    ivCheckAuthentication.setVisibility(View.VISIBLE);
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

    //getting current date and time
    public void getDataTime() {
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
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;

            } else {
                Toast.makeText(getActivity(), "Unble to Trace your location", Toast.LENGTH_SHORT).show();
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

                                for (int k = 0; k < 1; k++) {
                                    JSONObject imageObject = jsonArray.getJSONObject(k);
                                    String mImage = imageObject.getString("images");
                                    Glide.with(getActivity()).load(mImage).into(ivFirstImage);
                                }


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


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        ProgressDialog progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(getActivity());
            progressBar.setTitle("Uploading");
            progressBar.setMessage("please wait!");
            progressBar.setCancelable(false);
            progressBar.show();

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

                entity.addPart("img1", new FileBody(fileSecondImage));
                if(thirdBoolean){
                    entity.addPart("img2", new FileBody(filethirdImage));
                    thirdBoolean = false;
                }
                if(fourthBoolean){
                    entity.addPart("img3", new FileBody(fileFourthImage));
                    fourthBoolean = false;
                }
                if(fifthBoolean){
                    entity.addPart("img4", new FileBody(fileFifthImage));
                    fifthBoolean = false;
                }
                if(sixthBoolean){
                    entity.addPart("img5", new FileBody(fileSixthImage));
                    sixthBoolean = false;
                }

                Looper.prepare();
                entity.addPart("api_token", new StringBody(strApiToken));
                entity.addPart("job_id", new StringBody(str_JobID));
                entity.addPart("comment", new StringBody(etComments.getText().toString()));
                entity.addPart("latitude", new StringBody(strLatitude));
                entity.addPart("longitude", new StringBody(strLongitude));
                entity.addPart("current_time", new StringBody(strCurrentDateandTime));

                Log.d("show", strApiToken);
                Log.d("show", str_JobID);
                Log.d("show", strLatitude);
                Log.d("show", strLongitude);
                Log.d("show", strCurrentDateandTime);

                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                responseString = EntityUtils.toString(r_entity);

            } catch (ClientProtocolException e) {
                responseString = e.toString();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                responseString = e.toString();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
            Log.d("zma", "api response " + responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            Log.d("resp", message);
            try {
                progressBar.dismiss();
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.popup_layout);
                if (message != null) {
                    JSONObject jsonObject = new JSONObject(message);
                    String result = jsonObject.getString("message");
                    Log.d("message",result);

                    if (result.equals("Job Completed Successfully")){

                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("Job Completed");
                        TextView tv_message = dialog.findViewById(R.id.tv_message);
                        tv_message.setText("your job has been completed");
                        TextView ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        Fragment fragment = new MissionCompletedFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).addToBackStack("XYZ").commit();
                    }
                    else if(result.equals("Distance is out of 200 meter")){
                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("");
                        TextView tv_message = dialog.findViewById(R.id.tv_message);
                        tv_message.setText("Distance is out of 200 meter");
                        TextView ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    else if(result.equals("Time Interval is Greater than 10 minutes")){
                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("");
                        TextView tv_message = dialog.findViewById(R.id.tv_message);
                        tv_message.setText("Time Interval is Greater than\n 10 minutes");
                        tv_message.setGravity(Gravity.CENTER);
                        TextView ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                progressBar.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Server Response");
                builder.setMessage("you got some error");
                builder.setCancelable(true);
                builder.show();
            }
        }
    }


  //skip this for now
    public void firstApiCall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.JOB_COMPLETED
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("first", response);

                if (response.contains("true")) {
                    try {
                        JSONObject json = new JSONObject(response);
                        strCompletedJob_id = json.getString("job_completed_id");
                        new UploadFileToServer().execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.contains("Distance is out of 200 meter")) {
                    Toast.makeText(getActivity(), "Distance is out of 200 meter", Toast.LENGTH_SHORT).show();
                } else if (response.contains("This job already completed")) {
                    Toast.makeText(getActivity(), "This job already completed", Toast.LENGTH_SHORT).show();
                } else if (response.contains("Time Interval is Greater than 10 minutes")) {
                    Toast.makeText(getActivity(), "Time Interval is Greater than 10 minutes", Toast.LENGTH_SHORT).show();
                } else {
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
                params.put("job_id", str_JobID);
                params.put("comment", etComments.getText().toString());
                params.put("latitude", strLatitude);
                params.put("longitude", strLongitude);
                params.put("current_time", strCurrentDateandTime);

                Log.d("show", strApiToken);
                Log.d("show", str_JobID);
                Log.d("show", strLatitude);
                Log.d("show", strLongitude);
                Log.d("show", strCurrentDateandTime);

                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
//end
    }


}



