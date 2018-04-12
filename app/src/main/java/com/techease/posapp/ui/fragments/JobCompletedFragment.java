package com.techease.posapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.ui.models.MySingleton;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class JobCompletedFragment extends Fragment {
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String token;
    ImageView image1,image2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_completed, container, false);
        image1 = view.findViewById(R.id.image1);
        image2 = view.findViewById(R.id.image2);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image1.setImageBitmap(bitmap);
                setSelectImage();
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image2.setImageBitmap(bitmap);
                setSelectImage();
            }
        });

        return view;
    }

    public void setSelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_REQUEST && resultCode ==  RESULT_OK && data != null){
          Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),path);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_URL+"jobs"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("all",response);
                Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
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
                params.put("job_id","1");
                params.put("comment","bla bla");
                params.put("latitude","44.90");
                params.put("longitude","78.00");
                params.put("current_time","2018-04-11 10:31:27");
                params.put("image1",imageToString(bitmap));
                params.put("image2",imageToString(bitmap));
                return params;
            }
        };
      //  MySingleton.getmInstance(getActivity()).addToRequestQue(stringRequest);

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagesbyte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagesbyte,Base64.DEFAULT);
    }
}
