package com.genericapp.extnds.sunshine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sunshine_main_weather_list.adapter=WeatherListAdapter(this,null)
        sunshine_main_weather_list.layoutManager = LinearLayoutManager(this)
    }
}
