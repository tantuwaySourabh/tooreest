package com.ebabu.tooreest.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;

/**
 * Created by hp on 22/09/2016.
 */
public class DialogUtils {
    public static void openDialogToLogout(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to logout?");
        builder.setTitle("Logout");

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.logout(context);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void openAlertToShowMessage(String message, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void openAlertToShowMessage(String title, String message, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void openAlertOnInvalidToken(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Invalid token");
        builder.setCancelable(false);
        builder.setMessage("You might logged-in with some other device and your token has been expired. Please login again to continue with this device.");

        AppPreference.getInstance(context).clear();
        MobiComUserPreference.getInstance(context).clearAll();
        builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.logout(context);
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
