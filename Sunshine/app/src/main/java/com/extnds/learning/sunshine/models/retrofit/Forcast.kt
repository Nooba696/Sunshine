package com.extnds.learning.sunshine.models.retrofit

data class Forcast(val id: Long?) {
    val city: Location? = null
    val list: List<ForcastData>? = null
}