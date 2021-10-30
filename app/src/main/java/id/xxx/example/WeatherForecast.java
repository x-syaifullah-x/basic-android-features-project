package id.xxx.example;

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

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
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

        ProgressBar pb = findViewById(R.id.loading);
        pb.setVisibility(ProgressBar.VISIBLE);

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        String current, min, max;
        String imageName;
        String imageURL;
        Double value;
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
                            imageName = xpp.getAttributeValue(null, "icon") + ".png";
                            imageURL = "http://openweathermap.org/img/w/" + imageName;

                            if (fileIsExist(imageName)) {
                                Log.i(imageName, "load image from local");
                                FileInputStream fis = openFileInput(imageName);
                                image = BitmapFactory.decodeStream(fis);
                            } else {
                                URL url1 = new URL("http://openweathermap.org/img/w/" + imageName);
                                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                                connection.connect();
                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    Log.i(imageName, "load image from network");
                                    image = BitmapFactory.decodeStream(connection.getInputStream());
                                } else {
                                    Log.e("Error", "load image invalid");
                                }

                                FileOutputStream outputStream = openFileOutput(imageName, Context.MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }
                        }
                    }
                    eventType = xpp.next();
                }

                url = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=28.644800&lon=-77.216721");
                urlConnection = (HttpURLConnection) url.openConnection();
                input = urlConnection.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length; (length = input.read(buffer)) != -1; ) {
                    result.write(buffer, 0, length);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                value = jsonObject.getDouble("value");

                publishProgress(100);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return "Done";
        }

        public boolean fileIsExist(String fName) {
            File file = getBaseContext().getFileStreamPath(fName);
            return file.exists();
        }


        @Override
        public void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(values[0]);
        }


        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);

            TextView currentView = findViewById(R.id.current);
            currentView.setText(currentView.getText() + "\n" + current);

            TextView minView = findViewById(R.id.min);
            minView.setText(minView.getText() + "\n" + min);

            TextView maxView = findViewById(R.id.max);
            maxView.setText(maxView.getText() + "\n" + max);

            TextView uviView = findViewById(R.id.UV);
            uviView.setText("UV: " + value);

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(image);

            pb.setVisibility(View.INVISIBLE);
        }
    }
}


