package lpc.com.weatherdemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;


import java.io.IOException;

import androidx.annotation.Nullable;
import lpc.com.weatherdemo.gson.Weather;
import lpc.com.weatherdemo.util.HttpUtil;
import lpc.com.weatherdemo.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/13.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        updateBingPic();

        AlarmManager manger = (AlarmManager) getSystemService(ALARM_SERVICE);
        int AnHour = 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + AnHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manger.cancel(pi);
        manger.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {

        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkhttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(AutoUpdateService.this).edit();

                editor.putString("bing_pic", bingPic);
                editor.apply();

            }

        });
    }

    private void updateWeather() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);

        String WeatherString = preferences.getString("weather", null);

        if (WeatherString != null) {

            Weather weather = Utility.handleWeatherResponse(WeatherString);
            String WeatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + WeatherId
                    + "&key=bc0418b57b2d4918819d3974ac1285d9";

            HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {


                     String responseText = response.body().string();
                     Weather weather = Utility.handleWeatherResponse(responseText);

                    if (weather !=null && "ok".equals(weather.status)){


                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(AutoUpdateService.this)
                            .edit();

                    editor.putString("weather", responseText);
                    editor.apply();

                    }

                }
            });

        }

    }


}
