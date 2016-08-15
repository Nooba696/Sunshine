package com.genericapp.extnds.sunshine.Settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log
import com.genericapp.extnds.sunshine.R
import com.genericapp.extnds.sunshine.Ui.MainActivity

/**
 * Created by Nooba(PratickRoy) on 30-07-2016.
 */

class SettingsFragment() : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object{
        const val TAG = "SettingsFragment"
    }
    interface SettingsFragmentCallback{
        fun onPrefsChanged()
    }
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.d(TAG,"CHANGED")
        (activity as SettingsFragmentCallback).onPrefsChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }


}