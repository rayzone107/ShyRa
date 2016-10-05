package com.shyra.chat.helper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that contains generic helper methods across the app.
 * Created by Rachit Goyal for ShyRa on 10/1/16.
 */

public class Helper {

    /**
     * method is used for checking valid email id format.
     *
     * @param email The string contaning the email ID.
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Convert a dp value to it's corresponding pixels.
     *
     * @param context The context for the application
     * @param dpValue The dp value that needs to be converted into pixels
     * @return The converted value in pixels.
     */
    public static float dpToPixels(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }
}
