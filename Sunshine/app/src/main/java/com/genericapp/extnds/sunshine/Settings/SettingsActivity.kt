package com.genericapp.extnds.sunshine.Settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by Nooba(PratickRoy) on 30-07-2016.
 */


class SettingsActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

}
