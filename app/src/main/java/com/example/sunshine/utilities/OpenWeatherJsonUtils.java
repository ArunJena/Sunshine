package com.example.sunshine.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class OpenWeatherJsonUtils {

    public static String[] getSimpleWeatherStringsFromJson(Context context,String forecastJSONStr) throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_MESSAGE_CODE = "cod";

        String[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(forecastJSONStr);
        if(forecastJson.has(OWM_MESSAGE_CODE)){
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);
            switch (errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        parsedWeatherData = new String[weatherArray.length()];

        long localDate = System.currentTimeMillis();
        long utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate);
        long startDay = SunshineDateUtils.normalizeDate(utcDate);

        for(int i=0;i<weatherArray.length();i++){
            String date;
            String highAndLow;

            long dateTimeMillis;
            double high;
            double low;
            String description;

            JSONObject dayForecast = weatherArray.getJSONObject(i);
            dateTimeMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i;
            date = SunshineDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = SunshineWeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;

        }
        return parsedWeatherData;
    }
}
