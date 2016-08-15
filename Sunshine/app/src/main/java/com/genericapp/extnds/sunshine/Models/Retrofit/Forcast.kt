package com.genericapp.extnds.sunshine.Models.Retrofit

/**
 * Created by Nooba(PratickRoy) on 29-07-2016.
 */

data class Forcast(val id: Long?) {
    val city: Location? = null
    val list: List<ForcastData>? = null
}