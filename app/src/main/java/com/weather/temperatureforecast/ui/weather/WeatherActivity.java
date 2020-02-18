package com.weather.temperatureforecast.ui.weather;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.squareup.picasso.Picasso;
import com.weather.temperatureforecast.APIClient;
import com.weather.temperatureforecast.APIService;
import com.weather.temperatureforecast.R;
import com.weather.temperatureforecast.databinding.ActivityWeatherBinding;
import com.weather.temperatureforecast.model.WeatherModel;
import com.weather.temperatureforecast.ui.forecast.ForecastActivity;
import com.weather.temperatureforecast.ui.map.MapActivity;
import com.weather.temperatureforecast.util.Utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener {

    private final String TILE_TYPE = "clouds";

    private ActivityWeatherBinding mBinding;
    public static LatLngBounds camera_bounds;
    private TileOverlay tileOver;
    private MapFragment fMap;
    private String mStrLat, mStrLon;
    private GoogleMap mMap;
    private GoogleMap mThumbMap;
    private LatLng mCameraLatLon;
    private LatLng mUserLatLon;
    private boolean cameraPosHasBeenUpdated;
    private boolean cloud_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_weather);

        mStrLat = getIntent().getStringExtra("latitude");
        mStrLon = getIntent().getStringExtra("longitude");

        mCameraLatLon = new LatLng(Double.valueOf(mStrLat), Double.valueOf(mStrLon));
        mUserLatLon = mCameraLatLon;

        ActionBar actionBar = getSupportActionBar();// get the parent in order to insert custom logo and name
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_layout);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        fMap = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fMap);
        fMap.getMapAsync(this);

        ImageButton ibCloud = findViewById(R.id.ibCloud);
        cloud_selected = false;
        ibCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cloud_selected) {
                    cloud_selected = true;
                    setCloudsCamera();
                } else {
                    cloud_selected = false;
                    setDefaultCameraPosition();
                }
            }
        });

        // start the API query
        refreshWeatherData();

        cloud_selected = false;
        mBinding.ibForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForecast();
            }
        });

//        mBinding.ivExpand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showMapExpanded();
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        initThumbMap();

        mMap = map;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style));// custom json map style created
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMapClickListener(this);

        cameraPosHasBeenUpdated = false;
        setDefaultCameraPosition();
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onCameraMove() {
        cameraPosHasBeenUpdated = true;
    }

    @Override
    public void onCameraIdle() {
        LatLngBounds init_bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        camera_bounds = init_bounds;
        mCameraLatLon = init_bounds.getCenter();

        if (cameraPosHasBeenUpdated) {
            cameraPosHasBeenUpdated = false;
            // pause everything on map
            mMap.getUiSettings().setAllGesturesEnabled(false);

            mStrLat = Double.toString(init_bounds.getCenter().latitude);
            mStrLon = Double.toString(init_bounds.getCenter().longitude);
            // get refreshed updated weather for these coordinates
            refreshWeatherData();
        }
    }

    private void initThumbMap() {
        MapFragment fThumbMap = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fThumbMap);
        fThumbMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap tMap) {
                mThumbMap = tMap;
                mThumbMap.getUiSettings().setAllGesturesEnabled(false);
                mThumbMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getApplicationContext(), R.raw.style_thumb));

            }
        });
    }

    private void setDefaultCameraPosition() {
        CameraPosition cameraPosition;

        cameraPosition = new CameraPosition.Builder()
                .target(mUserLatLon)    // Sets the center of the map to the users position
                .zoom(12)               // Sets the zoom
                .bearing(0)             // -90 = west, 90 = east
                .tilt(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                cameraPosHasBeenUpdated = false;
                if (tileOver != null) {
                    tileOver.remove();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void setCloudsCamera() {
        CameraPosition cameraPosition;

        cameraPosition = new CameraPosition.Builder()
                .target(camera_bounds.getCenter())      // Sets the center of the map
                .zoom(5)                                // Sets the zoom
                .bearing(0)                             // -90 = west, 90 = east
                .tilt(0)
                .build();

        tileOver = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(createTileProvider()));
        tileOver.setVisible(false);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        tileOver.setVisible(true);
    }

    private void updateThumbnailMap(LatLngBounds init_bounds) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(init_bounds.getCenter())      // Sets the center of the map
                .zoom(0)                   // Sets the zoom
                .bearing(0)                // -90 = west, 90 = east
                .tilt(0)
                .build();

        mThumbMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private TileProvider createTileProvider() {
        return new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String fUrl = String.format(getString(R.string.tile_url), TILE_TYPE, zoom, x, y);
                URL url = null;
                try {
                    url = new URL(fUrl);
                } catch (MalformedURLException mfe) {
                    mfe.printStackTrace();
                }

                return url;
            }
        };
    }

    private void showMapExpanded() {
        // Ordinary Intent for launching a new activity
        Intent intent = new Intent(this, MapActivity.class);

        // Get the transition name from the string
        String transitionName = getString(R.string.transition_string);

        // Define the view that the animation will start from
        View viewStart = fMap.getView();

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        Objects.requireNonNull(viewStart),   // Starting view
                        transitionName    // The String
                );

        // Start the Intent
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void refreshWeatherData() {
        APIClient.createService(APIService.class).getWeatherData(mStrLat, mStrLon, getString(R.string.api), getString(R.string.metric)).enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherModel> call, @NonNull Response<WeatherModel> response) {
                WeatherModel model = response.body();

                assert model != null;
                createLayout(model);
            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                Toast.makeText(WeatherActivity.this, "Error: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createLayout(WeatherModel model) {
        if (mMap == null) {
            return;
        }

        LatLngBounds init_bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        updateThumbnailMap(init_bounds);

        // allow camera to update when map is moved again
        cameraPosHasBeenUpdated = true;
        mMap.getUiSettings().setAllGesturesEnabled(true);

        if (model == null) {
            return;
        } else if (model.getWind() == null) {
            return;
        } else if (model.getWind().getDeg() == null) {
            // the Logcat on Android 3 is unreliable, nearly sure this is catching error but no Log retrieved
            return;
        }

        // cast the wind direction to a clean number
        String deg_val = Integer.toString(model.getWind().getDeg());

        // clean String concatenation's of values to be set
        String strTemperature = getString(R.string.temperature) + model.getMain().getTemp() + getString(R.string.celcius);
        String strTemperatureMinMax = getString(R.string.min) + model.getMain().getTempMin() + getString(R.string.celcius) + getString(R.string.max) + model.getMain().getTempMax() + getString(R.string.celcius);
        String strWindSpeed = getString(R.string.wind_speed) + model.getWind().getSpeed() + getString(R.string.kph);
        String strWindDirection = getString(R.string.wind_direction) + deg_val + getString(R.string.degree);
        String strHumidity = getString(R.string.humidity) + model.getMain().getHumidity() + getString(R.string.percent);

        // set the compass rotation
        float float_val = Float.parseFloat(deg_val);
        mBinding.ivWindDirection.setRotation(float_val);

        // get the references in onCreate when first loading
        TextView tvTitle = findViewById(R.id.tvTitle);
        // set the values in the UI
        tvTitle.setText(model.getName());

        mBinding.tvTemperature.setText(strTemperature);
        mBinding.tvTemperatureMinMax.setText(strTemperatureMinMax);
        mBinding.tvWindSpeed.setText(strWindSpeed);
        mBinding.tvWindDirection.setText(strWindDirection);
        mBinding.tvHumidity.setText(strHumidity);

        // head wrecking stuff ...
        String sun_up_down = getString(R.string.sunrise) + Utilities.getHumanReadable(model.getSys().getSunrise())
                + getString(R.string.sunset) + Utilities.getHumanReadable(model.getSys().getSunset());
        mBinding.tvSunUpDown.setText(sun_up_down);

        // populate the info details box
        List<WeatherModel.Weather> weathers = model.getWeather();
        // the icon is in the first element {} of the list .get(0)
        String strIcon = getString(R.string.icon_url) + weathers.get(0).getIcon() + getString(R.string.png);

        // excellent library to do the heavy lifting of Image handling
        Picasso.with(getApplicationContext()).load(strIcon).into(mBinding.ivWeather);

        mBinding.tvDescription.setText(weathers.get(0).getDescription());
    }

    private void showForecast() {
        Intent intent = new Intent(this, ForecastActivity.class);// select a new station
        intent.putExtra("latitude", mStrLat);
        intent.putExtra("longitude", mStrLon);

        startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), R.transition.slide_in, R.transition.slide_out).toBundle());
    }

}
