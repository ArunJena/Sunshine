package com.example.sunshine.data;

import android.content.Context;

public class SunshinePreferences {
    public static final String PREF_CITY_NAME="city_name";
    public static final String PREF_COORD_LAT="coord_lat";
    public static final String PREF_COORD_LONG="coord_long";

    private static final String DEFAULT_WEATHER_LOCATION="94043,USA";
    private static final double[] DEFAULT_WEATHER_COORDINATED={37.4284, 122.0724};
    private static final String DEFAULT_MAP_LOCATION="1600 Amphitheatre Parkway, Mountain View, CA 94043";


    public static String getPreferredWeatherLocation(Context context){
        return getDefaultWeatherLocation();
    }
    private static String getDefaultWeatherLocation(){
        return DEFAULT_WEATHER_LOCATION;
    }
    public static boolean isMetric(Context context) {
        return true;
    }

}
