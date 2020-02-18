package com.weather.temperatureforecast.ui.map;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.weather.temperatureforecast.R;
import com.weather.temperatureforecast.ui.weather.WeatherActivity;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mLargeMap;
    private LatLng camera_latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

//        Slide slide = new Slide(Gravity.BOTTOM);
//        slide.setInterpolator(AnimationUtils.loadInterpolator(this,android.R.interpolator.linear_out_slow_in));
//        getWindow().setEnterTransition(slide);

        MapFragment fLargeMap = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fLargeMap);
        fLargeMap.getMapAsync(this);
        camera_latLng = WeatherActivity.camera_bounds.getCenter();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mLargeMap = googleMap;
        mLargeMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style));// custom json map style created
        setDefaultCameraPosition();
    }

    private void setDefaultCameraPosition() {
        mLargeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera_latLng, 12));
    }

}
