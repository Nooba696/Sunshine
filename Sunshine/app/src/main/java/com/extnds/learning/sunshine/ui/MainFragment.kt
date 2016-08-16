package com.extnds.learning.sunshine.ui

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
import android.util.Log
import android.view.*
import android.widget.Toast
import com.extnds.learning.sunshine.R
import com.extnds.learning.sunshine.models.retrofit.Forcast
import com.extnds.learning.sunshine.models.sugarORM.Location
import com.extnds.learning.sunshine.services.SunshineService
import com.extnds.learning.sunshine.settings.SettingsActivity
import com.extnds.learning.sunshine.utils.DividerItemDecoration
import com.extnds.learning.sunshine.utils.api.apiInterface
import com.extnds.learning.sunshine.utils.database.DatabaseUtils
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.properties.Delegates

class MainFragment() : Fragment() {

    companion object {
        const val TAG = "MainFragment"
        const val LOG_TAG = MainActivity.LOG_TAG_BASE + TAG

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
        Log.d(LOG_TAG, "Menu Created For MainFragment")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.refresh -> {
                Log.d(LOG_TAG, "Data is Being Refreshed")
                getForcast()
                return true
            }
            R.id.settings -> {

                Log.d(LOG_TAG, "App Settings is Being Displayed")
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
                if (intent.resolveActivity(context.packageManager) != null) {
                    Log.d(LOG_TAG, "Map is Being Displayed")
                    startActivity(intent)
                } else
                    Log.d(LOG_TAG, "Map is not being displayed.No app to run Maps")
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
                Log.d(LOG_TAG, "SettingsActivity Data properly Received")
                val hasPrefsChanged = data.getBooleanExtra(SettingsActivity.HAS_PREFS_CHANGED_KEY, false)
                if (hasPrefsChanged) {
                    Log.d(LOG_TAG, "Settings have Changed, Refreshing Data")
                    getPrefs()
                    getForcast()
                } else
                    Log.d(LOG_TAG, "Setting haven't changed, No need to refresh Data")
            }
        }
    }


    fun getPrefs() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)


        cityName = sharedPrefs.getString(
                resources.getString(R.string.location_preference_key),
                resources.getString(R.string.location_preference_value)).trim()
        units = sharedPrefs.getString(
                resources.getString(R.string.units_preference_key),
                resources.getString(R.string.units_preference_value))
        Log.d(LOG_TAG, "Get Prefs Called [$cityName,$units]")
    }

    fun setUpdateWeatherDataAlarm() {

        Log.d(LOG_TAG, "Weather Data Update Alarm Set")
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val alarmIntent = Intent(activity, SunshineService.AlarmReceiver::class.java)
        alarmIntent.putExtra(SunshineService.CITY_NAME_KEY, cityName)
        alarmIntent.putExtra(SunshineService.UNITS_KEY, units)

        val pi = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)

    }

    fun initializeSunshineMainWeatherList() {

        Log.d(LOG_TAG, "Sunshine List Initialized")
        sunshine_main_weather_list.adapter = null
        sunshine_main_weather_list.layoutManager = LinearLayoutManager(context)
        sunshine_main_weather_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST))
    }

    fun getForcast() {

        Log.d(LOG_TAG, "Forcast is Being Fetched")
        if (progress_bar.visibility == View.GONE)
            progress_bar.visibility = View.VISIBLE
        if (sunshine_main_weather_list.adapter != null)
            sunshine_main_weather_list.adapter = null

        if (!isFetchNecessary(cityName)) {
            databaseFetch(cityName, true)
            return
        }

        Log.d(LOG_TAG, "Fetching Data For [$cityName,$units]")
        apiInterface?.forcastQuery(cityName, units)?.enqueue(object : Callback<Forcast> {
            override fun onResponse(call: Call<Forcast>, response: Response<Forcast>) {
                Log.d(LOG_TAG, "Data Fetch Response Received")
                if (response.isSuccessful) {

                    Log.d(LOG_TAG, "Data Fetch Response is Successful")
                    DatabaseUtils.RetentionedDatabaseServices.
                            getRetentionedFragmentInstance(activity as? MainActivity).
                            addForcasts(context, response.body(), object : DatabaseUtils.ForcastDatabase.ForcastDatabaseCallback {

                                override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.extnds.learning.sunshine.models.sugarORM.Forcast>?) {

                                    Log.d(LOG_TAG, "DataBase data successfully received inside MainFragment")
                                    try {
                                        progress_bar.visibility = View.GONE
                                        sunshine_main_weather_list.adapter = WeatherListAdapter(context, dbForcasts)
                                        Toast.makeText(context, DATA_FETCH_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show()
                                        Log.d(LOG_TAG, "When Data was received, Activity was preset, so populated")
                                        if (resources.getBoolean(R.bool.has_two_panes)) {
                                            (activity as MainActivity).openDetailsFragment(1)
                                        }
                                    } catch (e: Exception) {
                                        Log.d(LOG_TAG, "When Data was received, Activity was being recreated, so not populated")
                                    }

                                }
                            })
                } else {
                    Log.d(LOG_TAG, "Data Fetch Response is Unsuccessful")
                    databaseFetch(cityName)
                }
            }

            override fun onFailure(call: Call<Forcast>, t: Throwable) {
                Log.d(LOG_TAG, "Data Fetch Request Failed")
                databaseFetch(cityName)
            }
        })
    }

    private fun isFetchNecessary(cityName: String): Boolean {

        val dbLoc = SugarRecord.find(Location::class.java, "name = ?", cityName)
        if (!dbLoc.isEmpty()) {

            val lastForcastDate = Calendar.getInstance()
            lastForcastDate.timeInMillis=dbLoc[0].getLastForcast()?.dateFetched ?: 0L

            val currentDate = Calendar.getInstance()
            currentDate.timeInMillis=System.currentTimeMillis()

            if (lastForcastDate.get(Calendar.DATE) == currentDate.get(Calendar.DATE) && lastForcastDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) && lastForcastDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                Log.d(LOG_TAG, "Fetching not necessary | " +
                        "Last Fetch Date:[${lastForcastDate.get(Calendar.DATE)}/${lastForcastDate.get(Calendar.MONTH)}/${lastForcastDate.get(Calendar.YEAR)}]" +
                        "Current Date:[${currentDate.get(Calendar.DATE)}/${currentDate.get(Calendar.MONTH)}/${currentDate.get(Calendar.YEAR)}]")
                return false
            }
            Log.d(LOG_TAG, "Fetching is necessary | " +
                    "Last Fetch Date:[${lastForcastDate.get(Calendar.DATE)}/${lastForcastDate.get(Calendar.MONTH)}/${lastForcastDate.get(Calendar.YEAR)}]" +
                    "Current Date:[${currentDate.get(Calendar.DATE)}/${currentDate.get(Calendar.MONTH)}/${currentDate.get(Calendar.YEAR)}]")
            return true
        }
        Log.d(LOG_TAG, "Fetching is necessary | No Data exists for this request")
        return true
    }

    private fun databaseFetch(cityName: String, isFailurePurposeful: Boolean = false) {
        progress_bar.visibility = View.GONE
        val dbLoc = SugarRecord.find(Location::class.java, "name = ?", cityName)
        if (dbLoc.isEmpty()) {
            Log.d(LOG_TAG, "Data Could not be fetched, No Cache to display")
            Toast.makeText(context, "$DATA_FETCH_UNSUCCESSFUL_MESSAGE $DATABASE_FETCH_UNSUCCESSFUL_MESSAGE", Toast.LENGTH_SHORT).show()
        } else {
            sunshine_main_weather_list.adapter = WeatherListAdapter(context, dbLoc[0].getForcasts())
            if (isFailurePurposeful) {
                Log.d(LOG_TAG, "Data Need not be fetched, Cached Data is fresh enough")
                Toast.makeText(context, "$DATA_FETCH_UNSUCCESSFUL_MESSAGE $DATABASE_FETCH_IS_DEFAULT_MESSAGE", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(LOG_TAG, "Data Could not be fetched, Making do with Cache Data")
                Toast.makeText(context, "$DATA_FETCH_UNSUCCESSFUL_MESSAGE $DATABASE_FETCH_SUCCESSFUL_MESSAGE", Toast.LENGTH_SHORT).show()
            }
        }
    }


}