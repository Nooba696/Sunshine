package com.extnds.learning.sunshine.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.extnds.learning.sunshine.R
import com.extnds.learning.sunshine.models.sugarORM.Forcast
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.action_bar.*

class MainActivity : AppCompatActivity(), WeatherListAdapter.WeatherListAdapterCallback {

    override fun openDetailsFragment(forcastId: Long?) {

        val activeFragmentResourceID: Int
        if (resources.getBoolean(R.bool.has_two_panes)) {
            Log.d(LOG_TAG, "Details Fragment Initialized as a part of the Two Panes")
            activeFragmentResourceID = R.id.detail_fragment
        } else {
            Log.d(LOG_TAG, "Details Fragment Initialized replacing the Main Fragment")
            activeFragmentResourceID = R.id.main_fragment
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(activeFragmentResourceID, supportFragmentManager.findFragmentByTag("${DetailsFragment.TAG}($forcastId)") as DetailsFragment? ?: DetailsFragment.newInstance(forcastId!!))
        ft.addToBackStack("${DetailsFragment.TAG}($forcastId)")
        ft.commit()
    }

    companion object {

        const val TAG = "MainActivity"

        const val LOG_TAG_BASE = "Sunshine_"
        const val LOG_TAG = LOG_TAG_BASE + TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(action_bar)
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "No SavedInstance : UI initialized, by call to initUi()")
            initUi()
        } else
            Log.d(LOG_TAG, "SavedInstance Present : UI BackStack implemented by System")
    }

    private fun initUi() {

        Log.d(LOG_TAG, "Main Fragment Initialized")
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_fragment, supportFragmentManager.findFragmentByTag(MainFragment.TAG) as MainFragment? ?: MainFragment())
        ft.addToBackStack(MainFragment.TAG)
        ft.commit()
        if (resources.getBoolean(R.bool.has_two_panes) && SugarRecord.findById(Forcast::class.java, 1) != null) {
            Log.d(LOG_TAG, "This is a Tablet, And Database Record is Found, So Showing The Two Panes")
            openDetailsFragment(1)
        }

    }


}
