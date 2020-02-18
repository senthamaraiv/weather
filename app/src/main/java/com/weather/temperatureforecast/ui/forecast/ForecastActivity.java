package com.weather.temperatureforecast.ui.forecast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.weather.temperatureforecast.APIClient;
import com.weather.temperatureforecast.APIService;
import com.weather.temperatureforecast.R;
import com.weather.temperatureforecast.databinding.ActivityForecastBinding;
import com.weather.temperatureforecast.model.DayDataModel;
import com.weather.temperatureforecast.model.DayDetailDataModel;
import com.weather.temperatureforecast.model.ExpandableDetails;
import com.weather.temperatureforecast.model.ExpandableHeader;
import com.weather.temperatureforecast.model.ForecastModel;
import com.weather.temperatureforecast.model.ListDataModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastActivity extends AppCompatActivity {

    ActivityForecastBinding mBinding;
    List<ExpandableHeader> mExpandableHeaders;
    List<ExpandableDetails> mExpandableDetails;
    LinkedHashMap<ExpandableHeader, List<ExpandableDetails>> mExpandableHeadersDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forecast);

        ActionBar actionBar = getSupportActionBar();// get the parent in order to insert custom logo and name
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_layout);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        // start the API query
        refreshForecastData(getIntent().getStringExtra("latitude"), getIntent().getStringExtra("longitude"));
    }

    private void refreshForecastData(String strLat, String strLon) {
        APIClient.createService(APIService.class).getForecastData(strLat, strLon, getString(R.string.api), getString(R.string.metric)).enqueue(new Callback<ForecastModel>() {
//        APIClient.createService(APIService.class).getForecastData(strLat, strLon, getString(R.string.api), getString(R.string.imperial), getString(R.string.json), getString(R.string.cnt)).enqueue(new Callback<ForecastModel>() {
            @Override
            public void onResponse(@NonNull Call<ForecastModel> call, @NonNull Response<ForecastModel> response) {
                ForecastModel model = response.body();

                assert model != null;
                createLayout(model);
            }

            @Override
            public void onFailure(@NonNull Call<ForecastModel> call, @NonNull Throwable t) {
                Log.e("ERROR", "onFailure: " + t);
                Toast.makeText(ForecastActivity.this, "Error: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createLayout(ForecastModel model) {
        // get the references in onCreate when first loading
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(model.getCity().getName());

        ImageButton ibCloud = findViewById(R.id.ibCloud);
        ibCloud.setVisibility(View.GONE);

        ListDataModel listDataModel = getListData(model);
        bindExpandableHeaderDetails(listDataModel);

        mExpandableHeaders = new ArrayList<>(mExpandableHeadersDetails.keySet());
        ForecastExpandableListAdapter forecastExpandableListAdapter = new ForecastExpandableListAdapter(ForecastActivity.this, mExpandableHeaders, mExpandableHeadersDetails);
        mBinding.elvForecast.setAdapter(forecastExpandableListAdapter);
        mBinding.elvForecast.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }

    // Returns ListDataModel class object to use in WeatherForeCastList activity class.
    private ListDataModel getListData(ForecastModel model) {
        ListDataModel listDataModel = new ListDataModel();
        try {
            List<DayDetailDataModel> dayDetailDataModels = null;
            List<DayDataModel> dayDataModels = new ArrayList<>();
            DayDataModel dayDataModel = null;
            DayDetailDataModel dayDetailDataModel;
            listDataModel.setCityName(model.getCity().getName());
            List<ForecastModel.List> lists = model.getList();
            String previousDate = "";
            for (ForecastModel.List list : lists) {
                String resultDate = list.getDtTxt().substring(0, 10);
                String resultTime = list.getDtTxt().substring(11, 18);
                if (previousDate.equals("") || (!previousDate.equals(resultDate))) {
                    dayDataModel = new DayDataModel();
                    dayDetailDataModels = new ArrayList<>();
                }
                dayDetailDataModel = new DayDetailDataModel();
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dayDataModel.setCurrentDate(inputFormat.parse(list.getDtTxt()));

                dayDetailDataModel.setMaxTemperature(Double.toString(list.getMain().getTempMax()));
                dayDetailDataModel.setMinTemperature(Double.toString(list.getMain().getTempMin()));
                dayDetailDataModel.setTemperature(Double.toString(list.getMain().getTemp()));
                dayDetailDataModel.setTime(resultTime);
                dayDetailDataModel.setWeather(list.getWeather().get(0).getMain());
                dayDetailDataModel.setWindSpeed(Double.toString(list.getWind().getSpeed()));
                dayDetailDataModels.add(dayDetailDataModel);
                if (previousDate.equals("") || (!previousDate.equals(resultDate))) {
                    dayDataModel.setDayDetailDataModels(dayDetailDataModels);
                    dayDataModels.add(dayDataModel);
                    listDataModel.setDayDataModels(dayDataModels);
                }
                previousDate = resultDate;
            }
            /* Fetch data section end*/
        } catch (Exception ex) {
            System.out.println("returnSerializableData Exception :" + ex.getMessage());
        }
        return listDataModel;
    }

    // Method to bind ListDataModel data to expandable list adaptor.
    private void bindExpandableHeaderDetails(ListDataModel listDataModel) {
        ExpandableHeader expandableHeader;
        ExpandableDetails expandableDetails;
        List<Double> minTempList = new ArrayList<>();
        List<Double> maxTempList = new ArrayList<>();
        List<DayDataModel> dayDataModels = listDataModel.getDayDataModels();
        mExpandableHeadersDetails = new LinkedHashMap<>();
        for (DayDataModel dayDataModel : dayDataModels) {
            expandableHeader = new ExpandableHeader();
            mExpandableDetails = new ArrayList<>();
            expandableHeader.setDay(dayDataModel.getCurrentDate().toString().substring(0, 10));
            List<DayDetailDataModel> dayDetailDataModels = dayDataModel.getDayDetailDataModels();
            for (DayDetailDataModel dayDetailDataModel : dayDetailDataModels) {
                expandableDetails = new ExpandableDetails();
                double minTemp = Double.parseDouble(dayDetailDataModel.getMinTemperature());
                double maxTemp = Double.parseDouble(dayDetailDataModel.getMaxTemperature());
//                minTemp = minTemp - 273.15F;
//                maxTemp = maxTemp - 273.15F;
                minTempList.add(minTemp);
                maxTempList.add(maxTemp);
                expandableHeader.setWeather(dayDetailDataModel.getWeather());
                expandableDetails.setWeather(dayDetailDataModel.getWeather());
                expandableDetails.setDateAndTime(dayDetailDataModel.getTime());
                double temperature = Double.parseDouble(dayDetailDataModel.getTemperature());
//                temperature = temperature - 273.15F;
                expandableDetails.setMinmaxTemp((int) temperature + "" + (char) 0x00B0);
                expandableDetails.setTemperature((int) temperature + "" + (char) 0x00B0);
                expandableDetails.setWind(dayDetailDataModel.getWindSpeed() + " Miles/hour");
                expandableDetails.setCityName(listDataModel.getCityName());
                mExpandableDetails.add(expandableDetails);
            }
            expandableHeader.setMinMaxTemp(minMaxTemp(minTempList, maxTempList));
            minTempList.clear();
            maxTempList.clear();
            mExpandableHeadersDetails.put(expandableHeader, mExpandableDetails);
        }
    }

    /**
     * Method to filter min and max temperature of the whole day.
     */
    private String minMaxTemp(List<Double> minTempList, List<Double> maxTempList) {
        String minMaxResulted;
        double maxValue = Collections.max(maxTempList);
        double minValue = Collections.min(minTempList);
        minMaxResulted = (int) minValue + "" + (char) 0x00B0 + " - " + (int) maxValue + "" + (char) 0x00B0;
        return minMaxResulted;
    }

}
