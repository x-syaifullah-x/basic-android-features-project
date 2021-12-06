package com.example.androidlabs.data.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.example.androidlabs.article.ArticleModel;
import com.example.androidlabs.data.local.db.GuardianDatabase;
import com.example.androidlabs.data.network.response.GuardianResponse;
import com.example.androidlabs.data.network.response.GuardianResult;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class GuardianAsyncTask extends AsyncTask<String, ArticleModel, Void> {

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
         * The result of the operation computed by {@link GuardianAsyncTask#doInBackground}.
         */
        void onPostExecute();

        /**
         * Runs on the UI thread called {@link GuardianAsyncTask#onProgressUpdate(ArticleModel...)}
         * The result of the operation computed by {@link GuardianAsyncTask#doInBackground}.
         */
        void onProgressUpdate(List<ArticleModel> models);
    }

    private final Callback callback;

    private final WeakReference<Context> contextWeakReference;

    private final String ALL = "all";

    private final List<String> queryList = Arrays.asList("news", "sport", ALL);

    @SuppressWarnings("deprecation")
    public GuardianAsyncTask(Context context, Callback callback) {
        this.callback = callback;
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String query = strings.length > 0 ? strings[0] : null;
        try {
            GuardianDatabase db = GuardianDatabase.getInstance(contextWeakReference.get());
            String qs = query != null ? query.split("=")[1] : ALL;
            // publish data from cache
            publishProgress(db.get(qs).toArray(new ArticleModel[0]));
            GuardianResponse guardianResponse = GuardianApi.search(query);
            // clear old data
            db.deleteWithQuery(qs);
            for (GuardianResult guardianResult : guardianResponse.getResults()) {
                // update new data from network
                db.save(
                        new ArticleModel(
                                guardianResult.getId(),
                                guardianResult.getWebTitle(),
                                guardianResult.getWebUrl(),
                                guardianResult.getSectionName(),
                                qs,
                                db.isFavorite(guardianResult.getId())
                        )
                );
            }
            // publish data from cache with new data
            publishProgress(db.get(qs).toArray(new ArticleModel[0]));

            // clear cache which is not needed
            if (!queryList.contains(qs)) {
                db.deleteWithQuery(qs);
            }

        } catch (Throwable t) {
            new Handler(Looper.getMainLooper()).post(() -> callback.onError(t, query));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(ArticleModel... values) {
        super.onProgressUpdate(values);
        callback.onProgressUpdate(Arrays.asList(values));
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        callback.onPostExecute();
    }
}