package com.techease.posapp.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
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
import com.techease.posapp.ui.activities.MainActivity;
import com.techease.posapp.ui.models.JobsModel;
import com.techease.posapp.utils.AlertsUtils;
import com.techease.posapp.utils.Configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {
<<<<<<< HEAD
    EditText ed_FirstName, ed_LastName, ed_Email, et_sex, et_dob;
    String api_token, user_id, strFirstName, strLastName, strProfileImage, strEmail, strSex, strDob;
=======
    EditText ed_FirstName, ed_LastName, ed_Email, ed_MobileNo;
    String api_token, user_id,strFirstName,strLastName,strProfileImage;
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
    Button btn_save;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
<<<<<<< HEAD
    LinearLayout showNamesLayout, setFirstNameLayout, setLastNameLayout;
    File profileImage_file;
    CircleImageView ivProfile;
    Uri image_uri;
    String str_firstName, str_lastName;


=======
    LinearLayout showNamesLayout,setNameLayout;
    File profileImage_file;
    CircleImageView ivProfile;
    Uri image_uri;
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ed_FirstName = (EditText) view.findViewById(R.id.edit_firstName);
        ed_LastName = (EditText) view.findViewById(R.id.edit_lastName);
        ed_Email = (EditText) view.findViewById(R.id.edit_email);
        ed_MobileNo = (EditText) view.findViewById(R.id.edit_mobileNo);
        btn_save = view.findViewById(R.id.edit_done);
        ivProfile = view.findViewById(R.id.set_profilePictr);
        setNameLayout = view.findViewById(R.id.edit_Name_layout);
        showNamesLayout = view.findViewById(R.id.edit_NameLayout);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        api_token = sharedPreferences.getString("api_token", "");
        user_id = sharedPreferences.getString("user_id", "");

        setNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNamesLayout.setVisibility(View.VISIBLE);
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
<<<<<<< HEAD
                if (profileImage_file == null && str_firstName == null) {
=======

//                if (profileImage_file == null && ed_FirstName.getText().toString() == null && ed_LastName.getText().toString() == null) {
//
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
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
<<<<<<< HEAD
                } else {
                    new setProfile().execute();
                }
=======

//                }
//                else {
//                    apicall();
//                    Fragment fragment = new UserProfileFragment();
//                    getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).addToBackStack("").commit();
//                }
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
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

    private void apicall(){
        final String str_firstName = ed_FirstName.getText().toString().trim();
        final String str_lastName = ed_LastName.getText().toString().trim();
        final String str_Email = ed_Email.getText().toString().trim();
        final String str_Mobile = ed_MobileNo.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.USER_EDIT_PROFILE
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
<<<<<<< HEAD
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
=======
                Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "you got some error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("first_name", str_firstName);
                params.put("last_name", str_lastName);
                params.put("email", str_Email);
                params.put("mobile_no", str_Mobile);
                params.put("api_token", api_token);
<<<<<<< HEAD
                params.put("user_id", user_id);
=======
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
                return params;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }
<<<<<<< HEAD

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

                        editor.putString("user_firstName",str_firstName);
                        editor.putString("user_lastName",str_lastName);
                        editor.putString("user_image",strProfileImage);
                        editor.commit();

                        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
                        tv_oops.setText("Profile Updated");
                        TextView tv_message = dialog.findViewById(R.id.tv_message);
                        tv_message.setText("your profile has been updated");
                        TextView ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
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

=======
>>>>>>> 7a5e69b08a0e99a4895fccb84acb390edae71054
}
