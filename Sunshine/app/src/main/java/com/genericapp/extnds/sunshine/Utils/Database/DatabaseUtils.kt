package com.genericapp.extnds.sunshine.Utils.Database

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.genericapp.extnds.sunshine.Models.SugarORM.Forcast
import com.genericapp.extnds.sunshine.Models.SugarORM.Location
import com.genericapp.extnds.sunshine.Ui.MainActivity
import com.genericapp.extnds.sunshine.Utils.getWeatherContdIcon
import com.orm.SugarRecord
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Nooba(PratickRoy) on 08-08-2016.
 */

object DatabaseUtils {

    const val TAG = "DatabaseUtils"

    var mForcastDatabase: ForcastDatabase? = null
    fun addLocation(apiLoc: com.genericapp.extnds.sunshine.Models.Retrofit.Location?): Location {

        val dbLoc = Location()
        with(dbLoc) {
            id = apiLoc?.id
            name = apiLoc?.name
            lat = apiLoc?.coord?.lat
            lon = apiLoc?.coord?.lon
            AsyncTask.execute {
                Log.d(TAG, "Location Data Saved with Id ${save()}")
            }
        }
        return dbLoc
    }

    class RetentionedDatabaseServices() : Fragment() {


        companion object {
            const val TAG = "RetentionedDatabaseServices"
            fun getRetentionedFragmentInstance(activity: MainActivity?, fragmentResourceID: Int): RetentionedDatabaseServices {
                var df = activity?.supportFragmentManager?.findFragmentByTag(RetentionedDatabaseServices.TAG) as RetentionedDatabaseServices?
                if (df == null) {
                    df = RetentionedDatabaseServices()
                    val ft = activity?.supportFragmentManager?.beginTransaction()
                    ft?.add(df, TAG)
                    ft?.addToBackStack(RetentionedDatabaseServices.TAG)
                    ft?.commit()
                }
                return df
            }
        }

        var dbForcasts: MutableList<Forcast>? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            Log.d(TAG, "called")
            super.onCreate(savedInstanceState)
            setRetainInstance(true)
        }

        fun addForcasts(context: Context?, apiForcast: com.genericapp.extnds.sunshine.Models.Retrofit.Forcast, forcastDatabaseCallback: ForcastDatabase.ForcastDatabaseCallback) {
            Log.d(TAG, "${dbForcasts}")
            if (mForcastDatabase == null && context != null) {
                mForcastDatabase = ForcastDatabase(context)
                ForcastDatabase(context).addForcastsWithRetention(apiForcast, forcastDatabaseCallback, object : ForcastDatabase.ForcastDatabaseCallbackRetention {
                    override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.genericapp.extnds.sunshine.Models.SugarORM.Forcast>?) {
                        this@RetentionedDatabaseServices.dbForcasts = dbForcasts
                    }
                })
            } else
                forcastDatabaseCallback.onDatabaseProperlySaved(dbForcasts)
        }
    }

    class ForcastDatabase(val context: Context) : AsyncTask<com.genericapp.extnds.sunshine.Models.Retrofit.Forcast, Void, MutableList<Forcast>>() {

        interface ForcastDatabaseCallback {
            fun onDatabaseProperlySaved(dbForcasts: MutableList<Forcast>?)
        }

        interface ForcastDatabaseCallbackRetention {
            fun onDatabaseProperlySaved(dbForcasts: MutableList<Forcast>?)
        }

        fun addForcasts(apiForcast: com.genericapp.extnds.sunshine.Models.Retrofit.Forcast, forcastDatabaseCallback: ForcastDatabaseCallback) {

            listener = forcastDatabaseCallback
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apiForcast)
        }

        fun addForcastsWithRetention(apiForcast: com.genericapp.extnds.sunshine.Models.Retrofit.Forcast, forcastDatabaseCallback: ForcastDatabaseCallback, forcastDatabaseCallbackRetention: ForcastDatabaseCallbackRetention) {

            listener = forcastDatabaseCallback
            listenerRet = forcastDatabaseCallbackRetention
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apiForcast)
        }

        private var listener  by Delegates.notNull<ForcastDatabaseCallback>()
        private var listenerRet: ForcastDatabaseCallbackRetention? = null

        override fun doInBackground(vararg params: com.genericapp.extnds.sunshine.Models.Retrofit.Forcast): MutableList<Forcast>? {

            val apiForcast = params[0]
            var dbLoc = SugarRecord.findById(Location::class.java, apiForcast.city?.id)
            if (dbLoc == null) {
                dbLoc = DatabaseUtils.addLocation(apiForcast.city)
            } else {
                val dbForcasts = dbLoc.getForcasts()
                for (forcast in dbForcasts) {
                    Log.d(TAG, "DELETED")
                    forcast.delete()
                }
            }
            val dbForcasts = ArrayList<Forcast>()


            for ((index, forcast) in apiForcast.list!!.withIndex()) {
                val dbForcast = Forcast()
                with(dbForcast) {
                    id = index.toLong() + 1
                    location = dbLoc
                    main = forcast.weather!![0].main
                    iconDay = context.getWeatherContdIcon(forcast.weather!![0].icon!!.substring(0, 2) + "d")
                    iconNight = context.getWeatherContdIcon(forcast.weather!![0].icon!!.substring(0, 2) + "n")
                    minTemp = forcast.temp?.min
                    windSpeed = forcast.temp?.max
                    humidity = forcast.humidity
                    maxTemp = forcast.temp?.max
                    date = forcast.dt
                    pressure = forcast.pressure
                    Log.d(DatabaseUtils.TAG, "Forcast Data Saved with Id ${save()}")
                }
                dbForcasts.add(dbForcast)
            }
            return dbForcasts
        }

        override fun onPostExecute(dbForcasts: MutableList<Forcast>?) {
            // your stuff
            Log.d(TAG, "Executed")
            listener.onDatabaseProperlySaved(dbForcasts)
            listenerRet?.onDatabaseProperlySaved(dbForcasts)

        }
    }
}


