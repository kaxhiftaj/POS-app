package com.techease.posapp.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;


public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidEmail(Editable email) {
        String mail = email.toString().trim();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    public static boolean isValidEmail(EditText email) {
        if (email != null) {
            return isValidEmail(email.getText());
        }
        return false;
    }

    public static boolean isFullName(String str) {
        String expression = "^[a-zA-Z\\s]+";
        return str.matches(expression);
    }

    public static boolean isFullName(EditText editText) {
        return editText != null;

    }

    public static boolean hasText(Editable editable) {
        String text = editable.toString().trim();
        return text != null && text.length() > 0;
    }

    public static boolean hasText(EditText editText) {

        if (editText != null) {
            return hasText(editText.getText());
        }
        return false;
    }


    public static boolean hasText(String text) {
        return text != null && text.length() > 0;
    }

    public static boolean isPinAndConfirmPinMatch(String pin,
                                                  String confirmPin) {
        boolean pStatus = false;
        if (confirmPin != null && pin != null) {
            if (pin.equals(confirmPin)) {
                pStatus = true;
            }
        }
        return pStatus;
    }

    public static boolean isValidPassword(Editable pin) {
        String pin_no = pin.toString().trim();
        return !TextUtils.isEmpty(pin_no) && pin_no.length() >= 4;
    }

    public static boolean isValidPassword(EditText pin) {
        String pin_no = pin.getText().toString().trim();
        return !TextUtils.isEmpty(pin_no) && pin_no.length() >= 4;
    }


}
