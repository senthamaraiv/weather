package com.weather.temperatureforecast;

import com.weather.temperatureforecast.model.ForecastModel;
import com.weather.temperatureforecast.model.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("weather/")
    Call<WeatherModel> getWeatherData(@Query("lat") String lat,
                                      @Query("lon") String lon,
                                      @Query("appid") String appId,
                                      @Query("units") String units);

    @GET("forecast/")
    Call<ForecastModel> getForecastData(@Query("lat") String lat,
                                        @Query("lon") String lon,
                                        @Query("appid") String appId,
                                        @Query("units") String units);

//    @GET("forecast?daily&")
//    Call<ForecastModel> getForecastData(@Query("lat") String lat,
//                                        @Query("lon") String lon,
//                                        @Query("appid") String appId,
//                                        @Query("units") String units,
//                                        @Query("mode") String mode,
//                                        @Query("cnt") String cnt);

}
