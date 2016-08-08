package com.genericapp.extnds.sunshine.Utils.Database

import com.genericapp.extnds.sunshine.Models.SugarORM.Forcast
import com.genericapp.extnds.sunshine.Models.SugarORM.Location
import com.orm.SugarRecord
import java.util.*

/**
 * Created by Nooba(PratickRoy) on 08-08-2016.
 */
object DatabaseService{
    fun addLocation(apiLoc : com.genericapp.extnds.sunshine.Models.Retrofit.Location?) : Location{
        val dbLoc = Location()
        with(dbLoc){
            id = apiLoc?.id
            name = apiLoc?.name
            lat = apiLoc?.coord?.lat
            lon = apiLoc?.coord?.lon
            save()
        }
        return dbLoc
    }
    fun addForcasts(apiForcast: com.genericapp.extnds.sunshine.Models.Retrofit.Forcast) : MutableList<Forcast>{

        var dbLoc = SugarRecord.findById(Location::class.java,apiForcast.city?.id)
        if(dbLoc==null){
            dbLoc=addLocation(apiForcast.city)
        }
        val dbForcasts = ArrayList<Forcast>()
        for (forcast in apiForcast.list!!){
            val dbForcast = Forcast()
            with(dbForcast){
                location = dbLoc
                weatherContdId = forcast.weather!![0].id
                minTemp = forcast.temp?.min
                windSpeed = forcast.temp?.max
                humidity = forcast.humidity
                maxTemp = forcast.temp?.max
                date = forcast.dt
                pressure = forcast.pressure
                save()
            }
            dbForcasts.add(dbForcast)

        }
        return dbForcasts
    }
}
