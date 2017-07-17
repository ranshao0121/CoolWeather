package lpc.com.weatherdemo.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lpc.com.weatherdemo.db.City;
import lpc.com.weatherdemo.db.County;
import lpc.com.weatherdemo.db.Province;
import lpc.com.weatherdemo.gson.Weather;

/**
 * Created by Administrator on 2017/7/7.
 */

public class Utility {

    public static boolean handleProvinceResponse(String response){

        if (!TextUtils.isEmpty(response)){
            try {

                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {

                    JSONObject provinceObject = allProvince.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){

        if (!TextUtils.isEmpty(response)){

            try {

                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {

                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){

        if (!TextUtils.isEmpty(response)){

            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {

                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static Weather handleWeatherResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
