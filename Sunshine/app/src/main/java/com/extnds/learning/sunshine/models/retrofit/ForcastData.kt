package com.extnds.learning.sunshine.models.retrofit

data class ForcastData(val id: Long?) {
    val dt: Long? = null
    val pressure: Float? = null
    val speed: Float? = null
    val humidity: Long? = null
    val temp: ForcastDataTemp? = null
    val weather: List<ForcastDataWeather>? = null
}