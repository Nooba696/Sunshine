package com.genericapp.extnds.sunshine.Utils

import android.content.Context
import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

/**
 * Created by Nooba(PratickRoy) on 14-08-2016.
 */

fun Context.getWeatherContdIcon(weatherIconCode: String): ByteArray {

    val bos = ByteArrayOutputStream()
    Picasso.with(this).load("http://openweathermap.org/img/w/${weatherIconCode}.png").get().compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}