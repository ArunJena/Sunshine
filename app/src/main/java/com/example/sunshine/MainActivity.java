package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]> {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter((ForecastAdapter.ForecastAdapterOnClickHandler) this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID,null,MainActivity.this);
    }

    public void onClick(String weatherForDay) {
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showWeatherDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String[]>(this){

            String[] mWeatherData = null;
            @Override
            protected void onStartLoading() {
                if(mWeatherData !=null){
                    deliverResult(mWeatherData);
                }
                else
                {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {

                String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                URL weatherRequestUrl=NetworkUtils.buildUrl(location);

                try{
                    String jsonWeatherResponse = NetworkUtils.
                            getResponseFromHttpUrl(weatherRequestUrl);
                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils.
                            getSimpleWeatherStringsFromJson(MainActivity.this,jsonWeatherResponse);

                    return  simpleJsonWeatherData;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data){
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mForecastAdapter.setWeatherData(null);
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID,null,this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}