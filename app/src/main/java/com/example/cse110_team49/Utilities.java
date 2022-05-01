package com.example.cse110_team49;
import android.app.Activity;
import android.app.AlertDialog;


public class Utilities {
    public static void showAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Notice")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}

