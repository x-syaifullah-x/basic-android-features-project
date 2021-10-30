package com.example.androidlabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ProgressBar pb = (ProgressBar) findViewById(R.id.loading);
        pb.setVisibility(ProgressBar.VISIBLE);

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class ForecastQuery extends AsyncTask<String, Integer, String>
    {
        String current, min, max;
        String imageName;
        String imageURL;
        Bitmap image;

        ProgressBar pb = findViewById(R.id.loading);

        @Override
        protected String doInBackground(String... s) {

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=delhi,in&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric%22");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream input = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(input, "UTF-8");

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("temperature")) {
                            current = xpp.getAttributeValue(null, "value");
                            publishProgress(25);
                            min = xpp.getAttributeValue(null, "min");
                            publishProgress(50);
                            max = xpp.getAttributeValue(null, "max");
                            publishProgress(75);
                        } else if (xpp.getName().equals("weather")) {
                            imageName = xpp.getAttributeValue(null, "icon");
                            imageURL = "http://openweathermap.org/img/w/\" + iconName + \".png";

                            String iconName = "";
                            if (fileExistance(iconName + ".png")) {
                                FileInputStream fis = null;
                                image = BitmapFactory.decodeStream(fis);
                            } else {
                                URL url1 = new URL("http://openweathermap.org/img/w/\" + iconName + \".png");
                                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                                connection.connect();
                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    image = BitmapFactory.decodeStream(connection.getInputStream());
                                }

                                FileOutputStream outputStream = openFileOutput(imageName + ".png", Context.MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }
                            publishProgress(100);
                        }
                    }
                    eventType = xpp.next();
                }
            }catch (Exception e)
            {
                Log.e("Error", e.getMessage());
            }
            return "Done";
        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();   }


        @Override
        public void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(values[0]);
        }



        @Override
        public void onPostExecute(String s)
        {
            super.onPostExecute(s);

            TextView currentView = findViewById(R.id.current);
            currentView.setText(getString(R.string.currentWeather));

            TextView minView = findViewById(R.id.min);
            minView.setText(getString(R.string.minTemperature));

            TextView maxView = findViewById(R.id.max);
            maxView.setText(getString(R.string.maxTemperature));

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(image);

            pb.setVisibility(View.INVISIBLE);

        }

    }


}


