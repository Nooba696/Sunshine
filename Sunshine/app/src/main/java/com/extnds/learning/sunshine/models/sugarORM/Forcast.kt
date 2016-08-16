package com.extnds.learning.sunshine.models.sugarORM

import com.orm.SugarRecord

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
    var dateFetched: Long? = null
    var pressure: Float? = null
}

