package com.genericapp.extnds.sunshine.Models.SugarORM

import com.orm.SugarRecord

/**
 * Created by Nooba(PratickRoy) on 08-08-2016.
 */

class Location() : SugarRecord() {

    var name : String? = null
    var lat : Float? = null
    var lon : Float? = null

    fun getForcasts() : List<Forcast>{
        return SugarRecord.find(Forcast::class.java, "location = ?", "$id")
    }

}