package com.example.zooseeker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {

/**
     Show alert that can finish the activity calling this alert.
*/
    public static void alertDialogShow(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                alertDialog.dismiss();
                ((Activity) context).finish();
            }
        });
        alertDialog.show();
    }

    public static void alertDialogShow2(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }




}
