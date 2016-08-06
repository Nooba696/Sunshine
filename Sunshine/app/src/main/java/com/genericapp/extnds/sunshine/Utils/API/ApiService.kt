package com.genericapp.extnds.sunshine.Utils.API

import com.genericapp.extnds.sunshine.Models.Retrofit.Forcast
import com.genericapp.extnds.sunshine.Utils.API.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Nooba(PratickRoy) on 07-07-2016.
 */

interface ApiService {
    @GET("data/2.5/forecast/daily")
    fun forcastQuery(@Query("q") cityName : String, @Query("units") units : String = "metric", @Query("appid") appid : String = API_KEY): Call<Forcast>

}


