package com.extnds.learning.sunshine.utils.database

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.extnds.learning.sunshine.models.sugarORM.Forcast
import com.extnds.learning.sunshine.models.sugarORM.Location
import com.extnds.learning.sunshine.ui.MainActivity
import com.extnds.learning.sunshine.utils.getWeatherContdIcon
import com.orm.SugarRecord
import java.util.*
import kotlin.properties.Delegates

object DatabaseUtils {

    const val TAG = "DatabaseUtils"
    const val LOG_TAG = MainActivity.LOG_TAG_BASE + TAG

    fun addLocation(apiLoc: com.extnds.learning.sunshine.models.retrofit.Location?): Location {

        val dbLoc = Location()
        with(dbLoc) {
            id = apiLoc?.id
            name = apiLoc?.name
            lat = apiLoc?.coord?.lat
            lon = apiLoc?.coord?.lon
            AsyncTask.execute {
                Log.d(LOG_TAG, "Location Data Saved with Id ${save()}")
            }
        }
        return dbLoc
    }

    class RetentionedDatabaseServices() : Fragment() {

        var dbForcasts: MutableList<Forcast>? = null
        var forcastCity: String? = null

        companion object {
            const val TAG = "RetentionedDatabaseServices"
            fun getRetentionedFragmentInstance(activity: MainActivity?): RetentionedDatabaseServices {
                var df = activity?.supportFragmentManager?.findFragmentByTag(TAG) as RetentionedDatabaseServices?
                if (df == null) {
                    df = RetentionedDatabaseServices()
                    activity?.supportFragmentManager?.beginTransaction()
                    ?.add(df, TAG)
                    ?.addToBackStack(RetentionedDatabaseServices.TAG)
                    ?.commit()
                }
                return df
            }
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            Log.d(TAG, "called")
            super.onCreate(savedInstanceState)
            retainInstance = true


        }

        fun addForcasts(context: Context?, apiForcast: com.extnds.learning.sunshine.models.retrofit.Forcast, forcastDatabaseCallback: ForcastDatabase.ForcastDatabaseCallback) {
            Log.d(LOG_TAG, "Retentioned addForcast Initiated")
            if (forcastCity.equals(apiForcast.city?.name) == false && context != null) {
                Log.d(LOG_TAG, "New Data, Needs to be saved to Headless fragment")
                forcastCity = apiForcast.city?.name
                ForcastDatabase(context).addForcastsWithRetention(apiForcast, forcastDatabaseCallback, object : ForcastDatabase.ForcastDatabaseCallbackRetention {
                    override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.extnds.learning.sunshine.models.sugarORM.Forcast>?) {
                        Log.d(LOG_TAG, "Data saved to Headless fragment")
                        this@RetentionedDatabaseServices.dbForcasts = dbForcasts
                    }
                })
            } else {
                Log.d(LOG_TAG, "Data Already There, No Need to save in Headless fragment")
                forcastDatabaseCallback.onDatabaseProperlySaved(dbForcasts)
            }
        }
    }

    class ForcastDatabase(val context: Context) : AsyncTask<com.extnds.learning.sunshine.models.retrofit.Forcast, Void, MutableList<Forcast>>() {

        interface ForcastDatabaseCallback {
            fun onDatabaseProperlySaved(dbForcasts: MutableList<Forcast>?)
        }

        interface ForcastDatabaseCallbackRetention {
            fun onDatabaseProperlySaved(dbForcasts: MutableList<Forcast>?)
        }

        fun addForcasts(apiForcast: com.extnds.learning.sunshine.models.retrofit.Forcast, forcastDatabaseCallback: ForcastDatabaseCallback) {

            Log.d(LOG_TAG, "addForcast Executed")
            listener = forcastDatabaseCallback
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apiForcast)
        }

        fun addForcastsWithRetention(apiForcast: com.extnds.learning.sunshine.models.retrofit.Forcast, forcastDatabaseCallback: ForcastDatabaseCallback, forcastDatabaseCallbackRetention: ForcastDatabaseCallbackRetention) {

            Log.d(LOG_TAG, "Retentioned addForcast Executed")
            listener = forcastDatabaseCallback
            listenerRet = forcastDatabaseCallbackRetention
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apiForcast)
        }

        private var listener  by Delegates.notNull<ForcastDatabaseCallback>()
        private var listenerRet: ForcastDatabaseCallbackRetention? = null

        override fun doInBackground(vararg params: com.extnds.learning.sunshine.models.retrofit.Forcast): MutableList<Forcast>? {

            val apiForcast = params[0]
            var dbLoc = SugarRecord.findById(Location::class.java, apiForcast.city?.id)
            if (dbLoc == null) {
                dbLoc = DatabaseUtils.addLocation(apiForcast.city)
            } else {
                val dbForcasts = dbLoc.getForcasts()
                for (forcast in dbForcasts) {
                    Log.d(LOG_TAG, "Forcast Data Deleted with Id ${forcast.id}")
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
                    iconDay = context.getWeatherContdIcon(forcast.weather[0].icon!!.substring(0, 2) + "d")
                    iconNight = context.getWeatherContdIcon(forcast.weather[0].icon!!.substring(0, 2) + "n")
                    minTemp = forcast.temp?.min
                    windSpeed = forcast.speed
                    humidity = forcast.humidity
                    maxTemp = forcast.temp?.max
                    date = forcast.dt
                    dateFetched = System.currentTimeMillis()
                    pressure = forcast.pressure
                    Log.d(DatabaseUtils.LOG_TAG, "Forcast Data Saved with Id ${save()}")
                }
                dbForcasts.add(dbForcast)
            }
            return dbForcasts
        }

        override fun onPostExecute(dbForcasts: MutableList<Forcast>?) {
            listener.onDatabaseProperlySaved(dbForcasts)
            listenerRet?.onDatabaseProperlySaved(dbForcasts)

        }
    }
}


