package com.genericapp.extnds.sunshine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.genericapp.extnds.mozillarecpro.DividerItemDecoration
import com.genericapp.extnds.sunshine.Models.Forcast
import com.genericapp.extnds.sunshine.Utils.apiService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    companion object
    {
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiService.forcastQuery("94043").enqueue(object : Callback<Forcast> {
            override fun onResponse(call: Call<Forcast>, response: Response<Forcast>) {

                progress_bar.visibility = View.GONE
                sunshine_main_weather_list.adapter=WeatherListAdapter(this@MainActivity,response.body().list!!)
                sunshine_main_weather_list.layoutManager = LinearLayoutManager(this@MainActivity)
                sunshine_main_weather_list.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL_LIST))
            }

            override fun onFailure(call: Call<Forcast>, t: Throwable) {

            }
        })
    }
}
