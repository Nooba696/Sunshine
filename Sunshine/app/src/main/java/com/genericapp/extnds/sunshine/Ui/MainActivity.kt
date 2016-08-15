package com.genericapp.extnds.sunshine.Ui

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.genericapp.extnds.sunshine.Models.Retrofit.ForcastData
import com.genericapp.extnds.sunshine.Models.SugarORM.Forcast
import com.genericapp.extnds.sunshine.R
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.action_bar.*


class MainActivity : AppCompatActivity(), WeatherListAdapter.WeatherListAdapterCallback {

    override fun openDetailsFragment(forcastId: Long?) {

        val activeFragmentResourceID: Int
        if (resources.getBoolean(R.bool.has_two_panes))
            activeFragmentResourceID = R.id.detail_fragment
        else
            activeFragmentResourceID = R.id.main_fragment

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(activeFragmentResourceID, supportFragmentManager.findFragmentByTag("${DetailsFragment.TAG}(${forcastId})") as DetailsFragment? ?: DetailsFragment.newInstance(forcastId!!))
        ft.addToBackStack("${DetailsFragment.TAG}(${forcastId})")
        ft.commit()
    }

    override fun openDetailsFragment(forcastData: ForcastData) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(action_bar)
        if(savedInstanceState==null) {
            initUi()
        }

    }

    private fun initUi() {

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_fragment, supportFragmentManager.findFragmentByTag(MainFragment.TAG) as MainFragment? ?: MainFragment())
        ft.addToBackStack(MainFragment.TAG)
        ft.commit()
        if (resources.getBoolean(R.bool.has_two_panes) && SugarRecord.findById(Forcast::class.java, 1) != null) {

            openDetailsFragment(1)
        }

    }




}
