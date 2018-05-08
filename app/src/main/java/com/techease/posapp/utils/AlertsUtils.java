package com.techease.posapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techease.posapp.R;

public class AlertsUtils {

    public static ProgressDialog progressDialog;


    /**
     * add new patient
     *
     * @param activity
     */
    public static void showErrorDialog(Activity activity, String message) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog
                , null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView tvError = dialogView.findViewById(R.id.tv_error);
        tvError.setText(message);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public static AlertDialog createProgressDialog(Activity activity) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.progress_dialog
                , null);

        dialogBuilder.setView(dialogView);
        ProgressBar pd = dialogView.findViewById(R.id.indeterminateBar);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialoge_box);
        alertDialog.getWindow().setAttributes(lp);
        pd.setVisibility(View.VISIBLE);
        return alertDialog;

    }

    public static AlertDialog showDialog(Activity activity){
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.popup_layout);
        TextView tv_oops = dialog.findViewById(R.id.tv_oops);
        tv_oops.setText("");
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText("This User ID have no job\n completed yet");
        tvMessage.setGravity(Gravity.CENTER);
        dialog.show();
        return (AlertDialog) dialog;
    }
}


