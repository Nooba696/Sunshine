package com.genericapp.extnds.sunshine.Models.Retrofit

/**
 * Created by Nooba(PratickRoy) on 29-07-2016.
 */
data class ForcastData(val id: Long?) {
    val dt: Long? = null
    val pressure: Float? = null
    val speed: Float? = null
    val humidity: Long? = null
    val temp: ForcastDataTemp? = null
    val weather: List<ForcastDataWeather>? = null
}