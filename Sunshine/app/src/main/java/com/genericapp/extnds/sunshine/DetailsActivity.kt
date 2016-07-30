package com.genericapp.extnds.sunshine

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.genericapp.extnds.sunshine.Settings.SettingsActivity
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.list_item.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        day.text = intent.getStringExtra(WeatherListAdapter.DAY_TAG)
        weather_type.text = intent.getStringExtra(WeatherListAdapter.WEATHER_TAG)
        temperature.text = intent.getStringExtra(WeatherListAdapter.TEMPERATURE_TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId)
        {
            R.id.settings -> {

                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }

        }

    }
}
