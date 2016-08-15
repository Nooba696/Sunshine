package com.genericapp.extnds.sunshine.Settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by Nooba(PratickRoy) on 30-07-2016.
 */


class SettingsActivity() : AppCompatActivity(), SettingsFragment.SettingsFragmentCallback {

    companion object {
        const val HAS_PREFS_CHANGED_KEY = "hasPrefsChanged"
    }

    var hasPrefsChanged = false
    override fun onPrefsChanged() {
        hasPrefsChanged = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(HAS_PREFS_CHANGED_KEY, hasPrefsChanged)
        setResult(RESULT_OK, intent)
        finish()
    }
}
