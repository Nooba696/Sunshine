package com.genericapp.extnds.sunshine.Models.SugarORM

import com.orm.SugarRecord

/**
 * Created by Nooba(PratickRoy) on 08-08-2016.
 */

class Forcast : SugarRecord() {

    var location: Location? = null

    var main: String? = null
    var iconDay: ByteArray? = null
    var iconNight: ByteArray? = null
    var minTemp: Float? = null
    var windSpeed: Float? = null
    var humidity: Long? = null
    var maxTemp: Float? = null
    var date: Long? = null
    var pressure: Float? = null
}

