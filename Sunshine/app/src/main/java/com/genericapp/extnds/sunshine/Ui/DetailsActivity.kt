package com.genericapp.extnds.sunshine.Ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.view.Menu
import android.view.MenuItem
import com.genericapp.extnds.sunshine.R
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.list_item.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //day.text = intent.getStringExtra(WeatherListAdapter.DAY_TAG)
        //weather_type.text = intent.getStringExtra(WeatherListAdapter.WEATHER_TAG)
        //temperature.text = intent.getStringExtra(WeatherListAdapter.TEMPERATURE_TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.details_menu, menu)
        val mShareActionProvider = MenuItemCompat.getActionProvider(menu.findItem(R.id.share)) as ShareActionProvider
        mShareActionProvider.setShareIntent(createShareIntent())
        return true
    }

    private fun createShareIntent(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        shareIntent.type = "text/plain"
        //shareIntent.putExtra(Intent.EXTRA_TEXT, "${day.text} ${weather_type.text} ${temperature.text} #Sunshine")
        return shareIntent
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
