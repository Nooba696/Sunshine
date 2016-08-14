package com.genericapp.extnds.sunshine.Ui

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.genericapp.extnds.sunshine.Settings.SettingsActivity
import com.genericapp.extnds.sunshine.Utils.API.apiService
import com.genericapp.extnds.sunshine.Utils.Database.DatabaseServices
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Nooba(PratickRoy) on 13-08-2016.
 */

class MainFragment() : Fragment() {

    companion object {
        const val TAG = "MainFragment"

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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                startActivity(Intent(context, SettingsActivity::class.java))
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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    fun getForcast() {
        progress_bar.visibility = View.VISIBLE
        sunshine_main_weather_list.adapter = null

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        val cityName = sharedPrefs.getString(
                resources.getString(R.string.location_preference_key),
                resources.getString(R.string.location_preference_value))
        val units = sharedPrefs.getString(
                resources.getString(R.string.units_preference_key),
                resources.getString(R.string.units_preference_value))

        sunshine_main_weather_list.layoutManager = LinearLayoutManager(context)
        sunshine_main_weather_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST))


        if (isFetchNecessary(cityName)) {
            apiService.forcastQuery(cityName, units).enqueue(object : Callback<Forcast> {
                override fun onResponse(call: Call<Forcast>, response: Response<Forcast>) {

                    if (response.isSuccessful) {

                        /*DatabaseServices.ForcastDatabase(context).addForcasts(response.body(), object : DatabaseServices.ForcastDatabase.ForcastDatabaseCallback{
                            override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.genericapp.extnds.sunshine.Models.SugarORM.Forcast>?) {
                                try {
                                    progress_bar.visibility = View.GONE
                                    sunshine_main_weather_list.adapter = WeatherListAdapter(context, forcastList = dbForcasts)
                                    Toast.makeText(context, DATA_FETCH_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show()
                                }
                                catch (e : Exception){}
                            }
                        })*/

                        DatabaseServices.RetentionedDatabaseServices.getRetentionedFragmentInstance(activity as? MainActivity,R.id.main_fragment).addForcasts(context,response.body(), object : DatabaseServices.ForcastDatabase.ForcastDatabaseCallback{
                            override fun onDatabaseProperlySaved(dbForcasts: MutableList<com.genericapp.extnds.sunshine.Models.SugarORM.Forcast>?) {

                                try {
                                    progress_bar.visibility = View.GONE
                                    sunshine_main_weather_list.adapter = WeatherListAdapter(context, dbForcasts)
                                    Toast.makeText(context, DATA_FETCH_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show()
                                    Log.d(TAG,"HERE")
                                    if(resources.getBoolean(R.bool.has_two_panes)){
                                        (activity as MainActivity).openDetailsFragment(1)
                                    }
                                }
                                catch (e : Exception){}
                                    //forcasts = dbForcasts
                                    //Log.d(TAG,"NULL")
                                //}

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
        } else {
            unsuccessfulFetch(cityName, true)
        }
    }

    private fun isFetchNecessary(cityName: String): Boolean {

        val dbLoc = SugarRecord.find(Location::class.java, "name = ?", cityName)
        Log.d(TAG,"EMPTY ${dbLoc.isEmpty()}")
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