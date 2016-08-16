package com.extnds.learning.sunshine.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.extnds.learning.sunshine.ui.MainActivity
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

const val TAG = "AppUtils"
const val LOG_TAG = MainActivity.LOG_TAG_BASE+TAG
fun Context.getWeatherContdIcon(weatherIconCode: String): ByteArray {

    Log.d(TAG,"Fetching WeatherContdIcon : http://openweathermap.org/img/w/$weatherIconCode.png")
    val bos = ByteArrayOutputStream()
    Picasso.with(this).load("http://openweathermap.org/img/w/$weatherIconCode.png").get().compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}