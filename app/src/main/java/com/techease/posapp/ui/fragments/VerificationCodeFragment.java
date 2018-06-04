package com.techease.posapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techease.posapp.R;
import com.techease.posapp.ui.activities.MainActivity;
import com.techease.posapp.utils.Configuration;


public class VerificationCodeFragment extends Fragment {
   EditText et_verificationCode;
   Button btn_verifyCode;
   SharedPreferences sharedPreferences;
   SharedPreferences.Editor editor;
   String res;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification_code, container, false);
        et_verificationCode = (EditText) view.findViewById(R.id.verification_code);
        btn_verifyCode = (Button) view.findViewById(R.id.btn_verify_code);
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        res = sharedPreferences.getString("response","null");

        btn_verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strVerificationCode = et_verificationCode.getText().toString().trim();
                if (res.contains("false")) {
                    Fragment fragment = new SetNamesFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else if(strVerificationCode.contains("00000"))
                {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    Toast.makeText(getActivity(), "Successfully Login", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Enter Correct Verification Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

}
