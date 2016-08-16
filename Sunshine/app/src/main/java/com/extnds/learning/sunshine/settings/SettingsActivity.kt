package com.extnds.learning.sunshine.settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.extnds.learning.sunshine.ui.MainActivity

class SettingsActivity() : AppCompatActivity(), SettingsFragment.SettingsFragmentCallback {

    companion object {
        const val TAG = "SettingsActivity"
        const val HAS_PREFS_CHANGED_KEY = "hasPrefsChanged"
        const val LOG_TAG = MainActivity.LOG_TAG_BASE + TAG
    }

    var hasPrefsChanged = false
    override fun onPrefsChanged() {
        hasPrefsChanged = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "No data fed to WeatherListAdapter, So nothing to populate")
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    override fun onBackPressed() {
        Log.d(LOG_TAG, "Preference Data is being sent back to Main Fragment hasPrefsChange : $hasPrefsChanged ")
        val intent = Intent()
        intent.putExtra(HAS_PREFS_CHANGED_KEY, hasPrefsChanged)
        setResult(RESULT_OK, intent)
        finish()
    }
}
