package com.genericapp.extnds.sunshine.Ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.Toast
import com.genericapp.extnds.mozillarecpro.DividerItemDecoration
import com.genericapp.extnds.sunshine.Models.Retrofit.Forcast
import com.genericapp.extnds.sunshine.Models.SugarORM.Location
import com.genericapp.extnds.sunshine.R
import com.genericapp.extnds.sunshine.Services.SunshineService
import com.genericapp.extnds.sunshine.Settings.SettingsActivity
import com.genericapp.extnds.sunshine.Utils.API.apiInterface
import com.genericapp.extnds.sunshine.Utils.Database.DatabaseUtils
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Nooba(PratickRoy) on 13-08-2016.
 */

class MainFragment() : Fragment() {

    companion object {
        const val TAG = "MainFragment"

        const val REQUEST_APP_PREFERENCE = 1
        const val DATA_FETCH_SUCCESSFUL_MESSAGE = "Data Successfully Fetched"
        const val DATA_FETCH_UNSUCCESSFUL_MESSAGE = "Data Not Fetched."

        const val DATABASE_FETCH_IS_DEFAULT_MESSAGE = " It hasn't changed."
        const val DATABASE_FETCH_UNSUCCESSFUL_MESSAGE = " Try Again."
        const val DATABASE_FETCH_SUCCESSFUL_MESSAGE = " Showing Cache."
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)
        return v
    }

    var cityName by Delegates.notNull<String>()
    var units by Delegates.notNull<String>()
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPrefs()
        setUpdateWeatherDataAlarm()
        initializeSunshineMainWeatherList()
        getForcast()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.refresh -> {
                getForcast()
                return true
            }
            R.id.settings -> {

                startActivityForResult(Intent(context, SettingsActivity::class.java), REQUEST_APP_PREFERENCE)
                return true
            }
            R.id.map -> {
                val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                val cityName = sharedPrefs.getString(
                        resources.getString(R.string.location_preference_key),
                        resources.getString(R.string.location_preference_value))
                val geoLocation = Uri.parse("geo:0,0?").buildUpon()
                        .appendQueryParameter("q", cityName)
                        .build()

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = geoLocation
                if (intent.resolveActivity(context.packageManager) != null)
                    startActivity(intent)
                else
                    Log.d(MainActivity.TAG, "No app to run Maps")
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_APP_PREFERENCE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val hasPrefsChanged = data.getBooleanExtra(SettingsActivity.HAS_PREFS_CHANGED_KEY, false)
                if (hasPrefsChanged) {
                    getPrefs()
                    getForcast()
                }
            }
        }
    }


    fun getPrefs() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        cityName = sharedPrefs.getString(
                resources.getString(R.string.location_preference_key),
                resources.getString(R.string.location_preference_value))
        units = sharedPrefs.getString(
                resources.getString(R.string.units_preference_key),
                resources.getString(R.string.units_preference_value))
    }

    fun setUpdateWeatherDataAlarm() {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val alarmIntent = Intent(activity, SunshineService.AlarmReceiver::class.java)
        alarmIntent.putExtra(SunshineService.CITY_NAME_KEY, cityName)
        alarmIntent.putExtra(SunshineService.UNITS_KEY, units)

        val pi = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d(DatabaseUtils.TAG, "${Date(System.currentTimeMillis()).minutes}")
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)

    }

    fun initializeSunshineMainWeatherList() {
        sunshine_main_weather_list.adapter = null
        sunshine_main_weather_list.layoutManager = LinearLayoutManager(context)
        sunshine_main_weather_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST))
    }

    fun getForcast() {

        if (progress_bar.visibility == View.GONE)
            progress_bar.visibility = View.VISIBLE
        if (sunshine_main_weather_list.adapter != null)
            sunshine_main_weather_list.adapter = null

        if (!isFetchNecessary(cityName)) {
            unsuccessfulFetch(cityName, true)
            return
        }

        apiInterface.forcastQuery(cityName, units).enqueue(object : Callback<Forcast> {
            override fun onResponse(call: Call<Forcast>, response: Response<Forcast>) {

                if (response.isSuccessful) {

                    DatabaseUtils.RetentionedDatabaseServices.
                            getRetentionedFragmentInstance(activity as? MainActivity, R.id.main_fragment).
                            addForcasts(context, response.body(), object : DatabaseUtils.ForcastDatabase.ForcastDatabaseCallback {

                                override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.genericapp.extnds.sunshine.Models.SugarORM.Forcast>?) {

                                    try {
                                        progress_bar.visibility = View.GONE
                                        if (dbForcasts == null) {
                                            val dbLoc = SugarRecord.find(Location::class.java, "name = ?", cityName)
                                            sunshine_main_weather_list.adapter = WeatherListAdapter(context, dbLoc[0].getForcastsForLastFetch())
                                        } else
                                            sunshine_main_weather_list.adapter = WeatherListAdapter(context, dbForcasts)
                                        Toast.makeText(context, DATA_FETCH_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show()
                                        Log.d(TAG, "HERE")
                                        if (resources.getBoolean(R.bool.has_two_panes)) {
                                            (activity as MainActivity).openDetailsFragment(1)
                                        }
                                    } catch (e: Exception) {
                                    }

                                }
                            })
                } else {
                    unsuccessfulFetch(cityName)
                }
            }

            override fun onFailure(call: Call<Forcast>, t: Throwable) {
                unsuccessfulFetch(cityName)
            }
        })
    }

    private fun isFetchNecessary(cityName: String): Boolean {

        val dbLoc = SugarRecord.find(Location::class.java, "name = ?", cityName)
        Log.d(TAG, "EMPTY ${dbLoc.isEmpty()}")
        if (!dbLoc.isEmpty()) {
            val lastForcastDate = Date(dbLoc[0].getLastForcast()?.date?.times(1000L) ?: 0L)
            val currentDate = Date(System.currentTimeMillis())

            Log.d(TAG, "${DateFormat.format("yyyy", lastForcastDate) as String},${DateFormat.format("MMM", lastForcastDate) as String} ${lastForcastDate.date}")
            Log.d(TAG, "${DateFormat.format("yyyy", currentDate) as String},${DateFormat.format("MMM", currentDate) as String} ${currentDate.date}")

            Log.d(TAG, "${dbLoc[0].getLastForcast()?.date?.times(1000L) ?: 0L}")
            Log.d(TAG, "${System.currentTimeMillis()}")

            if (lastForcastDate.date == currentDate.date && lastForcastDate.month == currentDate.month && lastForcastDate.year == currentDate.year)
                return false
            return true
        }
        return true
    }

    private fun unsuccessfulFetch(cityName: String, isFailurePurposeful: Boolean = false) {
        progress_bar.visibility = View.GONE
        val dbLoc = SugarRecord.find(Location::class.java, "name = ?", cityName)
        if (dbLoc.isEmpty())
            Toast.makeText(context, "${DATA_FETCH_UNSUCCESSFUL_MESSAGE} ${DATABASE_FETCH_UNSUCCESSFUL_MESSAGE}", Toast.LENGTH_SHORT).show()
        else {
            sunshine_main_weather_list.adapter = WeatherListAdapter(context, dbLoc[0].getForcastsForLastFetch())
            if (isFailurePurposeful)
                Toast.makeText(context, "${DATA_FETCH_UNSUCCESSFUL_MESSAGE} ${DATABASE_FETCH_IS_DEFAULT_MESSAGE}", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, "${DATA_FETCH_UNSUCCESSFUL_MESSAGE} ${DATABASE_FETCH_SUCCESSFUL_MESSAGE}", Toast.LENGTH_SHORT).show()
        }
    }


}