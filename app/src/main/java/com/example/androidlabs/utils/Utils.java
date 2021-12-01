package com.example.androidlabs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.androidlabs.R;

public class Utils {

    /**
     * open browser with url
     */
    public static void openBrowser(View view, String url) {
        Context context = view.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_browser)));
    }

    /**
     * help menu dialog
     */
    public static void showDialogHelp(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Help Menu");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("got it", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
