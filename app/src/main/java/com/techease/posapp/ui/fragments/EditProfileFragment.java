package com.techease.posapp.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techease.posapp.R;
import com.techease.posapp.ui.models.JobsModel;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {
    EditText ed_FirstName, ed_LastName, ed_Email,et_sex,et_dob;
    String api_token, user_id, strFirstName, strLastName, strProfileImage,strEmail,strSex,strDob;
    Button btn_save;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    LinearLayout showNamesLayout, setFirstNameLayout,setLastNameLayout;
    File profileImage_file;
    CircleImageView ivProfile;
    Uri image_uri;
    String str_firstName,str_lastName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        getActivity().setTitle("User Info");
        ed_FirstName = (EditText) view.findViewById(R.id.edit_firstName);
        ed_LastName = (EditText) view.findViewById(R.id.edit_lastName);
        ed_Email = (EditText) view.findViewById(R.id.edit_email);
        et_sex = view.findViewById(R.id.edit_sex);
        et_dob = view.findViewById(R.id.edit_dob);
        btn_save = view.findViewById(R.id.edit_done);
        ivProfile = view.findViewById(R.id.set_profilePictr);
        setFirstNameLayout = view.findViewById(R.id.edit_FirstNameLayout);
        setLastNameLayout = view.findViewById(R.id.edit_lastNameLayout);
        showNamesLayout = view.findViewById(R.id.show_name);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        api_token = sharedPreferences.getString("api_token", "");
        user_id = sharedPreferences.getString("user_id", "");

        apicallProfile();

        showNamesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstNameLayout.setVisibility(View.VISIBLE);
                setLastNameLayout.setVisibility(View.VISIBLE);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryPic();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileImage_file == null && str_firstName == null){
                    dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.popup_layout);
                    TextView tvOK = dialog.findViewById(R.id.ok);
                    tvOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }


                else {
                     new setProfile().execute();
                }
            }
        });
        return view;
    }

    private void galleryPic() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            image_uri = data.getData();
            ivProfile.setImageURI(image_uri);
            strProfileImage = getImagePath(image_uri);
            profileImage_file = new File(strProfileImage);

        }
    }

    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    private void apicallProfile() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_PROFILE
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject temp = jsonObject.getJSONObject("user_data");
                    strFirstName = temp.getString("first_name");
                     strLastName = temp.getString("last_name");
                     strEmail = temp.getString("email");
                     strSex = temp.getString("sex");
                     strDob = temp.getString("dob");


                    ed_FirstName.setText(strFirstName);
                    ed_LastName.setText(strLastName);
                    ed_Email.setText(strEmail);
                    et_sex.setText(strSex);
                    et_dob.setText(strDob);

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
                params.put("api_token", api_token);
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

    private class setProfile extends AsyncTask<Void, Integer, String> {
        ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(getActivity());
            progressBar.setTitle("Updating your Profile");
            progressBar.setMessage("please wait!");
            progressBar.setCancelable(false);
            progressBar.show();


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            str_firstName = ed_FirstName.getText().toString().trim();
            str_lastName = ed_LastName.getText().toString().trim();
            final String str_Email = ed_Email.getText().toString().trim();
            final String str_sex = et_sex.getText().toString().trim();
            final String str_dob = et_dob.getText().toString().trim();

            String responseString;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Configuration.USER_EDIT_PROFILE);
            try {
                HTTPMultiPartEntity entity = new HTTPMultiPartEntity(
                        new HTTPMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) 100) * 100));

                            }
                        });

                entity.addPart("userfile", new FileBody(profileImage_file));
                Looper.prepare();
                entity.addPart("user_id", new StringBody(user_id));
                entity.addPart("first_name", new StringBody(str_firstName));
                entity.addPart("last_name", new StringBody(str_lastName));
                entity.addPart("sex", new StringBody(str_sex));
                entity.addPart("dob", new StringBody(str_dob));
                entity.addPart("email", new StringBody(str_Email));
                entity.addPart("api_token", new StringBody(api_token));


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
                    Log.d("message", result);

                    if (result.equals("User Updated")) {

                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("Profile Updated");
                        TextView tv_message = dialog.findViewById(R.id.tv_message);
                        tv_message.setText("your profile has been updated");
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
                builder.setMessage("Time out Please try again!");
                builder.setCancelable(true);
                builder.show();
            }
        }
    }

}
