package com.example.androidlabs.data.network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.example.androidlabs.data.network.response.GuardianResponse;

public class GuardianAsyncTask extends AsyncTask<String, Void, GuardianResponse> {

    public interface Callback {
        /**
         * Runs on the UI thread before {@link GuardianAsyncTask#doInBackground(String...)}.
         */
        void onPreExecute();

        /**
         * Runs on the UI thread
         *
         * @param t     if an error occurs when calling {@link GuardianApi#search(String)} in {@link GuardianAsyncTask#doInBackground(String...)} )}
         * @param query is param form {@link GuardianAsyncTask#doInBackground(String...)} String...[0].
         */
        void onError(Throwable t, String query);

        /**
         * Runs on the UI thread after {@link GuardianAsyncTask#doInBackground(String...)}
         * The specified result is the value returned by {@link GuardianAsyncTask#doInBackground}.
         *
         * @param guardianResponse The result of the operation computed by {@link GuardianAsyncTask#doInBackground}.
         */
        void onPostExecute(@Nullable GuardianResponse guardianResponse);
    }

    private final Callback callback;

    @SuppressWarnings("deprecation")
    public GuardianAsyncTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onPreExecute();
    }

    @Override
    protected GuardianResponse doInBackground(String... strings) {
        String query = strings.length > 0 ? strings[0] : null;
        try {
            return GuardianApi.search(query);
        } catch (Throwable t) {
            new Handler(Looper.getMainLooper()).post(() -> callback.onError(t, query));
            return null;
        }
    }

    @Override
    protected void onPostExecute(GuardianResponse guardianResponse) {
        super.onPostExecute(guardianResponse);
        callback.onPostExecute(guardianResponse);
    }
}
