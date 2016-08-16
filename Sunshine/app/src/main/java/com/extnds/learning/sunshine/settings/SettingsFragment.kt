package com.extnds.learning.sunshine.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log
import com.extnds.learning.sunshine.R
import com.extnds.learning.sunshine.ui.MainActivity

class SettingsFragment() : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val TAG = "SettingsFragment"
        const val LOG_TAG = MainActivity.LOG_TAG_BASE + TAG
    }

    interface SettingsFragmentCallback {
        fun onPrefsChanged()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.d(LOG_TAG, "Preferences has been Changed")
        (activity as SettingsFragmentCallback).onPrefsChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "Preferences has been Changed")
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        Log.d(LOG_TAG, "preferenceManager registered")
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        Log.d(LOG_TAG, "preferenceManager unregistered")
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }


}