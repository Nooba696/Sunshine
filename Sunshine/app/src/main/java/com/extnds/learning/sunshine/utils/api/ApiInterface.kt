package com.extnds.learning.sunshine.utils.api

import com.extnds.learning.sunshine.models.retrofit.Forcast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("data/2.5/forecast/daily")
    fun forcastQuery(@Query("q") cityName: String, @Query("units") units: String = "metric", @Query("appid") appid: String = API_KEY): Call<Forcast>

}


