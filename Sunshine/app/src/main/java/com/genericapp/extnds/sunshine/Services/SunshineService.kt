package com.genericapp.extnds.sunshine.Services

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.genericapp.extnds.sunshine.Models.Retrofit.Forcast
import com.genericapp.extnds.sunshine.R
import com.genericapp.extnds.sunshine.Ui.MainActivity
import com.genericapp.extnds.sunshine.Ui.MainFragment
import com.genericapp.extnds.sunshine.Ui.WeatherListAdapter
import com.genericapp.extnds.sunshine.Utils.API.apiInterface
import com.genericapp.extnds.sunshine.Utils.Database.DatabaseUtils
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Nooba(PratickRoy) on 15-08-2016.
 */

class SunshineService() : IntentService(SERVICE_NAME) {

    companion object{
        const val TAG = "SunshineService"
        const val SERVICE_NAME = "Sunshine"

        const val CITY_NAME_KEY = "cityName"
        const val UNITS_KEY = "units"
    }


    override fun onHandleIntent(p0: Intent?) {
        Log.d(DatabaseUtils.TAG, "Service 2")
        val cityName = p0!!.getStringExtra(CITY_NAME_KEY)
        val units = p0.getStringExtra(CITY_NAME_KEY)
        apiInterface.forcastQuery(cityName, units).enqueue(object : Callback<Forcast> {
            override fun onResponse(call: Call<Forcast>, response: Response<Forcast>) {

                if (response.isSuccessful) {
                    DatabaseUtils.ForcastDatabase(this@SunshineService).addForcasts(response.body(),object : DatabaseUtils.ForcastDatabase.ForcastDatabaseCallback{
                        override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.genericapp.extnds.sunshine.Models.SugarORM.Forcast>?)
                        {
                            val mBuilder = NotificationCompat.Builder(this@SunshineService)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Sunshine")
                                    .setContentText("Weather Data Updated")

                            val resultIntent = Intent(this@SunshineService, MainActivity::class.java)

                            val stackBuilder = TaskStackBuilder.create(this@SunshineService)
                            stackBuilder.addParentStack(MainActivity::class.java)
                            stackBuilder.addNextIntent(resultIntent)
                            val resultPendingIntent = stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT)
                            mBuilder.setContentIntent(resultPendingIntent)
                            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val mNotificationId = 1
                            mNotificationManager.notify(mNotificationId, mBuilder.build())
                        }
                    })
                }
            }

            override fun onFailure(call: Call<Forcast>, t: Throwable) {
            }
        })
    }

    class AlarmReceiver() : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d(DatabaseUtils.TAG,"${Date(System.currentTimeMillis()).minutes}")
            val intent = Intent(p0,SunshineService::class.java)
            intent.putExtra(SunshineService.CITY_NAME_KEY,p1!!.getStringExtra(CITY_NAME_KEY))
            intent.putExtra(SunshineService.UNITS_KEY,p1.getStringExtra(CITY_NAME_KEY))
            p0?.startService(intent)
        }

    }


}