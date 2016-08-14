package com.genericapp.extnds.sunshine.Ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.text.format.DateFormat
import android.view.*
import com.genericapp.extnds.sunshine.Models.SugarORM.Forcast
import com.genericapp.extnds.sunshine.R
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.fragment_details.*
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by Nooba(PratickRoy) on 13-08-2016.
 */

class DetailsFragment() : Fragment() {

    companion object {
        const val TAG = "DetailsFragment"

        enum class Argument(val key: String) {
            FORCAST_DATA_ID("forcastDataId")
        }

        fun newInstance(forcastDataId: Long): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = Bundle()
            fragment.arguments.putLong(Argument.FORCAST_DATA_ID.key, forcastDataId)
            return fragment
        }
    }

    var dbForcast by Delegates.notNull<Forcast>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments ?: throw IllegalArgumentException("arguments == null")

        if (!arguments.containsKey(Argument.FORCAST_DATA_ID.key)) {
            throw IllegalArgumentException("placeId == null")
        }

        dbForcast = SugarRecord.findById(Forcast::class.java, arguments.getLong(Argument.FORCAST_DATA_ID.key))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_details, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.details_menu, menu)
        val mShareActionProvider = MenuItemCompat.getActionProvider(menu.findItem(R.id.share)) as ShareActionProvider
        mShareActionProvider.setShareIntent(createShareIntent())
    }

    private fun initUi(){
        val date = Date(dbForcast.date!! * 1000L)
        val currentDate = Date(System.currentTimeMillis())
        if(date.date==currentDate.date)
            day_text_view.text= "Today"
        else if(date.date==currentDate.date)
            day_text_view.text= "Tomorrow"
        else
            day_text_view.text= "${DateFormat.format("EEEE", date)}"
        if(currentDate.hours>6 && currentDate.hours<18)
            weather_type_details_image_view.setImageBitmap(BitmapFactory.decodeByteArray(dbForcast.iconDay, 0, dbForcast.iconDay!!.size))
        else
            weather_type_details_image_view.setImageBitmap(BitmapFactory.decodeByteArray(dbForcast.iconNight, 0, dbForcast.iconNight!!.size))

        month_date_text_view.text = "${DateFormat.format("MMMM", date)} ${date.date}"
        max_temp_details_text_view.text = context.getString(R.string.format_temperature, dbForcast.maxTemp!!)
        min_temp_details_text_view.text = context.getString(R.string.format_temperature, dbForcast.minTemp!!)
        weather_type_text_view.text = "${dbForcast.main}"
        humidity_text_view.text = context.getString(R.string.format_humidity,dbForcast.humidity)
        wind_text_view.text = context.getString(R.string.format_wind,dbForcast.windSpeed)
        pressure_text_view.text = context.getString(R.string.format_pressure,dbForcast.pressure)
    }
    private fun createShareIntent(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        shareIntent.type = "text/plain"
        val date = Date(dbForcast.date!! * 1000L)
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                context.getString(R.string.format_day_month_date, "${DateFormat.format("EEEE", date)}", "${DateFormat.format("MMMM", date)}", date.date))
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