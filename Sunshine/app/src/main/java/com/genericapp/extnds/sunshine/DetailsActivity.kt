package com.genericapp.extnds.sunshine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.list_item.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item)


        day.text = intent.getStringExtra(WeatherListAdapter.DAY_TAG)
        weather_type.text = intent.getStringExtra(WeatherListAdapter.WEATHER_TAG)
        temperature.text = intent.getStringExtra(WeatherListAdapter.TEMPERATURE_TAG)
    }
}
