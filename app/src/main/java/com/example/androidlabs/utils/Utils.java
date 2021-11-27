package com.example.androidlabs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.example.androidlabs.R;

public class Utils {

    public static void openBrowser(View view, String url) {
        Context context = view.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_browser)));
    }
}
