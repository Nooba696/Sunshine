package com.extnds.learning.sunshine.utils.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_URL = "http://api.openweathermap.org"
const val API_KEY = "dfc37d130602a32c97ec3e4cff956b0d"
val retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
val apiInterface: ApiInterface? = retrofit?.create(ApiInterface::class.java)