package com.example.anisimov.vlad.RandomCompany2task1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    CurInfo curInfo;
    TextView city;
    TextView temp;
    TextView minTemp;
    TextView maxTemp;
    TextView weather;
    ImageView weatherImage;
    String curCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (TextView) findViewById(R.id.city);
        temp = (TextView) findViewById(R.id.current);
        minTemp = (TextView) findViewById(R.id.minTemp);
        maxTemp = (TextView) findViewById(R.id.maxTemp);
        weather = (TextView) findViewById(R.id.weatherText);
        weatherImage = (ImageView) findViewById(R.id.weatherImage);
        curInfo = new CurInfo();
        Timer mTimer = new Timer();
        MyTimerTask mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask,1000,1800000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            curCity = "Днепропетровск";
        } else {
            curCity = extras.getString("CITY");
        }

        curInfo.setCity(curCity);
        city.setText(curInfo.getCity());



    }

    private static String toTranslit(String city){
        switch (city) {
            case "Днепропетровск":
                return "Dnipropetrovsk";
            case "Киев":
                return "Kiev";
            case "Львов":
                return "Lviv";

        }
        return city;
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            new RetrieveFeedTask().execute(toTranslit(curInfo.getCity()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.b_settings, menu);
        return true;
    }

    public boolean goToSettings(MenuItem item) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        return true;
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(String... city) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city[0] + "&APPID=952559e6664b02ab07a866b457c4f2aa&units=metric&lang=ru");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            try {
                Log.i("INFO", response);
                JSONObject info = (JSONObject) new JSONTokener(response).nextValue();
                curInfo.setTemp(info.getJSONObject("main").getString("temp"));
                curInfo.setMinTemp(info.getJSONObject("main").getString("temp_min"));
                curInfo.setMaxTemp(info.getJSONObject("main").getString("temp_max"));
                curInfo.setWeather(info.getJSONArray("weather").getJSONObject(0).getString("description"));
                temp.setText(format(curInfo.getTemp()));
                minTemp.setText(format(curInfo.getMinTemp()));
                maxTemp.setText(format(curInfo.getMaxTemp()));
                weather.setText(capitalizeFirstLetter(curInfo.getWeather()));
                new DownloadImageTask().execute("http://openweathermap.org/img/w/" + info.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png");

            } catch (JSONException e) {
            } catch (NullPointerException e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Hello There")
                        .setMessage("It seems you have no internet connection.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    private static String format(String temp) {
        int shortTemp = Math.round(Float.parseFloat(temp));
        if (shortTemp > 0) {
            return "+" + shortTemp + "°";
        } else {
            return shortTemp + "°";
        }
    }

    private static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            weatherImage.setImageBitmap(result);
        }
    }
}


class CurInfo {
    public CurInfo() {

    }

    public CurInfo(String city, String temp, String minTemp, String maxTemp, String weather) {
        this.city = city;
        this.temp = temp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.weather = weather;
    }

    private String city;
    private String temp;
    private String minTemp;
    private String maxTemp;
    private String weather;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }
}