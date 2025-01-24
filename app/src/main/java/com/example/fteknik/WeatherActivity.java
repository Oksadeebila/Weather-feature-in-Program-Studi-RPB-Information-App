package com.example.fteknik;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {
//proses integrasi API pada aplikasi
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final String OPEN_WEATHER_MAP_API = "d2604b1b145fe3cd7a65dbd8aa52f0d2";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

//    Membuat variabel untuk mengintegrasikannya ke halaman aplikasi
    private TextView tvIP, dateTime, city_name, tv_owl_text, tvTemperature;
    private LottieAnimationView triangle_loading;
    private RelativeLayout main_layout;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather); // Layout yang baru

        initializeViews();

        if (!isNetworkAvailable()) {
            showNoInternetDialog();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupDate();
        checkLocationPermission();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    tvIP.setText(latitude + " , " + longitude);
                    fetchWeather(latitude, longitude);
                }
            }
        };
    }

    private void initializeViews() {
        tvIP = findViewById(R.id.tvIP);
        triangle_loading = findViewById(R.id.triangle_loading);
        dateTime = findViewById(R.id.dateTime);
        city_name = findViewById(R.id.city_name);
        tv_owl_text = findViewById(R.id.tv_owl_text);
        tvTemperature = findViewById(R.id.tvTemperature);
        main_layout = findViewById(R.id.main_layout);
        triangle_loading.setSpeed(2f);
    }

    private void setupDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTime.setText(formattedDate);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            checkLocationSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationSettings();
        } else {
            Log.e("Location", "Permission denied");
        }
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                    startLocationUpdates();
                } catch (ApiException exception) {
                    handleLocationSettingsException(exception);
                }
            }
        });
    }

    private void handleLocationSettingsException(ApiException exception) {
        switch (exception.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                    resolvable.startResolutionForResult(WeatherActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException | ClassCastException e) {
                    Log.e("Location", "Error: " + e.getMessage());
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e("Location", "Location settings are inadequate, and cannot be fixed here.");
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void fetchWeather(double latitude, double longitude) {
        String url = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + OPEN_WEATHER_MAP_API + "&units=metric";
        RequestQueue queue = Volley.newRequestQueue(WeatherActivity.this);

        queue.add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("WeatherAPI", "Response from OpenWeatherMap: " + response);
                handleWeatherResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("WeatherError", "Error fetching weather data: " + error.getMessage());
            }
        }));
    }

    private void handleWeatherResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject main = jsonResponse.getJSONObject("main");
            double temp = main.getDouble("temp");
            String city = jsonResponse.getString("name");

            int temperature = (int) Math.round(temp);
            tvTemperature.setText(temperature + "°C");
            city_name.setText(city);

            setRecommendationText(temperature);

            triangle_loading.setVisibility(View.GONE);
            main_layout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            Log.e("WeatherError", "JSON parsing error: " + e.getMessage());
        }
    }

    private void setRecommendationText(double temperature) {
        String adviceText = "";
        if (temperature >= 0 && temperature <= 5) {
            adviceText = "You should bundle up! It's cold outside. ❄️";
        } else if (temperature > 5 && temperature <= 10) {
            adviceText = "You should wear a light jacket to stay comfortable. 🧥";
        } else if (temperature > 10 && temperature <= 15) {
            adviceText = "You should enjoy the cool weather with a light sweater. 🌤️";
        } else if (temperature > 15 && temperature <= 20) {
            adviceText = "You should take a casual stroll outside, it's perfect weather. 🚶‍♂️";
        } else if (temperature > 20 && temperature <= 25) {
            adviceText = "You should enjoy a pleasant day outdoors. 🌞";
        } else if (temperature > 25 && temperature <= 30) {
            adviceText = "You should stay hydrated in warm weather. 💧";
        } else if (temperature > 30 && temperature <= 35) {
            adviceText = "You should find shade and stay cool in hot weather. 🌳";
        } else if (temperature > 35 && temperature <= 40) {
            adviceText = "You should take precautions to avoid heat exhaustion in very hot weather. ☀️";
        } else if (temperature > 40 && temperature <= 45) {
            adviceText = "You should stay indoors if possible during extreme heat. 🏠";
        } else if (temperature > 45 && temperature <= 50) {
            adviceText = "You should limit outdoor activities in dangerously hot weather. ⚠️";
        }
        tv_owl_text.setText(adviceText);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates();
            } else {
                Log.e("Location", "User did not agree to make required location settings changes");
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or WiFi to access this. Press ok to Exit");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }


}
