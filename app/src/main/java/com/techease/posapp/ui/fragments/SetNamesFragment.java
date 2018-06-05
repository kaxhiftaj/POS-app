package com.techease.posapp.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.techease.posapp.R;
import com.techease.posapp.ui.activities.MainActivity;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;
import com.techease.posapp.utils.HTTPMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class SetNamesFragment extends Fragment {
    EditText et_set_FirstName, et_setLastName, et_setGender,et_setDob;
    Button btn_Go;
    String strFirstName, strLastName, strMobile, strImage, strGender,strDob;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    android.support.v7.app.AlertDialog alertDialog;
    CircleImageView iv_profileImage;
    File fileFirstImage = null;
    Boolean flagFirstImage = false;
    Uri image_uri;
    @BindView(R.id.profileImage_layout)
    LinearLayout profileImage_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_names, container, false);
        ButterKnife.bind(this, view);
        et_set_FirstName = view.findViewById(R.id.set_first_name);
        et_setLastName = view.findViewById(R.id.set_last_name);
        et_setGender = view.findViewById(R.id.set_gender);
        et_setDob = view.findViewById(R.id.set_dob);
        btn_Go = (Button) view.findViewById(R.id.go);
        iv_profileImage = view.findViewById(R.id.set_profileImage);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        strMobile = sharedPreferences.getString("phone_no", "");

        profileImage_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                takeImage();
                fancydialog();
                flagFirstImage = true;
            }
        });

        btn_Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataInput();
            }
        });

        return view;
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
        startActivityForResult(pickPhoto, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {


            case 0://camera
                if (resultCode == RESULT_OK) {

                    Bitmap bm = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    File sourceFile = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");

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
                        iv_profileImage.setImageBitmap(bm);
                        strImage = sourceFile.getAbsolutePath().toString();
                        fileFirstImage = new File(strImage);

                    }

                } else {

                    Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();

                }
                break;

            case 100://gallery
                if (resultCode == RESULT_OK) {
                    image_uri = imageReturnedIntent.getData();
                    if (flagFirstImage) {
                        iv_profileImage.setImageURI(image_uri);
                        strImage = getImagePath(image_uri);
                        fileFirstImage = new File(strImage);

                    }

                }
        }
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }


    public void onDataInput() {
        strFirstName = et_set_FirstName.getText().toString().trim();
        strLastName = et_setLastName.getText().toString().trim();
        strGender = et_setGender.getText().toString().trim();
        strDob = et_setDob.getText().toString().trim();

        if (strFirstName.equals("")) {
            et_set_FirstName.setError("Please enter your First Name");

        } else if (strLastName.equals("")) {
            et_setLastName.setError("Please enter your Last Name");
        } else {
            if (alertDialog == null)
                alertDialog = AlertsUtils.createProgressDialog(getActivity());
            alertDialog.show();
            new UploadFileToServer().execute();
        }
    }

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
            HttpPost httppost = new HttpPost(Configuration.USER_LOGIN);
            try {
                HTTPMultiPartEntity entity = new HTTPMultiPartEntity(
                        new HTTPMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) 100) * 100));

                            }
                        });
                entity.addPart("userfile", new FileBody(fileFirstImage));
                Looper.prepare();
                entity.addPart("first_name", new StringBody(strFirstName));
                entity.addPart("last_name", new StringBody(strLastName));
                entity.addPart("mobile_no", new StringBody(strMobile));
                entity.addPart("sex", new StringBody(strGender));
                entity.addPart("dob",new StringBody(strDob));

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
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response.contains("true")) {
                try {
                    if (alertDialog != null)
                        alertDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("user_data");
                    String api_token = jsonObject.getString("api_token");
                    int user_id = jsonObject.getInt("user_id");
                    String strFirstName = jsonObject.getString("first_name");
                    String strLastName = jsonObject.getString("last_name");
                    String strProfileImg = jsonObject.getString("userImg");
                    String strUserMobile = jsonObject.getString("mobile_no");
                    String strUserDob = jsonObject.getString("dob");

                    editor.putString("api_token", api_token);
                    editor.putString("user_id", String.valueOf(user_id));
                    editor.putString("user_firstName", strFirstName);
                    editor.putString("user_lastName", strLastName);
                    editor.putString("user_image", strProfileImg);
                    editor.putString("mobile_no", strUserMobile);
                    editor.putString("user_dob",strUserDob);

                    editor.commit();
                    startActivity(new Intent(getActivity(), MainActivity.class));

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
    }
}