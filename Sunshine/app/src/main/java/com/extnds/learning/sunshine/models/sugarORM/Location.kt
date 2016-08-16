package com.extnds.learning.sunshine.models.sugarORM

import com.orm.SugarRecord

class Location() : SugarRecord() {

    var name: String? = null
    var lat: Float? = null
    var lon: Float? = null

    fun getForcasts(): MutableList<Forcast> {
        return SugarRecord.find(Forcast::class.java, "location = ?", "$id")
    }

    fun getLastForcast(): Forcast? {
        val dbForcast = SugarRecord.find(Forcast::class.java, "location = ? and id = ?", "$id", "1")
        if (!dbForcast.isEmpty())
            return dbForcast[0]
        return null
    }

}