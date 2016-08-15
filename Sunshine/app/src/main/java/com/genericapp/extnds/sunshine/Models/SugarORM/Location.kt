package com.genericapp.extnds.sunshine.Models.SugarORM

import com.orm.SugarRecord

/**
 * Created by Nooba(PratickRoy) on 08-08-2016.
 */

class Location() : SugarRecord() {

    var name: String? = null
    var lat: Float? = null
    var lon: Float? = null

    fun getForcasts(): MutableList<Forcast> {
        return SugarRecord.find(Forcast::class.java, "location = ?", "$id")
    }

    fun getForcastsForLastFetch(): MutableList<Forcast> {
        return SugarRecord.find(Forcast::class.java, "location = ? and id < ?", "$id", "8")
    }

    fun getLastForcast(): Forcast? {
        val dbForcast = SugarRecord.find(Forcast::class.java, "location = ? and id = ?", "$id", "1")
        if (!dbForcast.isEmpty())
            return dbForcast[0]
        return null
    }

}