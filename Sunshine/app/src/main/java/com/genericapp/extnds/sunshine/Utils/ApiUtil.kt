package com.genericapp.extnds.sunshine.Utils

import com.genericapp.extnds.mozillarecpro.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Nooba(PratickRoy) on 29-07-2016.
 */
const val API_URL = "http://api.openweathermap.org"
const val API_KEY = "dfc37d130602a32c97ec3e4cff956b0d"
val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
val apiService = retrofit.create(ApiService::class.java)