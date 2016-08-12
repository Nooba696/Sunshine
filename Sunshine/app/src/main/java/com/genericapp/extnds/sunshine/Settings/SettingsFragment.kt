package com.genericapp.extnds.sunshine.Settings

import android.os.Bundle
import android.preference.PreferenceFragment
import com.genericapp.extnds.sunshine.R

/**
 * Created by Nooba(PratickRoy) on 30-07-2016.
 */

class SettingsFragment() : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    //override fun onCreateView(inflater: LayoutInflater, container : ViewGroup,savedInstanceState : Bundle) : View
    //{
    //    return inflater.inflate(R.layout.fragment_settings, container, false)
    //}

}